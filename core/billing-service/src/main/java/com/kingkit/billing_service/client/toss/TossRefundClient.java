package com.kingkit.billing_service.client.toss;

import com.kingkit.billing_service.client.AbstractRefundClient;
import com.kingkit.billing_service.config.toss.TossProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * Toss 결제 환불 요청 전용 클라이언트
 */
@Slf4j
@Component
public class TossRefundClient extends AbstractRefundClient {

    public TossRefundClient(TossProperties properties) {
        super(buildWebClient(properties));
    }

    private static WebClient buildWebClient(TossProperties properties) {
        String encodedKey = Base64.getEncoder()
                .encodeToString((properties.secretKey() + ":").getBytes(StandardCharsets.UTF_8));

        return WebClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    protected Mono<Boolean> requestRefundAsync(String paymentKey, String cancelReason, long cancelAmount) {
        return client.post()
                .uri("/v1/payments/{paymentKey}/cancel", paymentKey)
                .bodyValue(Map.of(
                        "cancelReason", cancelReason,
                        "cancelAmount", cancelAmount
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    log.info("✅ 환불 성공 - paymentKey: {}, response: {}", paymentKey, response);
                    return true;
                })
                .onErrorResume(e -> {
                    logError(e);
                    return Mono.just(false); // 실패 시 false 반환
                });
    }

    @Override
    protected String getTestUri() {
        return "/v1/payments/test-connection";
    }
}
