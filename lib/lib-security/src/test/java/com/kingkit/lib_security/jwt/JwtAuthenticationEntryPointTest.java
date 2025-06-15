package com.kingkit.lib_security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JwtAuthenticationEntryPointTest {

    @Test
    @DisplayName("✅ 인증 실패 시 401 상태 코드와 메시지 반환")
    void commence_shouldSendUnauthorized() throws IOException, ServletException {
        // given
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        var exception = new BadCredentialsException("Invalid JWT");

        // when
        entryPoint.commence(request, response, exception);

        // then
        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), contains("인증이 필요합니다."));
    }
}
