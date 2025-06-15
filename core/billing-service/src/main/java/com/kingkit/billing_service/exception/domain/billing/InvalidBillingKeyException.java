package com.kingkit.billing_service.exception.domain.billing;

import com.kingkit.billing_service.exception.core.BusinessException;
import com.kingkit.billing_service.exception.core.ErrorCode;

public class InvalidBillingKeyException extends BusinessException {
    public InvalidBillingKeyException(String billingKey) {
        super(ErrorCode.BILLING_KEY_NOT_FOUND, "유효하지 않은 billingKey: " + billingKey);
    }
}
