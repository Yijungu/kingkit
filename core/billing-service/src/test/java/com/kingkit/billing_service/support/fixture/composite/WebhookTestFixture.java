package com.kingkit.billing_service.support.fixture.composite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.domain.payment.PaymentFailure;
import com.kingkit.billing_service.domain.payment.PaymentMethod;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.SubscriptionPlan;
import com.kingkit.billing_service.domain.subscription.SubscriptionStatus;
import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import com.kingkit.billing_service.dto.request.TossWebhookRequest.CardInfo;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
public class WebhookTestFixture {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String DEFAULT_APPROVED_AT = "2024-06-01T15:30:00Z";

    public static final String TOSS_SECRET_KEY = "test-secret-key"; // 테스트용 시크릿 키

    // ✅ 기본 카드 정보
    public static CardInfo cardInfo() {
        return new CardInfo("국민카드", "****-****-****-1234");
    }

    public static TossWebhookRequest registeredEvent(Long userId, String orderId, String billingKey) {
        return new TossWebhookRequest(
                "SUBSCRIPTION_REGISTERED",
                billingKey,
                "user-" + userId,
                orderId,
                DEFAULT_APPROVED_AT,
                cardInfo()
        );
    }

    public static TossWebhookRequest subscriptionRegisteredRequest(Long userId) {
        return registeredEvent(userId, "order-" + userId, "billing-" + userId);
    }

    public static TossWebhookRequest paymentSuccessEvent(Long userId, String orderId) {
        return new TossWebhookRequest(
                "SUBSCRIPTION_PAYMENT_SUCCESS",
                "billing-" + userId,
                "user-" + userId,
                orderId,
                DEFAULT_APPROVED_AT,
                null
        );
    }

    public static TossWebhookRequest paymentFailedEvent(Long userId, String orderId) {
        return new TossWebhookRequest(
                "SUBSCRIPTION_PAYMENT_FAILED",
                "billing-" + userId,
                "user-" + userId,
                orderId,
                DEFAULT_APPROVED_AT,
                null
        );
    }

    public static TossWebhookRequest subscriptionRegisteredWebhook() {
        return new TossWebhookRequest(
            "SUBSCRIPTION_REGISTERED",
            "billing-123",
            "user-1001",
            "order-001",
            nowIso(),
            new CardInfo("국민카드", "****-****-****-1234")
        );
    }

    public static TossWebhookRequest paymentSuccessWebhook() {
        return new TossWebhookRequest(
            "SUBSCRIPTION_PAYMENT_SUCCESS",
            "billing-123",
            "user-1001",
            "order-001",
            nowIso(),
            null
        );
    }

    public static TossWebhookRequest paymentFailedWebhook() {
        return new TossWebhookRequest(
            "SUBSCRIPTION_PAYMENT_FAILED",
            "billing-123",
            "user-1001",
            "order-001",
            nowIso(),
            null
        );
    }

    private static String nowIso() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public static PaymentMethod activePaymentMethod(Long userId) {
        return paymentMethod(userId, "billing-" + userId);
    }

    public static PaymentMethod paymentMethod(Long userId, String billingKey) {
        return PaymentMethod.builder()
                .userId(userId)
                .billingKey(billingKey)
                .cardCompany("국민카드")
                .cardNumberMasked("****-****-****-1234")
                .isActive(true)
                .registeredAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    public static Subscription subscription(Long userId) {
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .planCode("test-plan")
                .price(10900L)
                .durationDays(30)
                .build();

        PaymentMethod method = activePaymentMethod(userId);
        return subscription(userId, plan, method);
    }

    public static Subscription subscription(Long userId, SubscriptionPlan plan, PaymentMethod method) {
        return Subscription.builder()
                .userId(userId)
                .plan(plan)
                .paymentMethod(method)
                .status(SubscriptionStatus.ACTIVE)
                .startedAt(LocalDateTime.now().minusDays(1))
                .nextBillingAt(LocalDateTime.now().plusDays(29))
                .build();
    }

    public static PaymentFailure unresolvedFailure(Subscription subscription, int retryCount) {
        return PaymentFailure.builder()
                .subscription(subscription)
                .retryCount(retryCount)
                .resolved(false)
                .failedAt(LocalDateTime.now().minusDays(1))
                .retryScheduledAt(LocalDateTime.now().plusHours(1))
                .reason("테스트 실패")
                .build();
    }

    // ✅ 시그니처 생성 (정상 서명)
    public static String validSignature(String rawBody) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(TOSS_SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = hmac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC signature", e);
        }
    }

    // ✅ 잘못된 시그니처 반환
    public static String invalidSignature() {
        return "invalid-signature";
    }

    // ✅ TossWebhookRequest → JSON 직렬화
    public static String rawBody(TossWebhookRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize request", e);
        }
    }

    public static TossWebhookRequest subscriptionRegisteredEvent(Long userId) {
    return new TossWebhookRequest(
        "SUBSCRIPTION_REGISTERED",
        "billing-" + userId,
        "user-" + userId,
        "order-" + userId,
        DEFAULT_APPROVED_AT,
        cardInfo()
    );
}

}
