package com.kingkit.auth_service.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtTokenInfo {
    private String accessToken;
    private String refreshToken;
}
