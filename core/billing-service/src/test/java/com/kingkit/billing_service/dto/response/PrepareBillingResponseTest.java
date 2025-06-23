package com.kingkit.billing_service.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrepareBillingResponseTest {

    @Test
    void builderWorks() {
        PrepareBillingResponse res = PrepareBillingResponse.builder()
                .checkoutUrl("url")
                .orderId("order")
                .customerKey("cust")
                .build();
        assertThat(res.getCheckoutUrl()).isEqualTo("url");
        assertThat(res.getOrderId()).isEqualTo("order");
        assertThat(res.getCustomerKey()).isEqualTo("cust");
    }
}
