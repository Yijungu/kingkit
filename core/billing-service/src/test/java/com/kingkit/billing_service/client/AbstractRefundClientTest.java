package com.kingkit.billing_service.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractRefundClientTest {

    @Test
    @DisplayName("requestRefund 결과 전파")
    void requestRefund_propagates() {
        TestRefundClient client = new TestRefundClient(Mono.just(true), Mono.just(HttpStatus.OK));
        assertThat(client.requestRefund("key","reason",1000L)).isTrue();
    }

    @Test
    @DisplayName("requestRefund 예외시 false")
    void requestRefund_exception() {
        TestRefundClient client = new TestRefundClient(Mono.error(new RuntimeException("boom")), Mono.just(HttpStatus.OK));
        assertThat(client.requestRefund("k","r",100L)).isFalse();
    }

    @Test
    @DisplayName("testConnection 반환")
    void testConnection_value() {
        TestRefundClient client = new TestRefundClient(Mono.empty(), Mono.just(HttpStatus.SERVICE_UNAVAILABLE));
        assertThat(client.testConnection()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    private static class TestRefundClient extends AbstractRefundClient {
        private final Mono<Boolean> refundMono;
        private final Mono<HttpStatusCode> connMono;

        TestRefundClient(Mono<Boolean> refundMono, Mono<HttpStatusCode> connMono) {
            super(null);
            this.refundMono = refundMono;
            this.connMono = connMono;
        }

        @Override
        protected Mono<Boolean> requestRefundAsync(String paymentKey, String cancelReason, long cancelAmount) {
            return refundMono;
        }

        @Override
        protected String getTestUri() { return "/test"; }

        @Override
        protected Mono<HttpStatusCode> testConnectionAsync() { return connMono; }
    }
}
