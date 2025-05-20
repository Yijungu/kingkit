package com.kingkit.billing_service.domain.payment;

import com.kingkit.billing_service.domain.subscription.Subscription;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_failures", indexes = {
    @Index(name = "idx_subscription_id", columnList = "subscription_id"),
    @Index(name = "idx_retry_scheduled_at", columnList = "retryScheduledAt"),
    @Index(name = "idx_resolved", columnList = "resolved")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentFailure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Subscription subscription;

    @Column(nullable = false)
    private LocalDateTime failedAt;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = false)
    private LocalDateTime retryScheduledAt;

    @Column(nullable = false)
    private boolean resolved;

    public void markResolved() {
        this.resolved = true;
    }

    public void scheduleNextRetry(Duration interval) {
        this.retryCount += 1;
        this.retryScheduledAt = LocalDateTime.now().plus(interval);
    }
}

