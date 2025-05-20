package com.kingkit.billing_service.support;

import com.kingkit.billing_service.domain.payment.*;
import com.kingkit.billing_service.domain.subscription.*;
import com.kingkit.billing_service.domain.subscription.repository.*;
import com.kingkit.billing_service.domain.payment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class FixtureFactory {

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentFailureRepository paymentFailureRepository;
    private final ScheduledBillingTriggerLogRepository triggerLogRepository;

    // 📦 요금제 생성
    @Transactional
    public SubscriptionPlan createPlan(String planCode, long price, int durationDays) {
        return planRepository.save(SubscriptionPlan.builder()
                .planCode(planCode)
                .name("테스트 요금제")
                .price(price)
                .durationDays(durationDays)
                .build());
    }

    // 💳 결제 수단 생성
    @Transactional
    public PaymentMethod createPaymentMethod(Long userId, String billingKey) {
        return paymentMethodRepository.save(PaymentMethod.builder()
                .userId(userId)
                .billingKey(billingKey)
                .cardCompany("국민카드")
                .cardNumberMasked("****-****-****-1234")
                .registeredAt(LocalDateTime.now())
                .isActive(true)
                .build());
    }

    // 🔄 구독 생성
    @Transactional
    public Subscription createSubscription(Long userId, SubscriptionPlan plan, PaymentMethod method, SubscriptionStatus status) {
        return subscriptionRepository.save(Subscription.builder()
                .userId(userId)
                .plan(plan)
                .paymentMethod(method)
                .startedAt(LocalDateTime.now().minusDays(1))
                .nextBillingAt(LocalDateTime.now().plusDays(29))
                .status(status)
                .build());
    }

    @Transactional
    public Subscription createActiveSubscription(Long userId) {
        SubscriptionPlan plan = createPlan("basic-monthly", 10900, 30);
        PaymentMethod method = createPaymentMethod(userId, "billing-" + userId);
        return createSubscription(userId, plan, method, SubscriptionStatus.ACTIVE);
    }

    @Transactional
    public Subscription createSubscriptionWithoutPlan(Long userId) {
        PaymentMethod method = createPaymentMethod(userId, "billing-fail");
        return createSubscription(userId, null, method, SubscriptionStatus.ACTIVE);
    }

    @Transactional
    public Subscription createSubscriptionWithoutPayment(Long userId) {
        SubscriptionPlan plan = createPlan("basic-fail", 10900, 30);
        return createSubscription(userId, plan, null, SubscriptionStatus.ACTIVE);
    }


    // 💰 결제 이력 생성
    @Transactional
    public PaymentHistory createPaymentHistory(Subscription subscription, String paymentKey, String orderId, long amount, boolean success) {
        PaymentStatus status = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        return paymentHistoryRepository.save(PaymentHistory.builder()
                .subscription(subscription)
                .paymentKey(paymentKey)
                .orderId(orderId)
                .paidAt(LocalDateTime.now())
                .status(status)
                .amount(amount)
                .description("테스트 결제")
                .pgResponseRaw("{...}")
                .retryCount(0)
                .build());
    }

    // ❌ 결제 실패 이력 생성
    @Transactional
    public PaymentFailure createPaymentFailure(Subscription subscription, String reason, int retryCount) {
        return paymentFailureRepository.save(PaymentFailure.builder()
                .subscription(subscription)
                .failedAt(LocalDateTime.now())
                .reason(reason)
                .retryCount(retryCount)
                .retryScheduledAt(LocalDateTime.now().plusMinutes(10))
                .resolved(false)
                .build());
    }

    @Transactional
    public PaymentFailure createPaymentFailure(Subscription subscription, boolean resolved, int retryCount) {
        return paymentFailureRepository.save(PaymentFailure.builder()
                .subscription(subscription)
                .failedAt(LocalDateTime.now())
                .reason("테스트 실패") // default reason
                .retryCount(retryCount)
                .retryScheduledAt(LocalDateTime.now().plusMinutes(10))
                .resolved(resolved)
                .build());
    }


    // ⏱ 트리거 로그 생성
    @Transactional
    public ScheduledBillingTriggerLog createTriggerLog(Long userId, TriggerResult result, String failureReason) {
        return triggerLogRepository.save(ScheduledBillingTriggerLog.builder()
                .triggerDate(LocalDate.now())
                .userId(userId)
                .result(result)
                .failureReason(failureReason)
                .triggeredAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public PaymentHistory createPaymentHistory(
        Subscription subscription,
        String orderId,
        PaymentStatus status,
        LocalDateTime paidAt,
        Long amount,
        String description
    ) {
        PaymentHistory history = PaymentHistory.builder()
            .subscription(subscription)
            .paymentKey("pay-" + orderId)
            .orderId(orderId)
            .paidAt(paidAt)
            .status(status)
            .amount(amount)
            .description(description)
            .pgResponseRaw("raw-response")
            .retryCount(0)
            .build();
        return paymentHistoryRepository.save(history);
    }

    public Subscription createSubscriptionWithoutBillingKey(Long userId, String planCode) {
        var plan = planRepository.findByPlanCode(planCode).orElseThrow();
    
        // 👉 Dummy PaymentMethod (billingKey = null or 임시 값)
        PaymentMethod dummy = PaymentMethod.builder()
                .userId(userId)
                .billingKey("dummy-" + userId)
                .cardCompany("테스트카드")
                .cardNumberMasked("****-****-****-0000")
                .registeredAt(LocalDateTime.now())
                .isActive(false) // 비활성화 표시
                .build();
        dummy = paymentMethodRepository.save(dummy);
    
        return createSubscription(userId, plan, dummy, SubscriptionStatus.ACTIVE);
    }

    public Subscription createSubscriptionWithoutBillingKey(Long userId, String planCode, LocalDate nextBillingAt) {
        var plan = planRepository.findByPlanCode(planCode).orElseThrow();
    
        PaymentMethod dummy = PaymentMethod.builder()
                .userId(userId)
                .billingKey("dummy-" + userId)
                .cardCompany("테스트카드")
                .cardNumberMasked("****-****-****-0000")
                .registeredAt(LocalDateTime.now())
                .isActive(false)
                .build();
        dummy = paymentMethodRepository.save(dummy);
    
        Subscription subscription = Subscription.builder()
                .userId(userId)
                .plan(plan)
                .paymentMethod(dummy)
                .status(SubscriptionStatus.ACTIVE)
                .startedAt(LocalDateTime.now().minusDays(1))
                .nextBillingAt(nextBillingAt.atStartOfDay()) // LocalDate → LocalDateTime
                .build();
    
        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription createActiveSubscriptionWithNextBilling(Long userId, LocalDate billingDate) {
        var plan = createPlan("test-plan", 10900L, 30);
        var method = createPaymentMethod(userId, "billing-" + userId);

        Subscription subscription = Subscription.builder()
                .userId(userId)
                .plan(plan)
                .paymentMethod(method)
                .startedAt(LocalDateTime.now().minusDays(1))
                .nextBillingAt(billingDate.atTime(4, 0))  // 강제 주입
                .status(SubscriptionStatus.ACTIVE)
                .build();

        return subscriptionRepository.save(subscription);
    }

    
}
