package com.kingkit.billing_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;


@Slf4j
public abstract class AbstractBillingClient implements PaymentClient {

    protected final WebClient client;

    protected AbstractBillingClient(WebClient client) {
        this.client = client;
    }

    @Override
    public void deleteBillingKey(String billingKey) {
        deleteBillingKeyAsync(billingKey).block();
    }

    @Override
    public HttpStatus testConnection(String billingKey) {
        return (HttpStatus) testConnectionAsync(billingKey).block();
    }

    protected Mono<ResponseEntity<Void>> deleteBillingKeyAsync(String billingKey) {
        return client.delete()
                .uri("/v1/billing/{billingKey}", billingKey)
                .retrieve()
                .toBodilessEntity()
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.warn("Billing key already deleted: {}", billingKey);
                        return Mono.empty();
                    }
                    logError(e);
                    return Mono.error(new IllegalStateException("Billing key delete failed: " + e.getMessage()));
                });
    }

    protected Mono<HttpStatusCode> testConnectionAsync(String uri) {
        return client.get()
                .uri(uri)
                .retrieve()
                .toBodilessEntity()
                .map(ResponseEntity::getStatusCode)
                .onErrorResume(WebClientResponseException.class, ex -> Mono.just(ex.getStatusCode()))
                .doOnError(this::logError);
    }

    protected void logError(Throwable e) {
        if (e instanceof WebClientResponseException ex) {
            log.error("API Error: {} {}\nBody={}", ex.getRawStatusCode(), ex.getStatusText(), ex.getResponseBodyAsString());
        } else {
            log.error("API Error", e);
        }
    }

    protected abstract Mono<String> requestCheckoutUrlAsync(String success, String fail, String customerKey, String orderId, long amount);

    protected abstract Mono<String> executeBillingAsync(String billingKey, String orderId, long amount);
}
