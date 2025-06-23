package com.kingkit.billing_service.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class AbstractPaymentQueryClientTest {

    @Test
    @DisplayName("isPaymentSuccessful 정상 완료")
    void isPaymentSuccessful_done() {
        TestQueryClient client = new TestQueryClient(Mono.just(Map.of("status", "DONE")), Mono.just(HttpStatus.OK));
        assertThat(client.isPaymentSuccessful("pay")).isTrue();
    }

    @Test
    @DisplayName("isPaymentSuccessful 실패 상태")
    void isPaymentSuccessful_fail() {
        TestQueryClient client = new TestQueryClient(Mono.just(Map.of("status", "CANCELED")), Mono.just(HttpStatus.OK));
        assertThat(client.isPaymentSuccessful("pay")).isFalse();
    }

    @Test
    @DisplayName("isPaymentSuccessful 예외 처리")
    void isPaymentSuccessful_exception() {
        TestQueryClient client = new TestQueryClient(Mono.error(new RuntimeException("boom")), Mono.just(HttpStatus.OK));
        assertThat(client.isPaymentSuccessful("pay")).isFalse();
    }

    @Test
    @DisplayName("testConnection 반환")
    void testConnection_value() {
        TestQueryClient client = new TestQueryClient(Mono.empty(), Mono.just(HttpStatus.SERVICE_UNAVAILABLE));
        assertThat(client.testConnection()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    private static class TestQueryClient extends AbstractPaymentQueryClient {
        private final Mono<Map> detailMono;
        private final Mono<HttpStatus> connMono;

        TestQueryClient(Mono<Map> detailMono, Mono<HttpStatus> connMono) {
            super(null);
            this.detailMono = detailMono;
            this.connMono = connMono;
        }

        @Override
        protected Mono<Map> getPaymentDetailAsync(String paymentKey) {
            return detailMono;
        }

        @Override
        protected Mono<HttpStatus> testConnectionAsync() {
            return connMono;
        }
    }
}
