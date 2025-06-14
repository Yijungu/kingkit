package com.kingkit.billing_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 결제 수단 등록 요청 후 Toss Checkout 정보를 담는 응답 DTO입니다.
 */
@Getter
@Builder
@AllArgsConstructor
public class PrepareBillingResponse {

    /** Toss Checkout 페이지 URL */
    private final String checkoutUrl;

    /** 주문 고유 ID */
    private final String orderId;

    /** 고객 키 (예: user-1001) */
    private final String customerKey;
}
