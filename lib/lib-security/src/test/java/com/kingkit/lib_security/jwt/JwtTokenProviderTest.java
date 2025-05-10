package com.kingkit.lib_security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;
import java.security.Key;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;


    Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());


    private final JwtProperties props = new JwtProperties(
        encodedKey,
        1000 * 60 * 5,
        1000 * 60 * 60
    );


    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(props);
        tokenProvider.getClass().getDeclaredMethods(); // just to trigger @PostConstruct
        try {
            var initMethod = JwtTokenProvider.class.getDeclaredMethod("init");
            initMethod.setAccessible(true);
            initMethod.invoke(tokenProvider);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("✅ 액세스 토큰 생성")
    void createAccessToken_success() {
        String token = tokenProvider.createAccessToken("user@example.com", "ROLE_USER");
        assertThat(token).isNotBlank();
        assertThat(tokenProvider.isTokenValid(token)).isTrue();
        assertThat(tokenProvider.getUserId(token)).isEqualTo("user@example.com");
        assertThat(tokenProvider.getRole(token)).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("✅ 리프레시 토큰 생성")
    void createRefreshToken_success() {
        String token = tokenProvider.createRefreshToken("user@example.com");
        assertThat(token).isNotBlank();
        assertThat(tokenProvider.isTokenValid(token)).isTrue();
        assertThat(tokenProvider.getUserId(token)).isEqualTo("user@example.com");
        assertThat(tokenProvider.getRole(token)).isNull();
    }

    @Test
    @DisplayName("✅ 만료된 토큰 → 유효하지 않음")
    void expiredToken_invalid() {
        String expiredToken = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9."
            + "eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTc0Njg2MTcwMywiZXhwIjoxNzQ2ODYxNjkzfQ."
            + "D9q5t-68EXciQEt7loQXXD6fBeL4WSEeFD5YtgdsHnCz83IfqLzTFCFCmWb2Bj0-IdoxCpU-Dt36WFimUk-gfw";

        assertThat(tokenProvider.isTokenValid(expiredToken)).isFalse();
    }

    @Test
    @DisplayName("✅ Role이 없는 토큰 → null 반환")
    void noRoleToken_success() {
        String token = tokenProvider.createRefreshToken("user@example.com");

        assertThat(tokenProvider.isTokenValid(token)).isTrue();  // ✅ true여야 함
        assertThat(tokenProvider.getRole(token)).isNull();       // ✅ role이 null인지 확인
    }

    @Test
    @DisplayName("✅ 잘못된 토큰 → 유효하지 않음")
    void invalidToken_returnsFalse() {
        String invalid = "this.is.invalid.token";
        assertThat(tokenProvider.isTokenValid(invalid)).isFalse();
    }

    @Test
    @DisplayName("❌ Null 토큰 → false")
    void nullToken_isInvalid() {
        assertThat(tokenProvider.isTokenValid(null)).isFalse();
    }
}
