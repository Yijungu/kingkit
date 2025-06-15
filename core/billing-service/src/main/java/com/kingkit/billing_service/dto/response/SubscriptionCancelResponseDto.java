package com.kingkit.billing_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자의 구독 취소 완료 시 반환되는 응답 DTO입니다.
 * 구독했던 요금제 이름과 해지 시각, 안내 메시지를 포함합니다.
 */
@Getter
@Builder
@AllArgsConstructor
public class SubscriptionCancelResponseDto {
    private final String status; // ✅ 추가
    private final String planName;
    private final LocalDateTime cancelledAt;
    private final String message;

    public static SubscriptionCancelResponseDto sample(String planName) {
        return SubscriptionCancelResponseDto.builder()
                .status("CANCELED")
                .planName(planName)
                .cancelledAt(LocalDateTime.now())
                .message("구독이 성공적으로 해지되었습니다.")
                .build();
    }

    public static SubscriptionCancelResponseDto simple(String message) {
        return SubscriptionCancelResponseDto.builder()
                .status("CANCELED")
                .planName(null)
                .cancelledAt(LocalDateTime.now())
                .message(message)
                .build();
    }
}

