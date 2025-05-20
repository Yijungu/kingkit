package com.kingkit.billing_service.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * BillingKey 삭제 결과 응답 DTO
 */
@Getter
@Builder
public class BillingKeyDeleteResponse {

    private String billingKey;   // PG사에서 발급한 billingKey
    private boolean deleted;     // 삭제 여부
    private String message;      // 상태 설명 (성공, 이미 삭제됨, 실패 등)
}
