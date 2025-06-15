package com.kingkit.billing_service.support.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kingkit.billing_service.application.port.out.RedisPlanCachePort;
import com.kingkit.billing_service.domain.payment.PaymentFailure;
import com.kingkit.billing_service.domain.payment.PaymentHistory;
import com.kingkit.billing_service.domain.payment.PaymentMethod;
import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.domain.payment.repository.PaymentFailureRepository;
import com.kingkit.billing_service.domain.payment.repository.PaymentHistoryRepository;
import com.kingkit.billing_service.domain.payment.repository.PaymentMethodRepository;
import com.kingkit.billing_service.domain.subscription.ScheduledBillingTriggerLog;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.SubscriptionPlan;
import com.kingkit.billing_service.domain.subscription.SubscriptionStatus;
import com.kingkit.billing_service.domain.subscription.TriggerResult;
import com.kingkit.billing_service.domain.subscription.repository.ScheduledBillingTriggerLogRepository;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionPlanRepository;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionRepository;
import com.kingkit.billing_service.dto.request.PrepareBillingRequest;
import com.kingkit.billing_service.integration.common.RedisTestSupport;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FixtureFactory {

    private static final String DEFAULT_DESCRIPTION = "테스트 결제";
    private static final String DEFAULT_PG_RESPONSE = "raw-response";

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentFailureRepository paymentFailureRepository;
    private final ScheduledBillingTriggerLogRepository triggerLogRepository;
    private final RedisPlanCachePort redisPlanCachePort;

    /* 🔧 유틸 */
    private String generatePlanCode(String prefix) {
        return prefix + "-" + System.currentTimeMillis();
    }

    private String generateBillingKey(Long userId) {
        return "billing-" + userId;
    }

    private LocalDateTime now() {
        return LocalDateTime.now();
    }

    private LocalDateTime nowPlusDays(int days) {
        return now().plusDays(days);
    }

    /* 📦 요금제 */
    @Transactional
    public SubscriptionPlan createPlan(String planCode, long price, int durationDays) {
        return planRepository.save(SubscriptionPlan.builder()
            .planCode(planCode)
            .name("테스트 요금제")
            .price(price)
            .durationDays(durationDays)
            .build());
    }

    @Transactional
    public SubscriptionPlan createUniqueTestPlan() {
        return createPlan(generatePlanCode("test-plan"), 10900, 30);
    }

    /* 💳 결제 수단 */
    @Transactional
    public PaymentMethod createPaymentMethod(Long userId, String billingKey) {
       return paymentMethodRepository.save(buildPaymentMethod(userId, billingKey, true));
    }

    @Transactional
    public PaymentMethod createInactivePaymentMethod(Long userId, String billingKey) {
        return paymentMethodRepository.save(buildPaymentMethod(userId, billingKey, false));
    }

    private PaymentMethod buildPaymentMethod(Long userId, String billingKey, boolean active) {
        return PaymentMethod.builder()
            .userId(userId)
            .billingKey(billingKey)
            .cardCompany(active ? "테스트카드" : "기존카드")
            .cardNumberMasked(active ? "****-****-****-1234" : "****-****-****-9999")
            .registeredAt(now().minusDays(active ? 0 : 10))
            .isActive(active)
            .build();
    }

    /* 🔄 구독 */
    @Transactional
    public Subscription createSubscription(Long userId, SubscriptionPlan plan, PaymentMethod method, SubscriptionStatus status, LocalDateTime nextBillingAt) {
        return subscriptionRepository.save(Subscription.builder()
            .userId(userId)
            .plan(plan)
            .paymentMethod(method)
            .startedAt(now().minusDays(1))
            .nextBillingAt(nextBillingAt)
            .status(status)
            .build());
    }

    @Transactional
    public Subscription createActiveSubscription(Long userId) {
        return createActiveSubscriptionWithNextBilling(userId, nowPlusDays(29));
    }

    @Transactional
    public Subscription createActiveSubscriptionWithNextBilling(Long userId, LocalDateTime billingDate) {
        var plan = createUniqueTestPlan();
        var method = createPaymentMethod(userId, generateBillingKey(userId));
        return createSubscription(userId, plan, method, SubscriptionStatus.ACTIVE, billingDate);
    }

    @Transactional
    public Subscription createActiveSubscription(Long userId, String billingKey) {

        // (1) 플랜 준비 – 기존 로직 재사용
        SubscriptionPlan plan = createUniqueTestPlan();

        // (2) 결제수단을 '지정된' billingKey 로 저장
        PaymentMethod method = createPaymentMethod(userId, billingKey);

        // (3) 구독 저장 – 기존 메서드 활용
        return createSubscription(
                userId,
                plan,
                method,
                SubscriptionStatus.ACTIVE,
                nowPlusDays(29));
    }

    @Transactional
    public Subscription createSubscriptionWithoutPlan(Long userId) {
        var method = createPaymentMethod(userId, generateBillingKey(userId));
        return createSubscription(userId, null, method, SubscriptionStatus.ACTIVE, nowPlusDays(30));
    }


    @Transactional
    public Subscription createSubscriptionWithoutPayment(Long userId) {
        var plan = createUniqueTestPlan();
        return createSubscription(userId, plan, null, SubscriptionStatus.ACTIVE, nowPlusDays(30));
    }

    @Transactional
    public Subscription createSubscriptionWithoutBillingKey(Long userId, String planCode) {
        return createSubscriptionWithoutBillingKey(userId, planCode, nowPlusDays(30));
    }

    @Transactional
    public Subscription createSubscriptionWithoutBillingKey(Long userId, String planCode, LocalDateTime nextBillingAt) {
        var plan = planRepository.findByPlanCode(planCode).orElseThrow();
        var dummy = createInactivePaymentMethod(userId, "dummy-" + userId);
        return createSubscription(userId, plan, dummy, SubscriptionStatus.ACTIVE, nextBillingAt);
    }

    @Transactional
    public Subscription setupWebhookDefaultSubscription(Long userId, String planCode) {
        var plan = planRepository.findByPlanCode(planCode)
            .orElseGet(() -> createPlan(planCode, 10900, 30));
        var dummy = createInactivePaymentMethod(userId, "dummy-" + userId);
        return createSubscription(userId, plan, dummy, SubscriptionStatus.ACTIVE, nowPlusDays(30));
    }

    /* 💰 결제 이력 */
    @Transactional
    public PaymentHistory createPaymentHistory(Subscription subscription, String paymentKey, String orderId, long amount, boolean success) {
        return paymentHistoryRepository.save(PaymentHistory.builder()
            .subscription(subscription)
            .paymentKey(paymentKey)
            .orderId(orderId)
            .paidAt(now())
            .status(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
            .amount(amount)
            .description(DEFAULT_DESCRIPTION)
            .pgResponseRaw("{...}")
            .retryCount(0)
            .build());
    }

    @Transactional
    public PaymentHistory createPaymentHistory(Subscription subscription, String orderId, PaymentStatus status, LocalDateTime paidAt, Long amount, String description) {
        return paymentHistoryRepository.save(PaymentHistory.builder()
            .subscription(subscription)
            .paymentKey("pay-" + orderId)
            .orderId(orderId)
            .paidAt(paidAt != null ? paidAt : now())
            .status(status)
            .amount(amount)
            .description(description)
            .pgResponseRaw(DEFAULT_PG_RESPONSE)
            .retryCount(0)
            .build());
    }

    /* ❌ 결제 실패 */
    @Transactional
    public PaymentFailure createPaymentFailure(Subscription subscription, String reason, int retryCount) {
        return buildPaymentFailure(subscription, reason, retryCount, false);
    }

    @Transactional
    public PaymentFailure createPaymentFailure(Subscription subscription, boolean resolved, int retryCount) {
        return buildPaymentFailure(subscription, "테스트 실패", retryCount, resolved);
    }

    private PaymentFailure buildPaymentFailure(Subscription subscription, String reason, int retryCount, boolean resolved) {
        return paymentFailureRepository.save(PaymentFailure.builder()
            .subscription(subscription)
            .failedAt(now())
            .reason(reason)
            .retryCount(retryCount)
            .retryScheduledAt(now().plusMinutes(10))
            .resolved(resolved)
            .build());
    }

    /* ⏱ 트리거 로그 */
    @Transactional
    public ScheduledBillingTriggerLog createTriggerLog(Long userId, TriggerResult result, String failureReason) {
        return triggerLogRepository.save(ScheduledBillingTriggerLog.builder()
            .triggerDate(LocalDate.now())
            .userId(userId)
            .result(result)
            .failureReason(failureReason)
            .triggeredAt(now())
            .build());
    }

    /* 기타 유틸 */
    @Transactional
    public void deactivateAllPaymentMethods(Long userId) {
        List<PaymentMethod> methods = paymentMethodRepository.findAllByUserId(userId);
        methods.forEach(PaymentMethod::deactivate);
        paymentMethodRepository.saveAll(methods);
    }

    @Transactional
    public void deactivateAndSave(PaymentMethod method) {
        method.deactivate();
        paymentMethodRepository.save(method);
    }

    @Transactional
    public void clearAllTestData() {
        paymentFailureRepository.deleteAllInBatch();
        paymentHistoryRepository.deleteAllInBatch();
        subscriptionRepository.deleteAllInBatch();
        paymentMethodRepository.deleteAllInBatch();
        planRepository.deleteAllInBatch();
        triggerLogRepository.deleteAllInBatch();
    }

    public SubscriptionPlan createSubscriptionPlan(String planCode, Long price) {
        return planRepository.findByPlanCode(planCode)
            .orElseGet(() -> planRepository.save(
                SubscriptionPlan.builder()
                    .planCode(planCode)
                    .name("Test Plan")
                    .price(price)
                    .durationDays(30)
                    .isActive(true)
                    .build()
            ));
    }

    public PrepareBillingRequest buildRequest(String planCode) {
        return new PrepareBillingRequest(
                planCode,
                "https://success.com",
                "https://fail.com"
        );
    }

    @Transactional
    public void preparePlanCacheFor(String orderId, String planCode) {
        // DB에 저장
        createSubscriptionPlan(planCode, 10900L);

        // Redis 캐시에 저장
        redisPlanCachePort.store(orderId, planCode);
    }


}
