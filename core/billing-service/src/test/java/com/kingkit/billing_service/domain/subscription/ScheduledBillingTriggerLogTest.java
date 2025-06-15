package com.kingkit.billing_service.domain.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduledBillingTriggerLogTest {

    @Test
    @DisplayName("success() 메서드는 SUCCESS 상태의 트리거 로그를 생성한다")
    void createSuccessLog() {
        LocalDate date = LocalDate.of(2025, 5, 27);
        ScheduledBillingTriggerLog log = ScheduledBillingTriggerLog.success(123L, date);

        assertThat(log.getUserId()).isEqualTo(123L);
        assertThat(log.getTriggerDate()).isEqualTo(date);
        assertThat(log.getResult()).isEqualTo(TriggerResult.SUCCESS);
        assertThat(log.getFailureReason()).isNull();
        assertThat(log.getTriggeredAt()).isNotNull();
    }

    @Test
    @DisplayName("failure() 메서드는 FAILED 상태의 트리거 로그를 생성한다")
    void createFailureLog() {
        LocalDate date = LocalDate.now();
        String reason = "카드 한도 초과";

        ScheduledBillingTriggerLog log = ScheduledBillingTriggerLog.failure(456L, date, reason);

        assertThat(log.getUserId()).isEqualTo(456L);
        assertThat(log.getTriggerDate()).isEqualTo(date);
        assertThat(log.getResult()).isEqualTo(TriggerResult.FAILED);
        assertThat(log.getFailureReason()).isEqualTo(reason);
        assertThat(log.getTriggeredAt()).isNotNull();
    }
}
