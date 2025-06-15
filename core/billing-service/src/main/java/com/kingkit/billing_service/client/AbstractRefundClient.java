package com.kingkit.billing_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * 환불 클라이언트 공통 추상 클래스
 * - WebClient 기반 공통 로직 처리
 * - PG사별로 URI, 파라미터만 오버라이드
 */
@Slf4j
public abstract class AbstractRefundClient implements RefundClient {

    protected final WebClient client;

    protected AbstractRefundClient(WebClient client) {
        this.client = client;
    }

    @Override
    public boolean requestRefund(String paymentKey, String cancelReason, long cancelAmount) {
        try {
            return requestRefundAsync(paymentKey, cancelReason, cancelAmount).block();
        } catch (Exception e) {
            logError(e);
            return false;
        }
    }

    protected abstract Mono<Boolean> requestRefundAsync(String paymentKey, String cancelReason, long cancelAmount);

    @Override
    public HttpStatus testConnection() {
        return (HttpStatus) testConnectionAsync().block();
    }

    protected Mono<HttpStatusCode> testConnectionAsync() {
        return client.get()
                .uri(getTestUri())
                .retrieve()
                .toBodilessEntity()
                .map(ResponseEntity::getStatusCode)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.warn("❌ 환불 API 연결 실패 - Status: {}", ex.getStatusCode());
                    return Mono.just(ex.getStatusCode());
                });
    }

    protected abstract String getTestUri();

    protected void logError(Throwable e) {
        if (e instanceof WebClientResponseException ex) {
            log.error("❌ Refund API 오류 - status: {}, body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        } else {
            log.error("❌ Refund API 호출 실패", e);
        }
    }
}
