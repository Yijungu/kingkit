package com.kingkit.billing_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 수동 결제 요청용 DTO입니다.
 * 내부 결제 트리거나 관리자 요청 등에 사용됩니다.
 */
@Getter
@Builder
@AllArgsConstructor
public class ManualBillingRequestDto {

    @NotNull
    private final Long userId;

    @NotBlank
    private final String billingKey;

    @NotBlank
    private final String orderId;

    @Min(100)
    private final Long amount;

    @NotBlank
    private final String description;

    // ✅ 정적 팩토리 메서드 추가
    public static ManualBillingRequestDto of(Long userId, String billingKey, String orderId, Long amount, String description) {
        return ManualBillingRequestDto.builder()
                .userId(userId)
                .billingKey(billingKey)
                .orderId(orderId)
                .amount(amount)
                .description(description)
                .build();
    }
}
