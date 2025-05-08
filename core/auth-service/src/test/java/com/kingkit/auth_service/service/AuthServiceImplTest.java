package com.kingkit.auth_service.service;

import com.kingkit.auth_service.domain.RefreshToken;
import com.kingkit.auth_service.dto.LoginRequestDto;
import com.kingkit.auth_service.dto.LoginResponseDto;
import com.kingkit.auth_service.dto.ReissueRequestDto;
import com.kingkit.auth_service.exception.*;
import com.kingkit.auth_service.feign.UserClient;
import com.kingkit.auth_service.feign.dto.UserDto;
import com.kingkit.auth_service.repository.RefreshTokenRepository;
import com.kingkit.lib_security.jwt.JwtTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class AuthServiceImplTest {

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
    void login_성공() {
        // given
        var request = new LoginRequestDto("test@email.com", "password");
        var user = new UserDto(1L, "test@email.com", "encodedPwd", "nickname", "", "ROLE_USER");

        given(userClient.getUserByEmail(request.getEmail())).willReturn(user);
        given(passwordEncoder.matches(request.getPassword(), user.getPassword())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(any(), any())).willReturn("accessToken");
        given(jwtTokenProvider.createRefreshToken(any())).willReturn("refreshToken");

        // when
        LoginResponseDto result = authService.login(request);

        // then
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
    }

    @Test
    void login_이메일없음_예외() {
        given(userClient.getUserByEmail("no@email.com")).willReturn(null);

        assertThatThrownBy(() -> authService.login(new LoginRequestDto("no@email.com", "pwd")))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void login_비밀번호불일치_예외() {
        var user = new UserDto(1L, "user@email.com", "encodedPwd", "nickname", "", "ROLE_USER");

        given(userClient.getUserByEmail(user.getEmail())).willReturn(user);
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequestDto(user.getEmail(), "wrong")))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    void reissue_성공() {
        String refreshToken = "refreshToken";
        String email = "email@test.com";
        String role = "ROLE_USER";

        given(jwtTokenProvider.isTokenValid(refreshToken)).willReturn(true);
        given(jwtTokenProvider.getUserId(refreshToken)).willReturn(email);
        given(jwtTokenProvider.getRole(refreshToken)).willReturn(role);
        given(refreshTokenRepository.findByEmail(email)).willReturn(Optional.of(new RefreshToken(email, refreshToken)));
        given(jwtTokenProvider.createAccessToken(email, role)).willReturn("newAccess");
        given(jwtTokenProvider.createRefreshToken(email)).willReturn("newRefresh");

        LoginResponseDto result = authService.reissue(new ReissueRequestDto(refreshToken));

        assertThat(result.getAccessToken()).isEqualTo("newAccess");
        assertThat(result.getRefreshToken()).isEqualTo("newRefresh");
    }

    @Test
    void reissue_유효하지않은토큰() {
        given(jwtTokenProvider.isTokenValid(any())).willReturn(false);

        assertThatThrownBy(() -> authService.reissue(new ReissueRequestDto("badToken")))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void reissue_저장된토큰과다름() {
        String token = "incoming";
        String email = "email@test.com";

        given(jwtTokenProvider.isTokenValid(token)).willReturn(true);
        given(jwtTokenProvider.getUserId(token)).willReturn(email);
        given(jwtTokenProvider.getRole(token)).willReturn("ROLE_USER");
        given(refreshTokenRepository.findByEmail(email)).willReturn(Optional.of(new RefreshToken(email, "different")));

        assertThatThrownBy(() -> authService.reissue(new ReissueRequestDto(token)))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void logout_성공() {
        authService.logout("email@test.com");

        then(refreshTokenRepository).should().deleteByEmail("email@test.com");
    }
}
