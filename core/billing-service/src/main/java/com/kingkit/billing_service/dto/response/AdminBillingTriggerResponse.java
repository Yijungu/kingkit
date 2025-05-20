package com.kingkit.billing_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 관리자 수동 결제 트리거 결과 응답 DTO
 * 전체 실행 통계 및 개별 유저별 결과를 포함함
 */
@Getter
@Builder
public class AdminBillingTriggerResponse {

    private int successCount;
    private int failureCount;
    private List<TriggerResultDetail> details;

    @Getter
    @Builder
    public static class TriggerResultDetail {
        private Long userId;
        private String status;    // SUCCESS or FAILED
        private String reason;    // nullable if SUCCESS
    }
}
