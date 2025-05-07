package com.kingkit.auth_service.oauth;

import com.kingkit.auth_service.repository.RefreshTokenRepository;
import com.kingkit.lib_security.jwt.JwtTokenProvider;
import com.kingkit.auth_service.domain.RefreshToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String email = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        String accessToken = jwtTokenProvider.createAccessToken(email, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        // DB에 RefreshToken 저장
        RefreshToken token = RefreshToken.builder()
                .email(email)
                .token(refreshToken)
                .build();
        refreshTokenRepository.save(token);

        // 클라이언트로 Redirect (프론트 URL에 맞게 수정)
        String redirectUrl = "http://localhost:3000/oauth/success?accessToken=" + accessToken + "&refreshToken=" + refreshToken;
        response.sendRedirect(redirectUrl);
    }
}
