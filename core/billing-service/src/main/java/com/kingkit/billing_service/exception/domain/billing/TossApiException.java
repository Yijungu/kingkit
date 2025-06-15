package com.kingkit.billing_service.exception.domain.billing;

import org.springframework.http.HttpStatus;

import com.kingkit.billing_service.exception.core.BusinessException;
import com.kingkit.billing_service.exception.core.ErrorCode;

import lombok.Getter;

/**
 * Toss API와의 통신 중 발생한 예외를 표현합니다.
 */
@Getter
public class TossApiException extends BusinessException {
    private final HttpStatus tossStatus;     // Toss 에서 받은 원본 HTTP

    public TossApiException(HttpStatus tossStatus, String body) {
        super(ErrorCode.TOSS_API_ERROR, "Toss 응답: " + tossStatus.value() + " " + body);
        this.tossStatus = tossStatus;
    }
}