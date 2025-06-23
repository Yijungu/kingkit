package com.kingkit.billing_service.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.*;

class BillingHistorySearchConditionTest {
    @Test
    @DisplayName("toPageable 기본값 확인")
    void toPageable_defaults() {
        BillingHistorySearchCondition cond = new BillingHistorySearchCondition();
        Pageable page = cond.toPageable();
        assertThat(page.getPageNumber()).isZero();
        assertThat(page.getPageSize()).isEqualTo(10);
    }
}
