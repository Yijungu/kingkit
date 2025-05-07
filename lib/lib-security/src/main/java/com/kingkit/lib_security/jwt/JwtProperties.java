package com.kingkit.lib_security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String secret,
    long accessTokenValidity,
    long refreshTokenValidity
) {}
