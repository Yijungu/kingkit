package com.kingkit.billing_service.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ManualBillingRequestDtoTest {
    @Test
    @DisplayName("of 팩토리 메서드 동작")
    void ofFactory() {
        ManualBillingRequestDto dto = ManualBillingRequestDto.of(1L, "bill", "order", 1000L, "desc");
        assertThat(dto.getUserId()).isEqualTo(1L);
        assertThat(dto.getBillingKey()).isEqualTo("bill");
        assertThat(dto.getOrderId()).isEqualTo("order");
        assertThat(dto.getAmount()).isEqualTo(1000L);
        assertThat(dto.getDescription()).isEqualTo("desc");
    }
}
