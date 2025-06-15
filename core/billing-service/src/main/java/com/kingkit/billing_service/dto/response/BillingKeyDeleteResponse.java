package com.kingkit.billing_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * BillingKey 삭제 결과 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class BillingKeyDeleteResponse {

    private final String billingKey;
    private final boolean deleted;
    private final String message;

    // ✅ 성공 팩토리 메서드
    public static BillingKeyDeleteResponse success(String billingKey) {
        return BillingKeyDeleteResponse.builder()
                .billingKey(billingKey)
                .deleted(true)
                .message("삭제 완료")
                .build();
    }

    // ✅ 실패 팩토리 메서드 (선택)
    public static BillingKeyDeleteResponse failure(String billingKey, String reason) {
        return BillingKeyDeleteResponse.builder()
                .billingKey(billingKey)
                .deleted(false)
                .message(reason)
                .build();
    }
}
