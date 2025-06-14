package com.kingkit.billing_service.config.toss;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml 의 'toss.*' 영역을 그대로 들고 있는 불변 DTO
 */
@ConfigurationProperties(prefix = "toss")
public record TossProperties(
        String baseUrl,
        String secretKey    // test_sk_xxx …
) {}
