package com.kingkit.billing_service.exception.core;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorResponse {
    private final String      timestamp;
    private final int         status;
    private final String      error;
    private final String      code;
    private final String      message;
    private final String      path;
}
