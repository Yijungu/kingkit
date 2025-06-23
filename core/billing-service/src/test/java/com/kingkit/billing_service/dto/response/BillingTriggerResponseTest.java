package com.kingkit.billing_service.dto.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BillingTriggerResponseTest {

    @Test
    void builderWorks() {
        LocalDateTime now = LocalDateTime.now();
        BillingTriggerResponse res = BillingTriggerResponse.builder()
                .subscriptionId(1L)
                .orderId("o")
                .paymentKey("p")
                .status("SUCCESS")
                .paidAt(now)
                .message("ok")
                .build();
        assertThat(res.getSubscriptionId()).isEqualTo(1L);
        assertThat(res.getPaidAt()).isEqualTo(now);
        assertThat(res.getStatus()).isEqualTo("SUCCESS");
        assertThat(res.getMessage()).isEqualTo("ok");
    }
}
