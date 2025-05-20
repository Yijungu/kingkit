package com.kingkit.billing_service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 결제 내역 리스트 응답 DTO
 */
@Getter
@Builder
public class BillingHistoryResponse {
    private List<BillingRecordDto> history;
}
