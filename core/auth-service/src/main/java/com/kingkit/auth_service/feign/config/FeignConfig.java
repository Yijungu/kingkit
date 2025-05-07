package com.kingkit.auth_service.feign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class FeignConfig {

    @Value("${internal.api-keys[0]}")
    private String internalApiKey;

    @Bean
    public RequestInterceptor internalApiKeyInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Internal-API-Key", internalApiKey);
        };
    }
}
