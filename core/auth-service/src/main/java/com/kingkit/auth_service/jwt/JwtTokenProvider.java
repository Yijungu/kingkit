package com.kingkit.auth_service.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor          // props 주입
public class JwtTokenProvider {

    private final JwtProperties props; // ✅ 설정 객체 주입
    private Key signingKey;

    /* ===== 초기화 ===== */
    @PostConstruct
    private void init() {
        byte[] keyBytes = Decoders.BASE64.decode(props.secret());
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String userId, String role) {
        return createToken(userId, role, props.accessTokenValidity());
    }

    public String createRefreshToken(String userId) {
        return createToken(userId, null, props.refreshTokenValidity());
    }

    private String createToken(String userId, String role, long validityMs) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(userId);
        if (role != null) claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validityMs))
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /* ===== 토큰 검증 & 정보 추출 ===== */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRole(String token) {
        Object role = parseClaims(token).get("role");
        return role != null ? role.toString() : null;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(signingKey)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }
}
