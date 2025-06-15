package com.kingkit.billing_service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Toss 연동 상태 점검 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
public class PgHealthResponse {

    /** Toss API 사용 가능 여부 */
    private final boolean available;

    /** Toss 응답 코드 */
    @JsonProperty("statusCode")
    private int statusCode; 

    /** 상태 설명 메시지 */
    private final String message;

    /** 응답 시간 (ms) */
    private final long responseTimeMillis;

    /** 1초 이상 지연 여부 */
    private final boolean slow;

    // ✅ 팩토리 메서드 예시
    public static PgHealthResponse success(int statusCode, long elapsedMillis) {
        return PgHealthResponse.builder()
                .available(true)
                .statusCode(statusCode)
                .message("Toss 연동 정상")
                .responseTimeMillis(elapsedMillis)
                .slow(elapsedMillis >= 1000)
                .build();
    }

    public static PgHealthResponse failure(int statusCode, String message, long elapsedMillis) {
        return PgHealthResponse.builder()
                .available(false)
                .statusCode(statusCode)
                .message(message)
                .responseTimeMillis(elapsedMillis)
                .slow(elapsedMillis >= 1000)
                .build();
    }
}
