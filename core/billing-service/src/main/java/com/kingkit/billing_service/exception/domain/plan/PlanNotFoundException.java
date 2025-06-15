package com.kingkit.billing_service.exception.domain.plan;

import com.kingkit.billing_service.exception.core.BusinessException;
import com.kingkit.billing_service.exception.core.ErrorCode;

/**
 * 존재하지 않는 요금제(planId)를 조회했을 때 발생
 */

public class PlanNotFoundException extends BusinessException {

    public PlanNotFoundException(String planCode) {
        super(ErrorCode.PLAN_NOT_FOUND,
              "존재하지 않는 요금제입니다: " + planCode);
    }
}