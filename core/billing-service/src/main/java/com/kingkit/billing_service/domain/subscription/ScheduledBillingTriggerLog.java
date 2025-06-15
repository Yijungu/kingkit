package com.kingkit.billing_service.domain.subscription;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_billing_triggers", indexes = {
    @Index(name = "idx_trigger_date", columnList = "triggerDate"),
    @Index(name = "idx_user_id", columnList = "userId")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ScheduledBillingTriggerLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate triggerDate;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TriggerResult result; // SUCCESS, FAILED

    @Column(length = 1000)
    private String failureReason;

    @Column(nullable = false)
    private LocalDateTime triggeredAt;

    public static ScheduledBillingTriggerLog success(Long userId, LocalDate triggerDate) {
        return ScheduledBillingTriggerLog.builder()
            .userId(userId)
            .triggerDate(triggerDate)
            .result(TriggerResult.SUCCESS)
            .triggeredAt(LocalDateTime.now())
            .build();
    }

    public static ScheduledBillingTriggerLog failure(Long userId, LocalDate triggerDate, String reason) {
        return ScheduledBillingTriggerLog.builder()
            .userId(userId)
            .triggerDate(triggerDate)
            .result(TriggerResult.FAILED)
            .failureReason(reason)
            .triggeredAt(LocalDateTime.now())
            .build();
    }
}

