package com.kingkit.user_service.exception;

import org.springframework.http.HttpStatus;

import lombok.Builder;


@Builder
public record ErrorResponse(int status, String code, String message) {
    public static ErrorResponse of(HttpStatus status, String code, String message) {
        return new ErrorResponse(status.value(), code, message);
    }

    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status.value(), null, message);
    }
}

