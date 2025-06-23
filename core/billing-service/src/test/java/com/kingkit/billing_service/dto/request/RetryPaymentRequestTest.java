package com.kingkit.billing_service.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RetryPaymentRequestTest {
    @Test
    @DisplayName("getter 동작 확인")
    void getters() {
        RetryPaymentRequest req = new RetryPaymentRequest(1L, "order", 500L);
        assertThat(req.getSubscriptionId()).isEqualTo(1L);
        assertThat(req.getOrderId()).isEqualTo("order");
        assertThat(req.getAmount()).isEqualTo(500L);
    }
}
