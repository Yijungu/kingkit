package com.kingkit.billing_service.exception.domain.billing;

import com.kingkit.billing_service.exception.core.BusinessException;
import com.kingkit.billing_service.exception.core.ErrorCode;

public class AlreadyResolvedFailureException extends BusinessException {

    public AlreadyResolvedFailureException() {
        super(ErrorCode.ALREADY_RESOLVED_FAILURE, "이미 처리된 결제 실패입니다.");
    }

    public AlreadyResolvedFailureException(String message) {
        super(ErrorCode.ALREADY_RESOLVED_FAILURE, message);
    }
}
