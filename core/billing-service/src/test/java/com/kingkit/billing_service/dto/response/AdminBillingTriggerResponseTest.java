package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdminBillingTriggerResponseTest {

    @Test
    void ofCalculatesFailureCount() {
        BillingTriggerDetail d = BillingTriggerDetail.builder()
                .subscriptionId(1L)
                .userId(2L)
                .status(PaymentStatus.FAILED)
                .reason("err")
                .build();
        AdminBillingTriggerResponse r = AdminBillingTriggerResponse.of(List.of(d), 3);
        assertThat(r.getSuccessCount()).isEqualTo(3);
        assertThat(r.getFailureCount()).isEqualTo(1);
        assertThat(r.getFailures()).containsExactly(d);
    }
}
