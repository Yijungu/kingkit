package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 결제 내역 단건 DTO
 */
@Getter
@Builder
public class BillingRecordDto {
    private String orderId;
    private Long amount;
    private PaymentStatus status;      // Enum을 그대로 사용하는 것이 명확함
    private LocalDateTime paidAt;
    private String description;
}
