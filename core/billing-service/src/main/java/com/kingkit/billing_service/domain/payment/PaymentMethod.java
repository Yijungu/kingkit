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

    // -------------------------------
    // 📌 상태 전이 메서드
    // -------------------------------

    /** 결제 수단 비활성화 */
    public void deactivate() {
        this.isActive = false;
    }

    /** 결제 수단 활성화 */
    public void activate() {
        this.isActive = true;
    }

    /** 현재 활성 상태인지 여부 반환 */
    public boolean isActivated() {
        return this.isActive;
    }

    // -------------------------------
    // 📌 정적 생성 메서드 (선택적)
    // -------------------------------

    public static PaymentMethod create(Long userId, String billingKey, String cardCompany, String maskedCard) {
        return PaymentMethod.builder()
                .userId(userId)
                .billingKey(billingKey)
                .cardCompany(cardCompany)
                .cardNumberMasked(maskedCard)
                .registeredAt(LocalDateTime.now())
                .isActive(true)
                .build();
    }
}
