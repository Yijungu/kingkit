package com.kingkit.billing_service.config.toss;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.kingkit.billing_service.client.toss.TossClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(TossProperties.class)
@RequiredArgsConstructor
public class TossClientConfig {

    private final TossProperties props;

    /** Toss 전용 WebClient */
    @Bean
    public WebClient tossWebClient(WebClient.Builder builder) {
        String auth = Base64.getEncoder()
                .encodeToString((props.secretKey() + ":").getBytes(StandardCharsets.UTF_8));

        return builder
                .baseUrl(props.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /** TossClient Adapter */
    @Bean
    public TossClient tossClient(WebClient.Builder builder) {
        // 👉 1) WebClient.Builder (스프링이 주입)  2) TossProperties (필드로 보유)  전달
        return new TossClient(builder, props);
    }
}

