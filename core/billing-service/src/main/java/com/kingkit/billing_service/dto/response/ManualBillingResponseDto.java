package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 수동 결제 처리 후 반환되는 응답 DTO입니다.
 * 결제 결과 정보와 상태를 포함합니다.
 */
@Builder
public record ManualBillingResponseDto(
    String paymentKey,               // PG에서 반환된 결제 키
    String orderId,                  // 주문 식별자
    PaymentStatus status,            // SUCCESS / FAILED
    LocalDateTime paidAt             // 결제 시각
) {}
