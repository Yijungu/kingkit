package com.kingkit.billing_service.exception.domain.billing;

import com.kingkit.billing_service.exception.core.BusinessException;
import com.kingkit.billing_service.exception.core.ErrorCode;

public class RetryLimitExceededException extends BusinessException {

    public RetryLimitExceededException() {
        super(ErrorCode.RETRY_LIMIT_EXCEEDED, "재시도 가능 횟수를 초과하였습니다.");
    }

    public RetryLimitExceededException(String message) {
        super(ErrorCode.RETRY_LIMIT_EXCEEDED, message);
    }
}
