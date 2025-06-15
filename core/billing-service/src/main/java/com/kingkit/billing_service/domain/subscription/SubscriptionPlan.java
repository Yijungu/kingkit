package com.kingkit.billing_service.domain.subscription;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "subscription_plans",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_plan_code", columnNames = "planCode")
    },
    indexes = {
        @Index(name = "idx_is_active", columnList = "isActive")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String planCode; // 예: basic-monthly

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Integer durationDays; // 일 단위 기간 (ex: 30, 365)

    @Column(nullable = false)
    private boolean isActive;

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}