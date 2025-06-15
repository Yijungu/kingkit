package com.kingkit.billing_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 관리자 정기 결제 트리거 응답 DTO
 * 성공/실패 수와 실패 상세 정보를 포함함
 */
@Getter
@Builder
@AllArgsConstructor
public class AdminBillingTriggerResponse {

    /** 성공한 사용자 수 */
    private final int successCount;

    /** 실패한 사용자 수 */
    private final int failureCount;

    /** 실패 상세 리스트 */
    private final List<BillingTriggerDetail> failures;

    public static AdminBillingTriggerResponse of(List<BillingTriggerDetail> failures, int successCount) {
        return AdminBillingTriggerResponse.builder()
                .successCount(successCount)
                .failureCount(failures.size())
                .failures(failures)
                .build();
    }
}
