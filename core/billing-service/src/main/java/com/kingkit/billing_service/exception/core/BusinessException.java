package com.kingkit.billing_service.exception.core;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, String detail) {
        super(detail == null ? errorCode.getMessage() : detail);
        this.errorCode = errorCode;
    }
}
