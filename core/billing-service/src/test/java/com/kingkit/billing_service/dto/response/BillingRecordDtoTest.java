package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BillingRecordDtoTest {

    @Test
    void builderSetsFields() {
        LocalDateTime now = LocalDateTime.now();
        BillingRecordDto dto = BillingRecordDto.builder()
                .orderId("o")
                .amount(100L)
                .status(PaymentStatus.SUCCESS)
                .paidAt(now)
                .description("desc")
                .build();
        assertThat(dto.getOrderId()).isEqualTo("o");
        assertThat(dto.getPaidAt()).isEqualTo(now);
        assertThat(dto.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }
}
