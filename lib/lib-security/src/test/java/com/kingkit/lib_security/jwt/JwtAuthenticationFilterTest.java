package com.kingkit.lib_security.jwt;

import com.kingkit.lib_security.jwt.JwtAuthenticationEntryPoint;
import com.kingkit.lib_security.jwt.JwtAuthenticationFilter;
import com.kingkit.lib_security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

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
    @DisplayName("유효한 JWT → ROLE_USER 부여")
    void validJwt_setsAuthentication() throws ServletException, IOException {
        var request = new MockHttpServletRequest("GET", "/api/protected");
        request.addHeader("Authorization", "Bearer valid.token.here");
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        when(jwtTokenProvider.isTokenValid("valid.token.here")).thenReturn(true);
        when(jwtTokenProvider.getUserId("valid.token.here")).thenReturn("user@example.com");
        when(jwtTokenProvider.getRole("valid.token.here")).thenReturn("ROLE_USER");

        filter.doFilterInternal(request, response, chain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities()).extracting("authority").contains("ROLE_USER");
        assertThat(auth.getPrincipal()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("Authorization 헤더 없음 → 인증 없이 흐름 유지")
    void noAuthorizationHeader_passesThrough() throws ServletException, IOException {
        var request = new MockHttpServletRequest("GET", "/api/public");
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("잘못된 JWT → EntryPoint 작동")
    void invalidJwt_triggersEntryPoint() throws ServletException, IOException {
        var request = new MockHttpServletRequest("GET", "/api/protected");
        request.addHeader("Authorization", "Bearer invalid.token");
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        when(jwtTokenProvider.isTokenValid("invalid.token")).thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        verify(entryPoint).commence(any(), any(), any());
    }
}
