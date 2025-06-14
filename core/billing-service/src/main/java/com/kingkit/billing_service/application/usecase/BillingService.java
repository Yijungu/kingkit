package com.kingkit.billing_service.application.usecase;

import com.kingkit.billing_service.application.port.out.RedisPlanCachePort;
import com.kingkit.billing_service.client.PaymentClient;
import com.kingkit.billing_service.client.dto.PaymentCommand;
import com.kingkit.billing_service.client.dto.PaymentResult;
import com.kingkit.billing_service.domain.payment.*;
import com.kingkit.billing_service.domain.payment.repository.*;
import com.kingkit.billing_service.domain.subscription.*;
import com.kingkit.billing_service.domain.subscription.repository.*;
import com.kingkit.billing_service.dto.request.*;
import com.kingkit.billing_service.dto.response.*;
import com.kingkit.billing_service.exception.domain.billing.DuplicateOrderIdException;
import com.kingkit.billing_service.exception.domain.billing.InvalidBillingKeyException;
import com.kingkit.billing_service.exception.domain.billing.SubscriptionNotFoundException;
import com.kingkit.billing_service.exception.domain.billing.TossApiException;
import com.kingkit.billing_service.exception.domain.plan.PlanNotFoundException;
import com.kingkit.billing_service.util.OrderIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillingService {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentFailureRepository paymentFailureRepository;

    private final PaymentClient paymentClient;
    private final OrderIdGenerator orderIdGenerator;
    private final RedisPlanCachePort redisPlanCachePort;

    @Transactional
    public PrepareBillingResponse prepareBilling(Long userId, PrepareBillingRequest request) {
        SubscriptionPlan plan = planRepository.findByPlanCode(request.planId())
                .orElseThrow(() -> new PlanNotFoundException(request.planId()));

        String orderId = orderIdGenerator.generate();
        String customerKey = "user-" + userId;

        redisPlanCachePort.store(orderId, request.planId());

        String checkoutUrl = paymentClient.requestCheckoutUrl(
                request.successUrl(), request.failUrl(), customerKey, orderId, plan.getPrice());

        return PrepareBillingResponse.builder()
                .checkoutUrl(checkoutUrl)
                .orderId(orderId)
                .customerKey(customerKey)
                .build();
    }

    @Transactional(readOnly = true)
    public SubscriptionStatusResponseDto getSubscriptionStatus(Long userId) {
        return subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .map(SubscriptionStatusResponseDto::from)
                .orElse(SubscriptionStatusResponseDto.inactive());
    }


    @Transactional
    public ManualBillingResponseDto executeManualBilling(ManualBillingRequestDto request) {
        Long userId = request.getUserId();
        
        PaymentMethod method = paymentMethodRepository.findByUserIdAndBillingKey(userId, request.getBillingKey())
                .orElseThrow(() -> new InvalidBillingKeyException(request.getBillingKey()));


        if (paymentHistoryRepository.existsByOrderId(request.getOrderId())) {
            throw new DuplicateOrderIdException(request.getOrderId());
        }

        PaymentResult result = paymentClient.execute(
            PaymentCommand.billing(
                request.getBillingKey(),
                request.getOrderId(),
                request.getAmount()
            )
        );

        String paymentKey = result.getPaymentKey();
        PaymentStatus status = (paymentKey != null) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        
        Subscription subscription = subscriptionRepository.findByUserIdAndPaymentMethod(userId, method)
        .orElseThrow(() -> new IllegalStateException("해당 userId와 paymentMethod로 구독이 없습니다."));

        
        String safePaymentKey = (paymentKey != null)
                ? paymentKey
                : "fail-" + request.getOrderId() + "-" + UUID.randomUUID();

        PaymentHistory history = PaymentHistory.builder()
            .subscription(subscription) // ✅ not-null로 설정
            .paymentKey(safePaymentKey)
            .orderId(request.getOrderId())
            .paidAt(LocalDateTime.now())
            .status(status)
            .amount(request.getAmount())
            .description(request.getDescription())
            .pgResponseRaw("raw-response")
            .retryCount(0)
            .build();

        paymentHistoryRepository.save(history);

        if (status == PaymentStatus.FAILED) {
            paymentFailureRepository.save(PaymentFailure.builder()
                    .subscription(subscription)
                    .failedAt(LocalDateTime.now())
                    .reason("Toss 결제 실패")
                    .retryCount(0)
                    .retryScheduledAt(LocalDateTime.now().plusHours(1))
                    .resolved(false)
                    .build());
        }

        return ManualBillingResponseDto.builder()
                .paymentKey(paymentKey)
                .orderId(request.getOrderId())
                .paidAt(history.getPaidAt())
                .status(status)
                .build();
    }

    public BillingKeyDeleteResponse deleteBillingKey(Long userId) {
        PaymentMethod method = paymentMethodRepository
            .findByUserIdAndIsActiveTrue(userId)
            .orElseThrow(() ->
                new InvalidBillingKeyException("활성화된 BillingKey가 존재하지 않습니다."));

        String billingKey = method.getBillingKey();

        try {
            paymentClient.deleteBillingKey(billingKey);
        } catch (TossApiException ex) {
            HttpStatus status = ex.getTossStatus();

            if (status == HttpStatus.NOT_FOUND) {
                log.warn("⚠️ [BillingKey] PG에 이미 삭제된 상태 - userId={}, billingKey={}", userId, billingKey);
                // 계속 진행
            } else if (status.is4xxClientError()) {
                log.error("❌ [BillingKey] 사용자 요청 오류 - userId={}, billingKey={}, status={}", userId, billingKey, status);
                throw ex;
            } else {
                log.error("🔥 [BillingKey] Toss 서버 오류 - userId={}, billingKey={}, status={}", userId, billingKey, status);
                throw ex;
            }
        }

        method.deactivate(); // DB 상태를 단일 source of truth로 유지

        return BillingKeyDeleteResponse.builder()
            .billingKey(billingKey)
            .deleted(true)
            .message("BillingKey 삭제 완료 (또는 이미 삭제됨)")
            .build();
    }

    @Transactional
    public SubscriptionCancelResponseDto cancelSubscription(Long userId) {
        Subscription subscription = subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new SubscriptionNotFoundException("해지 가능한 구독이 없습니다."));

        PaymentMethod method = paymentMethodRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new InvalidBillingKeyException("billingKey 없음"));

        try {
            paymentClient.deleteBillingKey(method.getBillingKey());
        } catch (Exception e) {
            throw new TossApiException(HttpStatus.BAD_GATEWAY, "PG사 billingKey 삭제 실패");
        }

        subscription.markCanceled();
        method.deactivate();

        return SubscriptionCancelResponseDto.sample(subscription.getPlan().getName());
    }
}
