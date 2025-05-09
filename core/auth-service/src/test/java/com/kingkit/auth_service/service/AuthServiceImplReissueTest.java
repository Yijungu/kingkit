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
    @DisplayName("정상적인 리프레시 토큰 재발급 성공")
    void reissue_success() {
        // given
        String email = "test@example.com";
        String role = "USER";
        String oldToken = "old.refresh.token";
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";

        ReissueRequestDto dto = new ReissueRequestDto(oldToken);

        when(jwtTokenProvider.isTokenValid(oldToken)).thenReturn(true);
        when(jwtTokenProvider.getUserId(oldToken)).thenReturn(email);
        when(jwtTokenProvider.getRole(oldToken)).thenReturn(role);

        RefreshToken savedToken = RefreshToken.builder()
                .email(email)
                .token(oldToken)
                .build();

        when(refreshTokenRepository.findByEmail(email)).thenReturn(Optional.of(savedToken));
        when(jwtTokenProvider.createAccessToken(email, role)).thenReturn(newAccessToken);
        when(jwtTokenProvider.createRefreshToken(email)).thenReturn(newRefreshToken);

        // when
        LoginResponseDto response = authService.reissue(dto);

        // then
        assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("리프레시 토큰이 유효하지 않으면 예외 발생")
    void reissue_invalidToken() {
        // given
        String invalidToken = "invalid.token";
        ReissueRequestDto dto = new ReissueRequestDto(invalidToken);

        when(jwtTokenProvider.isTokenValid(invalidToken)).thenReturn(false);

        // then
        assertThatThrownBy(() -> authService.reissue(dto))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("유효하지 않습니다.");
    }

    @Test
    @DisplayName("DB에 저장된 토큰이 없으면 예외 발생")
    void reissue_tokenNotFoundInDb() {
        // given
        String token = "some.token";
        String email = "test@example.com";

        ReissueRequestDto dto = new ReissueRequestDto(token);

        when(jwtTokenProvider.isTokenValid(token)).thenReturn(true);
        when(jwtTokenProvider.getUserId(token)).thenReturn(email);
        when(refreshTokenRepository.findByEmail(email)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> authService.reissue(dto))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("저장된 Refresh Token이 없습니다.");
    }

    @Test
    @DisplayName("리프레시 토큰이 DB와 다르면 예외 발생")
    void reissue_tokenMismatch() {
        // given
        String token = "provided.token";
        String dbToken = "different.token";
        String email = "test@example.com";

        ReissueRequestDto dto = new ReissueRequestDto(token);

        when(jwtTokenProvider.isTokenValid(token)).thenReturn(true);
        when(jwtTokenProvider.getUserId(token)).thenReturn(email);
        when(refreshTokenRepository.findByEmail(email))
                .thenReturn(Optional.of(RefreshToken.builder().email(email).token(dbToken).build()));

        // then
        assertThatThrownBy(() -> authService.reissue(dto))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("일치하지 않습니다.");
    }
}
