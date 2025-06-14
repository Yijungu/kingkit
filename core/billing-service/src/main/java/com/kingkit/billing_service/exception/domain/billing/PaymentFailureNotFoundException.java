package com.kingkit.billing_service.exception.domain.billing;

import com.kingkit.billing_service.exception.core.BusinessException;
import com.kingkit.billing_service.exception.core.ErrorCode;

public class PaymentFailureNotFoundException extends BusinessException {

    public PaymentFailureNotFoundException() {
        super(ErrorCode.PAYMENT_FAILURE_NOT_FOUND, "결제 실패 내역이 존재하지 않습니다.");
    }

    public PaymentFailureNotFoundException(String message) {
        super(ErrorCode.PAYMENT_FAILURE_NOT_FOUND, message);
    }
}
