package com.kingkit.billing_service.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionCancelResponseDtoTest {

    @Test
    void sampleCreatesFilledDto() {
        SubscriptionCancelResponseDto dto = SubscriptionCancelResponseDto.sample("plan");
        assertThat(dto.getStatus()).isEqualTo("CANCELED");
        assertThat(dto.getPlanName()).isEqualTo("plan");
        assertThat(dto.getMessage()).contains("해지");
        assertThat(dto.getCancelledAt()).isNotNull();
    }

    @Test
    void simpleCreatesDtoWithMessageOnly() {
        SubscriptionCancelResponseDto dto = SubscriptionCancelResponseDto.simple("bye");
        assertThat(dto.getPlanName()).isNull();
        assertThat(dto.getMessage()).isEqualTo("bye");
    }
}
