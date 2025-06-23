package com.kingkit.billing_service.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderIdGeneratorTest {

    @Test
    void generate_returnsFormattedOrderId() {
        OrderIdGenerator generator = new OrderIdGenerator();
        String id = generator.generate();

        assertThat(id).matches("order-\\d{14}-[0-9a-f]{8}");
    }
}
