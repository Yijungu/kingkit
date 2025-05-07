package com.kingkit.lib_security.apikey;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class InternalApiKeyFilterTest {

    private InternalApiKeyFilter filter;
    private final String validKey = "testkey-1234";

    @BeforeEach
    void setUp() {
        filter = new TestableInternalApiKeyFilter(
            Set.of(validKey),
            List.of("127.0.0.1"),
            request -> request.getRequestURI().startsWith("/internal")
        );
        SecurityContextHolder.clearContext(); // 🔁 이전 테스트 인증 정보 클리어
    }

    @Test
    @DisplayName("유효한 API-Key + IP → 필터 통과, ROLE_INTERNAL 부여")
    void validKeyAndIp_pass() throws ServletException, IOException {
        var request = new MockHttpServletRequest("GET", "/internal/api");
        request.addHeader("X-Internal-API-Key", validKey);
        request.setRemoteAddr("127.0.0.1");
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();
            assertThat(auth.getAuthorities()).extracting("authority").contains("ROLE_INTERNAL");
        });
    }

    @Test
    @DisplayName("잘못된 API-Key → 401 Unauthorized")
    void invalidKey_unauthorized() throws ServletException, IOException {
        var request = new MockHttpServletRequest("GET", "/internal/api");
        request.addHeader("X-Internal-API-Key", "wrong-key");
        request.setRemoteAddr("127.0.0.1");
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> fail("통과되면 안 됨"));

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("API-Key 없음 → 401 Unauthorized")
    void missingKey_unauthorized() throws ServletException, IOException {
        var request = new MockHttpServletRequest("GET", "/internal/api");
        request.setRemoteAddr("127.0.0.1");
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> fail("통과되면 안 됨"));

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("허용되지 않은 IP → 403 Forbidden")
    void invalidIp_forbidden() throws ServletException, IOException {
        var request = new MockHttpServletRequest("GET", "/internal/api");
        request.addHeader("X-Internal-API-Key", validKey);
        request.setRemoteAddr("192.168.0.100");
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> fail("통과되면 안 됨"));

        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    @DisplayName("URI 미매칭 → 필터 작동 안 함")
    void uriNotMatch_noFilterEffect() throws ServletException, IOException {
        var request = new MockHttpServletRequest("GET", "/public/api");
        request.addHeader("X-Internal-API-Key", validKey);
        request.setRemoteAddr("127.0.0.1");
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNull(); // shouldNotFilter 작동함
        });
    }
}
