package com.kingkit.billing_service.exception.domain.billing;

import com.kingkit.billing_service.exception.core.BusinessException;
import com.kingkit.billing_service.exception.core.ErrorCode;

public class SubscriptionNotFoundException extends BusinessException {
    public SubscriptionNotFoundException(String detailMessage) {
        super(ErrorCode.SUBSCRIPTION_NOT_FOUND, detailMessage);
    }
}
