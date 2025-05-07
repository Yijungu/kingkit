package com.kingkit.user_service.exception;

import org.springframework.http.HttpStatus;
import lombok.Builder;

@Builder
public record ErrorResponse(int status, String code, String message) {

    public static ErrorResponse of(HttpStatus status, String code, String message) {
        return new ErrorResponse(status.value(), code, message);
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        // 기본적으로 status name (ex: BAD_REQUEST) 를 code로 채워줌
        return new ErrorResponse(status.value(), status.name(), message);
    }

    public static ErrorResponse of(String code, String message) {
        // 별도 status 없이 code + message로 응답
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), code, message);
    }
}
