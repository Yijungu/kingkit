package com.kingkit.lib_security.jwt;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static com.kingkit.lib_test_support.testsupport.jwt.JwtTestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtTokenProvider jwtTokenProvider;
    private JwtAuthenticationEntryPoint entryPoint;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = mock(JwtTokenProvider.class);
        entryPoint = mock(JwtAuthenticationEntryPoint.class);
        filter = new JwtAuthenticationFilter(jwtTokenProvider, entryPoint);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("✅ 유효한 JWT → ROLE_USER 부여")
    void validJwt_setsAuthentication() throws ServletException, IOException {
        var request = requestWithToken("/api/protected", DEFAULT_VALID_TOKEN);
        var response = response();
        var chain = chain();

        when(jwtTokenProvider.isTokenValid(DEFAULT_VALID_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUserId(DEFAULT_VALID_TOKEN)).thenReturn("user@example.com");
        when(jwtTokenProvider.getRole(DEFAULT_VALID_TOKEN)).thenReturn("ROLE_USER");

        filter.doFilterInternal(request, response, chain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities()).extracting("authority").contains("ROLE_USER");
        assertThat(auth.getPrincipal()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("✅ Authorization 헤더 없음 → 인증 없이 흐름 유지")
    void noAuthorizationHeader_passesThrough() throws ServletException, IOException {
        var request = requestWithoutToken("/api/public");
        var response = response();
        var chain = chain();

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("✅ 잘못된 JWT → EntryPoint 작동")
    void invalidJwt_triggersEntryPoint() throws ServletException, IOException {
        var request = requestWithToken("/api/protected", DEFAULT_INVALID_TOKEN);
        var response = response();
        var chain = chain();

        when(jwtTokenProvider.isTokenValid(DEFAULT_INVALID_TOKEN)).thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        verify(entryPoint).commence(any(), any(), any());
    }

    @Test
    @DisplayName("✅ Authorization 헤더 형식이 잘못된 경우 → 인증 없이 흐름 유지")
    void invalidAuthorizationHeaderFormat_passesThrough() throws ServletException, IOException {
        var request = requestWithoutToken("/api/protected");
        request.addHeader("Authorization", "Token invalid-format"); // Bearer 아님
        var response = response();
        var chain = chain();

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("✅ 'Bearer '만 있고 토큰 없음 → 인증 없이 흐름 유지")
    void emptyBearerToken_passesThrough() throws ServletException, IOException {
        var request = requestWithToken("/api/protected", "");
        var response = response();
        var chain = chain();

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("✅ 유효한 JWT + Role 없음 → 권한 없음 상태로 인증")
    void validJwt_withoutRole_setsAuthWithoutAuthorities() throws ServletException, IOException {
        var request = requestWithToken("/api/protected", DEFAULT_VALID_TOKEN);
        var response = response();
        var chain = chain();

        when(jwtTokenProvider.isTokenValid(DEFAULT_VALID_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUserId(DEFAULT_VALID_TOKEN)).thenReturn("user@example.com");
        when(jwtTokenProvider.getRole(DEFAULT_VALID_TOKEN)).thenReturn(null); // Role 없음

        filter.doFilterInternal(request, response, chain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities()).isEmpty();
        assertThat(auth.getPrincipal()).isEqualTo("user@example.com");
    }
}
