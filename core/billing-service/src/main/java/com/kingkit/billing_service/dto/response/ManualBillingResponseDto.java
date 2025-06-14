package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ManualBillingResponseDto {

    private final String paymentKey;
    private final String orderId;
    private final PaymentStatus status;
    private final LocalDateTime paidAt;

    public static ManualBillingResponseDto success(String paymentKey, String orderId, LocalDateTime paidAt) {
        return ManualBillingResponseDto.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .status(PaymentStatus.SUCCESS)
                .paidAt(paidAt)
                .build();
    }

    public static ManualBillingResponseDto failed(String orderId) {
        return ManualBillingResponseDto.builder()
                .paymentKey(null)
                .orderId(orderId)
                .status(PaymentStatus.FAILED)
                .paidAt(null)
                .build();
    }
}
