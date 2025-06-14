package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import lombok.Builder;

/**
 * 정기 결제 트리거 결과의 개별 항목 DTO.
 * 구독 ID, 사용자 ID, 결제 결과 상태 및 실패 이유를 포함합니다.
 */
public record BillingTriggerDetail(
        Long subscriptionId,
        Long userId,
        PaymentStatus status,
        String reason // 실패 이유 또는 null
) {
    @Builder
    public BillingTriggerDetail(Long subscriptionId, Long userId, PaymentStatus status, String reason) {
        this.subscriptionId = subscriptionId;
        this.userId = userId;
        this.status = status;
        this.reason = reason;
    }
}
