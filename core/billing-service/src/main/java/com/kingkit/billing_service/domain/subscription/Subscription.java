package com.kingkit.billing_service.domain.subscription;

import com.kingkit.billing_service.domain.payment.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions",
       indexes = {
           @Index(name = "idx_user_id", columnList = "userId"),
           @Index(name = "idx_next_billing_at", columnList = "nextBillingAt")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private SubscriptionPlan plan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime nextBillingAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    // -------------------------------
    // 📌 비즈니스 로직 (도메인 서비스가 아닌, 엔티티 내부 책임)
    // -------------------------------

    public void markCanceled() {
        this.status = SubscriptionStatus.CANCELED;
    }

    public void markExpired() {
        this.status = SubscriptionStatus.EXPIRED;
    }

    public void renewNextBilling() {
        this.nextBillingAt = this.nextBillingAt.plusDays(plan.getDurationDays());
    }

    public boolean isActive() {
        return this.status == SubscriptionStatus.ACTIVE;
    }
}