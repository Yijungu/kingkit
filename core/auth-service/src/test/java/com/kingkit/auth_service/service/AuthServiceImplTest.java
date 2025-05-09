package com.kingkit.auth_service.service;

import com.kingkit.auth_service.dto.LoginRequestDto;
import com.kingkit.auth_service.dto.LoginResponseDto;
import com.kingkit.auth_service.exception.*;
import com.kingkit.auth_service.feign.UserClient;
import com.kingkit.auth_service.fixture.LoginRequestFixture;
import com.kingkit.auth_service.repository.RefreshTokenRepository;
import com.kingkit.lib_dto.UserDto;
import com.kingkit.lib_security.jwt.JwtTokenProvider;
import com.kingkit.lib_test_support.testsupport.fixture.UserFixture;

import feign.FeignException;
import feign.Request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        LoginRequestDto request = LoginRequestFixture.valid();
        UserDto user = UserFixture.custom(
                1L,
                request.getEmail(),
                "encodedPwd",
                "ROLE_USER",
                "nickname",
                ""
        );

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
        // given
        FeignException notFoundException = new FeignException.NotFound(
                "404 Not Found",
                Request.create(Request.HttpMethod.GET, "/internal/users/email", Map.of(), null, null, null),
                null, null
        );

        given(userClient.getUserByEmail("no@email.com")).willThrow(notFoundException);

        // expect
        assertThatThrownBy(() -> authService.login(LoginRequestFixture.withEmail("no@email.com")))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void login_비밀번호불일치_예외() {
        // given
        UserDto user = UserFixture.withEmail("user@email.com");
        given(userClient.getUserByEmail(user.getEmail())).willReturn(user);
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        // expect
        assertThatThrownBy(() -> authService.login(LoginRequestFixture.withEmail("user@email.com")))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @Test
    void logout_성공() {
        authService.logout("email@test.com");
        then(refreshTokenRepository).should().deleteByEmail("email@test.com");
    }
}
