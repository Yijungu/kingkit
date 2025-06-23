package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RetryPaymentResponseTest {

    @Test
    void successFactories() {
        RetryPaymentResponse r1 = RetryPaymentResponse.success("pkey");
        assertThat(r1.getPaymentKey()).isEqualTo("pkey");
        assertThat(r1.getStatus()).isEqualTo(PaymentStatus.SUCCESS);

        LocalDateTime now = LocalDateTime.now();
        RetryPaymentResponse r2 = RetryPaymentResponse.success("p","o",now);
        assertThat(r2.getOrderId()).isEqualTo("o");
        assertThat(r2.getPaidAt()).isEqualTo(now);
    }

    @Test
    void failedFactory() {
        RetryPaymentResponse r = RetryPaymentResponse.failed("o1");
        assertThat(r.getPaymentKey()).isNull();
        assertThat(r.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(r.getPaidAt()).isNull();
    }

    @Test
    void testFactory() {
        RetryPaymentResponse r = RetryPaymentResponse.test("p","o","SUCCESS","2024-01-01T01:00:00");
        assertThat(r.getPaymentKey()).isEqualTo("p");
        assertThat(r.getPaidAt()).isEqualTo(LocalDateTime.parse("2024-01-01T01:00:00"));
    }
}
