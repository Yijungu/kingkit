package com.kingkit.billing_service.support.fixture.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kingkit.billing_service.domain.subscription.ScheduledBillingTriggerLog;
import com.kingkit.billing_service.domain.subscription.TriggerResult;

public class ScheduledBillingTriggerLogFixture {

    public static ScheduledBillingTriggerLog success(Long userId, LocalDate date) {
        return ScheduledBillingTriggerLog.builder()
                .userId(userId)
                .triggerDate(date)
                .result(TriggerResult.SUCCESS)
                .triggeredAt(LocalDateTime.now())
                .build();
    }

    public static ScheduledBillingTriggerLog failure(Long userId, LocalDate date, String reason) {
        return ScheduledBillingTriggerLog.builder()
                .userId(userId)
                .triggerDate(date)
                .result(TriggerResult.FAILED)
                .failureReason(reason)
                .triggeredAt(LocalDateTime.now())
                .build();
    }

    public static ScheduledBillingTriggerLog arbitrary(Long userId, LocalDate date, TriggerResult result, String reason) {
        return ScheduledBillingTriggerLog.builder()
                .userId(userId)
                .triggerDate(date)
                .result(result)
                .failureReason(reason)
                .triggeredAt(LocalDateTime.now())
                .build();
    }
}
