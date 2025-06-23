package com.kingkit.billing_service.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class AdminBillingTriggerRequestTest {

    @Test
    @DisplayName("targetDate 와 userIds 여부 체크")
    void checkFlags() {
        AdminBillingTriggerRequest empty = AdminBillingTriggerRequest.builder().build();
        assertThat(empty.hasTargetDate()).isFalse();
        assertThat(empty.hasUserIds()).isFalse();

        AdminBillingTriggerRequest withDate = AdminBillingTriggerRequest.builder()
                .targetDate(LocalDate.now()).build();
        assertThat(withDate.hasTargetDate()).isTrue();
        assertThat(withDate.hasUserIds()).isFalse();

        AdminBillingTriggerRequest withUsers = AdminBillingTriggerRequest.builder()
                .userIds(List.of(1L, 2L)).build();
        assertThat(withUsers.hasUserIds()).isTrue();
    }
}
