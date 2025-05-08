package com.kingkit.auth_service.feign;

import com.kingkit.auth_service.dto.LoginRequestDto;
import com.kingkit.auth_service.exception.UsernameNotFoundException;
import com.kingkit.lib_security.jwt.JwtTokenProvider;
import com.kingkit.auth_service.repository.RefreshTokenRepository;
import com.kingkit.auth_service.service.AuthServiceImpl;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserClientFeignExceptionTest {

    @Mock private UserClient userClient;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("FeignException 발생 시 UsernameNotFoundException으로 변환")
    void feignException_변환확인() {
        // given
        String email = "notfound@example.com";
        LoginRequestDto request = new LoginRequestDto(email, "password");

        when(userClient.getUserByEmail(email))
                .thenThrow(FeignException.NotFound.class);  // simulate 404 error

        // expect
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("해당 이메일의 유저를 찾을 수 없습니다");
    }
}
