package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 결제 실패 재시도 응답 DTO
 */
@Getter
@Builder
public class RetryPaymentResponse {
    private String paymentKey;
    private String orderId;
    private PaymentStatus status;      // SUCCESS or FAILED
    private LocalDateTime paidAt;      // 결제 승인 시각 (실패 시 null)
}
