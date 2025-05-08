package com.kingkit.auth_service.oauth;

import com.kingkit.auth_service.domain.RefreshToken;
import com.kingkit.auth_service.repository.RefreshTokenRepository;
import com.kingkit.lib_security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OAuth2SuccessHandlerTest {

    @InjectMocks
    private OAuth2SuccessHandler successHandler;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        successHandler = new OAuth2SuccessHandler(jwtTokenProvider, refreshTokenRepository);
    }

    @Test
    @DisplayName("OAuth2 로그인 성공 시 JWT 발급 및 리다이렉트 처리")
    void onAuthenticationSuccess_shouldGenerateTokensAndRedirect() throws IOException {
        // given
        String email = "test@example.com";
        String role = "ROLE_USER";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(role)),
                Collections.singletonMap("email", email),
                "email"
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.createAccessToken(email, role)).thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(email)).thenReturn(refreshToken);

        // when
        successHandler.onAuthenticationSuccess(request, response, new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), "google"));

        // then
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        assertThat(response.getRedirectedUrl()).contains("accessToken=" + accessToken);
        assertThat(response.getRedirectedUrl()).contains("refreshToken=" + refreshToken);
    }
}
