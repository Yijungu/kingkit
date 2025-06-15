package com.kingkit.billing_service.client.toss;

import com.kingkit.billing_service.client.AbstractBillingClient;
import com.kingkit.billing_service.client.dto.PaymentCommand;
import com.kingkit.billing_service.client.dto.PaymentResult;
import com.kingkit.billing_service.config.toss.TossProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class TossClient extends AbstractBillingClient {

    public TossClient(WebClient.Builder builder, TossProperties properties) {
        super(buildWebClient(builder, properties.baseUrl(), properties.secretKey()));
    }

    public static TossClient testClient(String baseUrl, String secretKey) {
        WebClient testWebClient = buildWebClient(WebClient.builder(), baseUrl, secretKey);
        return new TossClient(testWebClient);
    }

    private TossClient(WebClient client) {
        super(client);
    }

    private static WebClient buildWebClient(WebClient.Builder builder, String baseUrl, String secretKey) {
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        return builder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofSeconds(5))
                ))
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public PaymentResult execute(PaymentCommand command) {
        try {
            String paymentKey = Optional.ofNullable(
                executeBilling(command.getBillingKey(), command.getOrderId(), command.getAmount())
            ).orElseThrow(() -> new IllegalStateException("결제 실패: paymentKey 없음"));

            return PaymentResult.successWithPaymentKey(paymentKey);

        } catch (Exception e) {
            logError(e);
            return PaymentResult.fail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public String requestCheckoutUrl(String success, String fail, String customerKey, String orderId, long amount) {
        return Optional.ofNullable(
            requestCheckoutUrlAsync(success, fail, customerKey, orderId, amount).block()
        ).orElseThrow(() -> new IllegalStateException("Checkout URL 생성 실패"));
    }

    public String executeBilling(String billingKey, String orderId, long amount) {
        return executeBillingAsync(billingKey, orderId, amount).block();
    }

    @Override
    public void deleteBillingKey(String billingKey) {
        deleteBillingKeyAsync(billingKey).block();
    }

    @Override
    public HttpStatus testConnection(String billingKey) {
        String uri = (billingKey != null)
                ? "/v1/billing/" + billingKey
                : "/v1/billing/test-connection";
        return (HttpStatus) testConnectionAsync(uri).block();
    }

    @Override
    protected Mono<String> requestCheckoutUrlAsync(String success, String fail, String customerKey, String orderId, long amount) {
        return client.post()
                .uri("/v1/billing/authorizations")
                .bodyValue(Map.of(
                        "successUrl", success,
                        "failUrl", fail,
                        "customerKey", customerKey,
                        "orderId", orderId,
                        "amount", amount
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> (String) res.get("checkoutUrl"))
                .doOnError(this::logError);
    }

    @Override
    protected Mono<String> executeBillingAsync(String billingKey, String orderId, long amount) {
        return client.post()
                .uri("/v1/billing/{billingKey}", billingKey)
                .bodyValue(Map.of("orderId", orderId, "amount", amount))
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> (String) res.get("paymentKey"))
                .onErrorResume(e -> {
                    logError(e);
                    return Mono.empty();
                });
    }

    @Override
    protected Mono deleteBillingKeyAsync(String billingKey) {
        return client.delete()
                .uri("/v1/billing/{billingKey}", billingKey)
                .retrieve()
                .toBodilessEntity()
                .then()
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.warn("Toss billingKey({}) already deleted", billingKey);
                        return Mono.empty();
                    }
                    logError(e);
                    return Mono.error(new IllegalStateException("빌링키 삭제 실패: " + e.getMessage()));
                });
    }

    @Override
    protected Mono testConnectionAsync(String uri) {
        return client.get()
                .uri(uri)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode())
                .onErrorResume(WebClientResponseException.class, ex -> Mono.just(ex.getStatusCode()))
                .doOnError(this::logError);
    }

    protected void logError(Throwable e) {
        if (e instanceof WebClientResponseException ex) {
            log.error("[Toss] API Error: {} {}\nBody={}", ex.getRawStatusCode(), ex.getStatusText(), ex.getResponseBodyAsString());
        } else {
            log.error("[Toss] Unknown Error", e);
        }
    }
}
