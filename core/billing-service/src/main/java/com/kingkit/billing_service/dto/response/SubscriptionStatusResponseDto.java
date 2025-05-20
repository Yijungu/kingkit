package com.kingkit.billing_service.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 현재 로그인한 사용자의 구독 상태 정보를 나타내는 DTO.
 * ACTIVE 상태의 구독이 존재할 경우 상세 정보를 포함.
 */
@Builder
public record SubscriptionStatusResponseDto(
    String planName,
    LocalDateTime subscribedAt,
    LocalDateTime nextBillingAt,
    boolean isActive,
    CardInfo cardInfo
) {

    /**
     * 카드 정보 내 중첩 DTO
     */
    @Builder
    public record CardInfo(
        String cardCompany,
        String cardNumberMasked
    ) {}
}
