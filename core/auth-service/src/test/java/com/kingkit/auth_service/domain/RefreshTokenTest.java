package com.kingkit.auth_service.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RefreshTokenTest {

    @Test
    @DisplayName("토큰을 새 값으로 업데이트할 수 있다")
    void update_성공() {
        // given
        RefreshToken token = RefreshToken.builder()
                .email("user@test.com")
                .token("oldToken")
                .role("USER")
                .build();

        // when
        token.update("newToken");

        // then
        assertThat(token.getToken()).isEqualTo("newToken");
    }

    @Test
    @DisplayName("토큰 일치 여부 확인 - 일치")
    void isTokenMatch_일치() {
        RefreshToken token = new RefreshToken("user@test.com", "refresh123", "USER");

        assertThat(token.isTokenMatch("refresh123")).isTrue();
    }

    @Test
    @DisplayName("토큰 일치 여부 확인 - 불일치")
    void isTokenMatch_불일치() {
        RefreshToken token = new RefreshToken("user@test.com", "refresh123", "USER");

        assertThat(token.isTokenMatch("otherToken")).isFalse();
    }
}
