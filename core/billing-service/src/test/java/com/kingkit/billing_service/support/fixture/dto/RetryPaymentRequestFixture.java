package com.kingkit.billing_service.support.fixture.dto;

import com.kingkit.billing_service.dto.request.RetryPaymentRequest;

public class RetryPaymentRequestFixture {

    public static RetryPaymentRequest defaultRetryRequest() {
        return new RetryPaymentRequest(1L, "order-2024-001", 10900L);
    }

    public static RetryPaymentRequest custom(Long subscriptionId, String orderId, Long amount) {
        return new RetryPaymentRequest(subscriptionId, orderId, amount);
    }
}
