package com.kingkit.billing_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 관리자 수동 정기결제 트리거 요청 DTO
 * 특정 날짜 또는 유저 ID 리스트 기준으로 구독 결제를 수행함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminBillingTriggerRequest {

    /**
     * 결제 대상 기준 날짜 (Optional, null이면 기본값: 오늘)
     */
    private LocalDate targetDate;

    /**
     * 결제 대상 유저 ID 리스트 (Optional)
     * null일 경우 모든 유저를 대상으로 수행
     */
    private List<Long> userIds;

    public boolean hasTargetDate() {
        return targetDate != null;
    }

    public boolean hasUserIds() {
        return userIds != null && !userIds.isEmpty();
    }
}
