package com.kingkit.billing_service.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Toss 연동 상태 점검 응답 DTO
 */
@Getter
@Builder
public class PgHealthResponse {

    private boolean available;          // Toss API 사용 가능 여부
    private int statusCode;             // Toss 응답 코드
    private String message;             // 상태 설명 메시지
    private long responseTimeMillis;    // 응답 시간 (ms)
    private boolean slow;               // 1초 이상 지연 여부
}
