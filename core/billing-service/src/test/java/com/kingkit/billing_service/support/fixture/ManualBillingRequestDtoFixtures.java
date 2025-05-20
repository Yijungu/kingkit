package com.kingkit.billing_service.support.fixture;

import com.kingkit.billing_service.dto.request.ManualBillingRequestDto;

public class ManualBillingRequestDtoFixtures {

    public static ManualBillingRequestDto valid(Long userId, String billingKey, String orderId) {
        return new ManualBillingRequestDto(
            userId,
            billingKey,
            orderId,
            10900L,
            "테스트 수동 결제"
        );
    }

    public static ManualBillingRequestDto withInvalidBillingKey(Long userId) {
        return new ManualBillingRequestDto(
            userId,
            "invalid-billing-key",
            "order-invalid",
            10900L,
            "결제키 오류"
        );
    }

    public static ManualBillingRequestDto withDuplicateOrderId(Long userId, String billingKey) {
        return new ManualBillingRequestDto(
            userId,
            billingKey,
            "duplicate-order-id",
            10900L,
            "중복 주문 테스트"
        );
    }

    public static ManualBillingRequestDto withCustomAmount(Long userId, String billingKey, String orderId, long amount) {
        return new ManualBillingRequestDto(
            userId,
            billingKey,
            orderId,
            amount,
            "사용자 지정 금액 테스트"
        );
    }
}
