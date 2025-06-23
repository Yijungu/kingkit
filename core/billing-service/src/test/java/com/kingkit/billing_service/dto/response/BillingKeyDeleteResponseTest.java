package com.kingkit.billing_service.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BillingKeyDeleteResponseTest {

    @Test
    void successFactory() {
        BillingKeyDeleteResponse res = BillingKeyDeleteResponse.success("key");
        assertThat(res.isDeleted()).isTrue();
        assertThat(res.getBillingKey()).isEqualTo("key");
        assertThat(res.getMessage()).isEqualTo("삭제 완료");
    }

    @Test
    void failureFactory() {
        BillingKeyDeleteResponse res = BillingKeyDeleteResponse.failure("key", "no");
        assertThat(res.isDeleted()).isFalse();
        assertThat(res.getMessage()).isEqualTo("no");
    }
}
