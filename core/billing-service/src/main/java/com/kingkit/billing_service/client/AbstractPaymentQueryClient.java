package com.kingkit.billing_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public abstract class AbstractPaymentQueryClient implements PaymentQueryClient {

    protected final WebClient webClient;

    protected AbstractPaymentQueryClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Map<String, Object> getPaymentDetail(String paymentKey) {
        return getPaymentDetailAsync(paymentKey).block();
    }

    protected Mono<Map> getPaymentDetailAsync(String paymentKey) {
        return webClient.get()
                .uri("/v1/payments/{paymentKey}", paymentKey)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnError(this::logError);
    }

    @Override
    public boolean isPaymentSuccessful(String paymentKey) {
        try {
            Map<String, Object> result = getPaymentDetail(paymentKey);
            return "DONE".equals(result.get("status"));
        } catch (Exception e) {
            log.error("❌ 결제 성공 여부 확인 실패 - {}", e.getMessage());
            return false;
        }
    }

    @Override
    public HttpStatus testConnection() {
        return testConnectionAsync().block();
    }

    protected Mono<HttpStatus> testConnectionAsync() {
        return webClient.get()
                .uri("/v1/payments/test-connection")
                .retrieve()
                .toBodilessEntity()
                .map(response -> (HttpStatus) response.getStatusCode())
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.warn("❌ 테스트 연결 실패 - Status: {}", ex.getStatusCode());
                    return Mono.just((HttpStatus) ex.getStatusCode());
                });
    }

    protected void logError(Throwable e) {
        if (e instanceof WebClientResponseException ex) {
            log.error("❌ Toss API 오류 - status: {}, body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        } else {
            log.error("❌ Toss API 호출 실패", e);
        }
    }
}
