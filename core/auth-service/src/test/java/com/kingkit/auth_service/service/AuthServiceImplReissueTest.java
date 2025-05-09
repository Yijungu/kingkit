package com.kingkit.auth_service.service;

import com.kingkit.auth_service.domain.RefreshToken;
import com.kingkit.auth_service.dto.LoginResponseDto;
import com.kingkit.auth_service.dto.ReissueRequestDto;
import com.kingkit.auth_service.exception.InvalidTokenException;
import com.kingkit.auth_service.feign.UserClient;
import com.kingkit.auth_service.repository.RefreshTokenRepository;
import com.kingkit.lib_security.jwt.JwtTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplReissueTest {

    @Mock UserClient userClient;
    @Mock JwtTokenProvider jwtTokenProvider;
    @Mock PasswordEncoder passwordEncoder;
    @Mock RefreshTokenRepository refreshTokenRepository;

    @InjectMocks AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("✅ 정상적인 리프레시 토큰 재발급 성공")
    void reissue_success() {
        // given
        String email = "test@example.com";
        String role = "USER";
        String oldToken = "valid.refresh.token";
        String newAccessToken = "access.token";
        String newRefreshToken = "refresh.token";

        ReissueRequestDto requestDto = new ReissueRequestDto(oldToken);

        RefreshToken savedToken = RefreshToken.builder()
                .email(email)
                .token(oldToken)
                .role(role)
                .build();

        when(jwtTokenProvider.isTokenValid(oldToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(oldToken)).thenReturn(email);
        when(refreshTokenRepository.findByEmail(email)).thenReturn(Optional.of(savedToken));
        when(jwtTokenProvider.createAccessToken(email, role)).thenReturn(newAccessToken);
        when(jwtTokenProvider.createRefreshToken(email)).thenReturn(newRefreshToken);

        // when
        LoginResponseDto result = authService.reissue(requestDto);

        // then
        assertThat(result.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(result.getRefreshToken()).isEqualTo(newRefreshToken);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("❌ 유효하지 않은 리프레시 토큰이면 예외 발생")
    void reissue_invalidToken() {
        String invalidToken = "invalid.token";
        ReissueRequestDto request = new ReissueRequestDto(invalidToken);

        when(jwtTokenProvider.isTokenValid(invalidToken)).thenReturn(false);

        assertThatThrownBy(() -> authService.reissue(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("유효하지 않은");
    }

    @Test
    @DisplayName("❌ 저장된 토큰이 없으면 예외 발생")
    void reissue_tokenNotFoundInDb() {
        String token = "valid.token";
        String email = "test@example.com";

        ReissueRequestDto request = new ReissueRequestDto(token);

        when(jwtTokenProvider.isTokenValid(token)).thenReturn(true);
        when(jwtTokenProvider.getUserId(token)).thenReturn(email);
        when(refreshTokenRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.reissue(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("저장된");
    }

    @Test
    @DisplayName("❌ 저장된 토큰과 일치하지 않으면 예외 발생")
    void reissue_tokenMismatch() {
        String providedToken = "provided.token";
        String dbToken = "different.token";
        String email = "test@example.com";

        ReissueRequestDto request = new ReissueRequestDto(providedToken);

        when(jwtTokenProvider.isTokenValid(providedToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(providedToken)).thenReturn(email);
        when(refreshTokenRepository.findByEmail(email))
                .thenReturn(Optional.of(
                        RefreshToken.builder()
                                .email(email)
                                .token(dbToken)
                                .role("USER")
                                .build()));

        assertThatThrownBy(() -> authService.reissue(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("일치하지 않습니다");
    }
}
