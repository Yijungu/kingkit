package com.kingkit.billing_service.support.fixture;

public class WebhookRequestFixture {

    public static String paymentSuccess(Long userId, String orderId, String paymentKey) {
        return """
            {
              "eventType": "SUBSCRIPTION_PAYMENT_SUCCESS",
              "data": {
                "billingKey": "%s",
                "customerKey": "user-%d",
                "orderId": "%s",
                "approvedAt": "2025-05-20T11:00:00+09:00",
                "paymentKey": "%s",
                "amount": 10900,
                "status": "DONE",
                "lastTransactionKey": "txn-001",
                "method": "카드",
                "card": {
                  "company": "토스뱅크카드",
                  "number": "****-****-****-1234"
                }
              }
            }
        """.formatted("billing-" + userId, userId, orderId, paymentKey);
    }

    public static String paymentFailed(Long userId, String orderId, String paymentKey) {
        return """
            {
              "eventType": "SUBSCRIPTION_PAYMENT_FAILED",
              "data": {
                "billingKey": "%s",
                "customerKey": "user-%d",
                "orderId": "%s",
                "approvedAt": "2025-05-20T12:00:00+09:00",
                "paymentKey": "%s",
                "amount": 10900,
                "status": "FAILED",
                "lastTransactionKey": "txn-fail-001",
                "method": "카드",
                "card": {
                  "company": "신한카드",
                  "number": "****-****-****-1111"
                }
              }
            }
        """.formatted("billing-" + userId, userId, orderId, paymentKey);
    }
}
