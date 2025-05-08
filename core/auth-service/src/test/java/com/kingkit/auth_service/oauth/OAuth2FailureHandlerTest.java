package com.kingkit.auth_service.oauth;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2FailureHandlerTest {

    private OAuth2FailureHandler failureHandler;

    @BeforeEach
    void setUp() {
        failureHandler = new OAuth2FailureHandler();
    }

    @Test
    @DisplayName("OAuth2 로그인 실패 시 /oauth/failure 로 리다이렉트")
    void onAuthenticationFailure_redirectsToFailurePage() throws ServletException, IOException {
        // given
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var exception = new BadCredentialsException("소셜 로그인 실패!");

        // when
        failureHandler.onAuthenticationFailure(request, response, exception);

        // then
        assertThat(response.getRedirectedUrl()).isEqualTo("http://localhost:3000/oauth/failure");
    }
}
