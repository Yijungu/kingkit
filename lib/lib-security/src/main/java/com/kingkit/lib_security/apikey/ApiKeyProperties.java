package com.kingkit.lib_security.apikey;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "internal")
public record ApiKeyProperties(
        /** 허용 API-Key 집합 */
        Set<String> apiKeys,
        /** 허용 IP 목록(선택) */
        List<String> allowedIps
) { }
