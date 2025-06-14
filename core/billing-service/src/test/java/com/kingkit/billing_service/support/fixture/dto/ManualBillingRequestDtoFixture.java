package com.kingkit.billing_service.support.fixture.dto;

import com.kingkit.billing_service.dto.request.ManualBillingRequestDto;

public class ManualBillingRequestDtoFixture {

    public static final long DEFAULT_AMOUNT = 10900L;
    public static final String DEFAULT_DESCRIPTION = "테스트 수동 결제";

    /**
     * ✅ 정상 케이스용 요청 생성
     */
    public static ManualBillingRequestDto valid(Long userId, String billingKey, String orderId) {
        return new ManualBillingRequestDto(
                userId,
                billingKey,
                orderId,
                DEFAULT_AMOUNT,
                DEFAULT_DESCRIPTION
        );
    }

    /**
     * ❌ 존재하지 않는 billingKey 케이스
     */
    public static ManualBillingRequestDto withInvalidBillingKey(Long userId) {
        return new ManualBillingRequestDto(
                userId,
                "invalid-billing-key-" + userId,
                "order-invalid-key-" + userId,
                DEFAULT_AMOUNT,
                "billingKey 없음"
        );
    }

    /**
     * ❌ orderId 중복 케이스용 (orderId 재사용)
     */
    public static ManualBillingRequestDto withDuplicateOrderId(Long userId, String billingKey) {
        return new ManualBillingRequestDto(
                userId,
                billingKey,
                "order-dup-" + userId,
                DEFAULT_AMOUNT,
                "중복 테스트"
        );
    }


    public static ManualBillingRequestDto of(Long userId, String billingKey, String orderId, Long amount, String description) {
        return new ManualBillingRequestDto(userId, billingKey, orderId, amount, description);
    }

    public static ManualBillingRequestDto defaultRequest() {
        return of(1001L, "billing-test-key", "order-20240519-001", 10900L, "관리자 수동 결제 테스트");
    }
}
