package com.kingkit.billing_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 수동 결제 요청용 DTO입니다.
 * 내부 결제 트리거나 관리자 요청 등에 사용됩니다.
 */
public record ManualBillingRequestDto(
    @NotNull Long userId,             // 결제 대상 사용자 ID
    @NotBlank String billingKey,      // PG 결제 키 (Toss에서 발급)
    @NotBlank String orderId,         // 중복 방지를 위한 고유 주문 ID
    @Min(100) Long amount,            // 결제 금액 (단위: 원)
    @NotBlank String description      // 결제 설명 (관리자 주석 등)
) {}
