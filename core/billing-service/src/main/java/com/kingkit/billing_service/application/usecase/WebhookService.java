package com.kingkit.billing_service.application.usecase;

import com.kingkit.billing_service.application.port.out.RedisPlanCachePort;
import com.kingkit.billing_service.domain.payment.*;
import com.kingkit.billing_service.domain.payment.repository.*;
import com.kingkit.billing_service.domain.subscription.*;
import com.kingkit.billing_service.domain.subscription.repository.*;
import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import com.kingkit.billing_service.dto.request.TossWebhookRequest.CardInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentFailureRepository paymentFailureRepository;
    private final RedisPlanCachePort redisPlanCachePort;

    @Transactional
    public void handleWebhookEvent(TossWebhookRequest request) {
        switch (request.eventType()) {
            case "SUBSCRIPTION_REGISTERED" -> handleSubscriptionRegistered(request);
            case "SUBSCRIPTION_PAYMENT_SUCCESS" -> handlePaymentSuccess(request);
            case "SUBSCRIPTION_PAYMENT_FAILED" -> handlePaymentFailed(request);
            default -> log.warn("🔔 알 수 없는 이벤트 수신: {}", request.eventType());
        }
    }

    @Transactional
    public void handleSubscriptionRegistered(TossWebhookRequest request) {
        Long userId = extractUserId(request.customerKey());
        String orderId = request.orderId();
        String billingKey = request.billingKey();
        CardInfo cardInfo = request.cardInfo();

        // Redis에서 planId 조회 실패 시 → 무시
        String planCode = redisPlanCachePort.findPlanId(orderId);
        if (planCode == null) {
            log.warn("🚫 planId 없음. orderId: {}", orderId);
            return;
        }

        // 기존 결제 수단 비활성화
        paymentMethodRepository.findAllByUserId(userId)
                .forEach(PaymentMethod::deactivate);

        // 결제 수단 저장
        PaymentMethod newMethod = paymentMethodRepository.save(PaymentMethod.builder()
                .userId(userId)
                .billingKey(billingKey)
                .cardCompany(cardInfo.company())
                .cardNumberMasked(cardInfo.numberMasked())
                .registeredAt(LocalDateTime.now())
                .isActive(true)
                .build());

        // 요금제 조회 및 구독 생성
        SubscriptionPlan plan = subscriptionPlanRepository.findByPlanCode(planCode)
                .orElseThrow(() -> new IllegalStateException("요금제 없음: " + planCode));

        subscriptionRepository.save(Subscription.builder()
                .userId(userId)
                .plan(plan)
                .paymentMethod(newMethod)
                .startedAt(LocalDateTime.now())
                .nextBillingAt(LocalDateTime.now().plusDays(plan.getDurationDays()))
                .status(SubscriptionStatus.ACTIVE)
                .build());
    }

    @Transactional
    public void handlePaymentSuccess(TossWebhookRequest req) {
        String orderId = req.orderId();
        if (paymentHistoryRepository.existsByOrderId(orderId)) {
            log.info("✅ 중복 결제 수신 orderId={}", orderId);
            return;
        }

        Long userId = extractUserId(req.customerKey());
        Subscription sub = subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElse(null);
        if (sub == null) {
            log.warn("🚫 ACTIVE 구독 없음 userId={}, orderId={}", userId, orderId);
            return;
        }

        paymentHistoryRepository.save(
            PaymentHistory.success(
                sub,
                req.billingKey(),
                orderId,
                sub.getPlan().getPrice(),
                "정기 결제 성공",
                "{}",             // raw PG JSON
                0
            )
        );
        sub.renewNextBilling();
    }


    @Transactional
    public void handlePaymentFailed(TossWebhookRequest req) {
        String orderId = req.orderId();
        if (paymentHistoryRepository.existsByOrderId(orderId)) {
            log.info("✅ 중복 실패 수신 orderId={}", orderId);
            return;
        }

        Long userId = extractUserId(req.customerKey());
        Subscription sub = subscriptionRepository
                .findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElse(null);
        if (sub == null) {
            log.warn("🚫 ACTIVE 구독 없음 userId={}, orderId={}", userId, orderId);
            return;
        }

        paymentHistoryRepository.save(
            PaymentHistory.failed(
                sub,
                req.billingKey(),
                orderId,
                sub.getPlan().getPrice(),
                "정기 결제 실패",
                "{}",
                0
            )
        );

        paymentFailureRepository.save(
            PaymentFailure.builder()
                .subscription(sub)
                .failedAt(LocalDateTime.now())
                .reason("PG사 결제 실패")
                .retryCount(0)
                .retryScheduledAt(LocalDateTime.now().plusHours(1))
                .resolved(false)
                .build()
        );
    }

    private Long extractUserId(String customerKey) {
        try {
            return Long.valueOf(customerKey.replace("user-", ""));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid customerKey: " + customerKey);
        }
    }
}