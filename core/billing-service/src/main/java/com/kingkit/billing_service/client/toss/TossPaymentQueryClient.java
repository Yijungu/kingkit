package com.kingkit.billing_service.client.toss;

import com.kingkit.billing_service.client.AbstractPaymentQueryClient;
import com.kingkit.billing_service.config.toss.TossProperties;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Component
public class TossPaymentQueryClient extends AbstractPaymentQueryClient {

    public TossPaymentQueryClient(TossProperties properties) {
        super(buildWebClient(properties));
    }

    private static WebClient buildWebClient(TossProperties properties) {
    String encodedKey = Base64.getEncoder()
            .encodeToString((properties.secretKey() + ":").getBytes(StandardCharsets.UTF_8));

    return WebClient.builder()
            .baseUrl(properties.baseUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(
                new ReactorClientHttpConnector(
                    HttpClient.create()
                              .responseTimeout(Duration.ofSeconds(5))   // ✅ 응답 타임아웃
                              .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) // ✅ 연결 타임아웃
                )
            )
            .build();
}

}
