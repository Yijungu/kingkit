package com.kingkit.billing_service.client.dto;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentResultTest {

    @Test
    void isSuccessTrueOnlyWhenStatusSuccessAndPaymentKeyPresent() {
        PaymentResult result = PaymentResult.successWithPaymentKey("pkey");
        assertThat(result.isSuccess()).isTrue();
        PaymentResult fail = PaymentResult.fail(HttpStatus.BAD_REQUEST, "bad");
        assertThat(fail.isSuccess()).isFalse();
    }

    @Test
    void isSuccessFalseWhenPaymentKeyMissing() {
        PaymentResult noKey = PaymentResult.successWithCheckoutUrl("url");
        assertThat(noKey.isSuccess()).isFalse();

        PaymentResult missing = PaymentResult.builder()
                .status(PaymentResult.Status.SUCCESS)
                .httpStatus(HttpStatus.OK)
                .build();
        assertThat(missing.isSuccess()).isFalse();
    }

    @Test
    void isSuccessFalseWhenStatusFailedEvenWithKey() {
        PaymentResult result = PaymentResult.builder()
                .status(PaymentResult.Status.FAILED)
                .paymentKey("key")
                .httpStatus(HttpStatus.OK)
                .build();

        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void successFactoriesPopulateFields() {
        PaymentResult ok = PaymentResult.successWithPaymentKey("key");
        assertThat(ok.getStatus()).isEqualTo(PaymentResult.Status.SUCCESS);
        assertThat(ok.getPaymentKey()).isEqualTo("key");
        assertThat(ok.getPaidAt()).isNotNull();

        PaymentResult url = PaymentResult.successWithCheckoutUrl("url");
        assertThat(url.getCheckoutUrl()).isEqualTo("url");

        PaymentResult health = PaymentResult.healthCheck(HttpStatus.OK, true);
        assertThat(health.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(health.isSlow()).isTrue();
    }

    @Test
    void failureFactoryPopulatesReason() {
        PaymentResult fail = PaymentResult.fail(HttpStatus.NOT_FOUND, "reason");
        assertThat(fail.getStatus()).isEqualTo(PaymentResult.Status.FAILED);
        assertThat(fail.getReason()).isEqualTo("reason");
        assertThat(fail.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
