package com.kingkit.billing_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 정기 결제 트리거 결과 응답 DTO
 * 결제 성공/실패 여부 및 상세 정보를 반환
 */
@Getter
@Builder
public class BillingTriggerResponse {

    private Long subscriptionId;

    private String orderId;
    private String paymentKey;

    private String status;              // SUCCESS or FAILED
    private LocalDateTime paidAt;       // 결제 성공 시각
    private LocalDateTime failedAt;     // 결제 실패 시각 (nullable)
    private String reason;              // 실패 사유 (nullable)

    private String message;             // 사용자 안내 메시지
}
