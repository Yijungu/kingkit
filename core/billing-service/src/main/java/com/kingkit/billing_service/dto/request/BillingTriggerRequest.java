package com.kingkit.billing_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 정기 결제 트리거 요청 DTO
 * subscriptionId 또는 userId 중 하나만 입력 가능
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingTriggerRequest {

    private Long subscriptionId;
    private Long userId;

    public boolean hasSubscriptionId() {
        return subscriptionId != null;
    }

    public boolean hasUserId() {
        return userId != null;
    }
}
