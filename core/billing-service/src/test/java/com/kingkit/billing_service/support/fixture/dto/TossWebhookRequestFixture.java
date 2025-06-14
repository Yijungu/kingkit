package com.kingkit.billing_service.support.fixture.dto;

import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import com.kingkit.billing_service.dto.request.TossWebhookRequest.CardInfo;

public class TossWebhookRequestFixture {

    public static TossWebhookRequest subscriptionRegistered(String orderId, String customerKey, String billingKey) {
        return new TossWebhookRequest(
            "SUBSCRIPTION_REGISTERED",
            billingKey,
            customerKey,
            orderId,
            "2025-05-20T15:00:00Z",
            new CardInfo("국민카드", "****-****-****-1234")
        );
    }

    public static TossWebhookRequest paymentSuccess(String orderId) {
        return new TossWebhookRequest(
            "SUBSCRIPTION_PAYMENT_SUCCESS",
            "billing-success-key",
            "user-1001",
            orderId,
            "2025-05-20T15:10:00Z",
            null
        );
    }

    public static TossWebhookRequest paymentFailed(String orderId, String reason) {
        return new TossWebhookRequest(
            "SUBSCRIPTION_PAYMENT_FAILED",
            "billing-fail-key",
            "user-1002",
            orderId,
            "2025-05-20T15:15:00Z",
            null
        );
    }

    public static TossWebhookRequest unknownEvent(String eventType) {
        return new TossWebhookRequest(
            eventType,
            "billing-unknown",
            "user-9999",
            "order-unknown",
            "2025-05-20T15:30:00Z",
            null
        );
    }

    public static TossWebhookRequest registeredEvent(Long userId, String orderId, String billingKey) {
    return new TossWebhookRequest(
        "SUBSCRIPTION_REGISTERED",
        billingKey,
        "user-" + userId,
        orderId,
        "2025-05-20T15:00:00Z",
        new CardInfo("국민카드", "****-****-****-1234")
    );
}
}