package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ManualBillingResponseDtoTest {

    @Test
    void successFactory() {
        LocalDateTime now = LocalDateTime.now();
        ManualBillingResponseDto dto = ManualBillingResponseDto.success("pk", "order", now);
        assertThat(dto.getPaymentKey()).isEqualTo("pk");
        assertThat(dto.getOrderId()).isEqualTo("order");
        assertThat(dto.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(dto.getPaidAt()).isEqualTo(now);
    }

    @Test
    void failedFactory() {
        ManualBillingResponseDto dto = ManualBillingResponseDto.failed("order");
        assertThat(dto.getPaymentKey()).isNull();
        assertThat(dto.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(dto.getPaidAt()).isNull();
    }
}
