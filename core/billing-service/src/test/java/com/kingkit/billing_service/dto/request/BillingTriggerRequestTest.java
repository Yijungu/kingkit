package com.kingkit.billing_service.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class BillingTriggerRequestTest {
    @Test
    @DisplayName("subscriptionId, userId 존재 체크")
    void flags() {
        BillingTriggerRequest req = BillingTriggerRequest.builder().build();
        assertThat(req.hasSubscriptionId()).isFalse();
        assertThat(req.hasUserId()).isFalse();

        BillingTriggerRequest req2 = BillingTriggerRequest.builder().subscriptionId(1L).build();
        assertThat(req2.hasSubscriptionId()).isTrue();
        assertThat(req2.hasUserId()).isFalse();

        BillingTriggerRequest req3 = BillingTriggerRequest.builder().userId(3L).build();
        assertThat(req3.hasUserId()).isTrue();
    }
}
