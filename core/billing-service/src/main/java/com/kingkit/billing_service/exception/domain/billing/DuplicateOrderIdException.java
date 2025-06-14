package com.kingkit.billing_service.exception.domain.billing;

import com.kingkit.billing_service.exception.core.BusinessException;
import com.kingkit.billing_service.exception.core.ErrorCode;

/**
 * 중복된 orderId로 결제가 요청될 때 발생하는 예외
 */
public class DuplicateOrderIdException extends BusinessException {
    public DuplicateOrderIdException(String orderId) {
        super(ErrorCode.DUPLICATE_ORDER_ID, "중복 주문 ID: " + orderId);
    }
}

