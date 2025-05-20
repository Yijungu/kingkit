package com.kingkit.billing_service.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 사용자의 구독 취소 완료 시 반환되는 응답 DTO입니다.
 * 구독했던 요금제 이름과 해지 시각, 안내 메시지를 포함합니다.
 */
@Builder
public record SubscriptionCancelResponseDto(
    String planName,
    LocalDateTime cancelledAt,
    String message
) {}
