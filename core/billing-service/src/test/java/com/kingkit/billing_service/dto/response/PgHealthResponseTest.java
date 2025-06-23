package com.kingkit.billing_service.dto.response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PgHealthResponseTest {

    @Test
    void successSetsSlowFlag() {
        PgHealthResponse res = PgHealthResponse.success(200, 1500);
        assertThat(res.isAvailable()).isTrue();
        assertThat(res.getStatusCode()).isEqualTo(200);
        assertThat(res.isSlow()).isTrue();
    }

    @Test
    void failureFactory() {
        PgHealthResponse res = PgHealthResponse.failure(500, "err", 100);
        assertThat(res.isAvailable()).isFalse();
        assertThat(res.getMessage()).isEqualTo("err");
        assertThat(res.isSlow()).isFalse();
    }
}
