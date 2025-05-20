package com.kingkit.billing_service.domain.payment;

import com.kingkit.billing_service.domain.subscription.Subscription;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_histories", indexes = {
    @Index(name = "idx_subscription_id", columnList = "subscription_id"),
    @Index(name = "idx_order_id", columnList = "orderId"),
    @Index(name = "idx_paid_at", columnList = "paidAt")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Subscription subscription;

    @Column(nullable = false, unique = true)
    private String paymentKey; // PG사 고유 키

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Long amount;

    private String description;

    @Lob
    private String pgResponseRaw; // PG에서 받은 원본 JSON (정산, 문제해결용)

    @Column(nullable = false)
    private Integer retryCount;

    // 정적 팩토리
    public static PaymentHistory success(Subscription sub, String paymentKey, String orderId, Long amount, String description, String pgResponseRaw, int retryCount) {
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

    public static PaymentHistory failed(Subscription sub, String paymentKey, String orderId, Long amount, String description, String pgResponseRaw, int retryCount) {
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
}

