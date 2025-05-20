package com.kingkit.billing_service.domain.payment;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "payment_methods",
    indexes = {
        @Index(name = "idx_user_active", columnList = "userId, isActive"),
        @Index(name = "idx_billing_key", columnList = "billingKey")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_billing_key", columnNames = {"userId", "billingKey"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String billingKey;

    @Column(nullable = false, length = 30)
    private String cardCompany;

    @Column(nullable = false, length = 20)
    private String cardNumberMasked; // 예: ****-****-****-1234

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    @Column(nullable = false)
    private boolean isActive;

    /** 단일 결제 수단 활성화 처리 */
    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}
