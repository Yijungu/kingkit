package com.kingkit.billing_service.domain.payment;

import com.kingkit.billing_service.domain.BaseTimeEntity;
import com.kingkit.billing_service.domain.subscription.Subscription;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

/**
 * 📄 결제 이력
 */
@Entity
@Table(name = "payment_histories", indexes = {
        @Index(name = "idx_ph_subscription_id", columnList = "subscription_id"),
        @Index(name = "idx_ph_order_id",       columnList = "orderId"),
        @Index(name = "idx_ph_paid_at",        columnList = "paidAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PaymentHistory extends BaseTimeEntity {

    /* ────────── PK ────────── */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ────────── 관계 ────────── */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Subscription subscription;

    /* ────────── 결제 식별 정보 ────────── */
    @Column(nullable = false, unique = true, length = 255)
    private String paymentKey;

    @Column(nullable = false, length = 255)
    private String orderId;

    /* ────────── 결제 메타 ────────── */
    @Column(nullable = false)
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false)
    private Long amount;

    /** 관리자 메모 겸 결제 설명 */
    @Column(columnDefinition = "text")
    private String description;

    /** PG 원본 JSON(1 GB까지 저장 가능) */
    @Column(columnDefinition = "text")
    private String pgResponseRaw;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer retryCount;

    /* ────────── 정적 팩토리 메서드 ────────── */
    public static PaymentHistory of(
            Subscription sub,
            Request dto,
            PaymentStatus status,
            int retryCount
    ) {
        return PaymentHistory.builder()
                .subscription(sub)
                .paymentKey(dto.paymentKey())
                .orderId(dto.orderId())
                .paidAt(LocalDateTime.now())
                .status(status)
                .amount(dto.amount())
                .description(dto.description())
                .pgResponseRaw(dto.pgResponseRaw())
                .retryCount(retryCount)
                .build();
    }

    public static PaymentHistory success(
            Subscription sub,
            String paymentKey,
            String orderId,
            Long amount,
            String description,
            String pgResponseRaw,
            int retryCount
    ) {
        return PaymentHistory.builder()
                .subscription(sub)
                .paymentKey(paymentKey)
                .orderId(orderId)
                .paidAt(LocalDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .amount(amount)
                .description(description)
                .pgResponseRaw(pgResponseRaw)
                .retryCount(retryCount)
                .build();
    }

    public static PaymentHistory failed(
            Subscription sub,
            String paymentKey,
            String orderId,
            Long amount,
            String description,
            String pgResponseRaw,
            int retryCount
    ) {
        return PaymentHistory.builder()
                .subscription(sub)
                .paymentKey(paymentKey)
                .orderId(orderId)
                .paidAt(LocalDateTime.now())
                .status(PaymentStatus.FAILED)
                .amount(amount)
                .description(description)
                .pgResponseRaw(pgResponseRaw)
                .retryCount(retryCount)
                .build();
    }

    /* ────────── 상태 전이 메서드 ────────── */
    public void increaseRetry() {
        this.retryCount += 1;
    }

    /* 요청 DTO 전용 내부 레코드 */
    public record Request(
            String paymentKey,
            String orderId,
            Long amount,
            String description,
            String pgResponseRaw
    ) {}
}
