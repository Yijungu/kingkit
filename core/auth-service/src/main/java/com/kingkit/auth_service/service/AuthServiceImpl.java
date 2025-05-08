package com.kingkit.auth_service.service;

import com.kingkit.auth_service.domain.RefreshToken;
import com.kingkit.auth_service.dto.LoginRequestDto;
import com.kingkit.auth_service.dto.LoginResponseDto;
import com.kingkit.auth_service.dto.ReissueRequestDto;
import com.kingkit.auth_service.exception.InvalidTokenException;
import com.kingkit.auth_service.exception.PasswordMismatchException;
import com.kingkit.auth_service.exception.UsernameNotFoundException;
import com.kingkit.auth_service.feign.UserClient;
import com.kingkit.auth_service.feign.dto.UserDto;
import com.kingkit.lib_security.jwt.JwtTokenProvider;

import feign.FeignException;

import com.kingkit.auth_service.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserClient userClient; // ✅ static 아님! DI 주입된 인스턴스
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

   @Override
    public LoginResponseDto login(LoginRequestDto request) {
        UserDto user;
        try {
            user = userClient.getUserByEmail(request.getEmail());
        } catch (FeignException.NotFound e) {
            throw new UsernameNotFoundException("해당 이메일의 유저를 찾을 수 없습니다: " + request.getEmail());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException();
        }
        // 3. 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // ✅ 4. RefreshToken 저장 (있으면 update, 없으면 insert)
        refreshTokenRepository.save(
            RefreshToken.builder()
                .email(user.getEmail())
                .token(refreshToken)
                .build()
        );

        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Override
    public LoginResponseDto reissue(ReissueRequestDto request) {
        String refreshToken = request.getRefreshToken();

        // 1. 유효한지 확인
        if (!jwtTokenProvider.isTokenValid(refreshToken)) {
            throw new InvalidTokenException("Refresh Token이 유효하지 않습니다.");
        }

        // 2. 사용자 정보 추출
        String email = jwtTokenProvider.getUserId(refreshToken);
        String role = jwtTokenProvider.getRole(refreshToken); // 보통 role은 refresh에 없기도 함

        // 3. 저장된 토큰과 일치하는지 확인 (선택적)
        RefreshToken saved = refreshTokenRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidTokenException("저장된 Refresh Token이 없습니다."));

        if (!saved.getToken().equals(refreshToken)) {
            throw new InvalidTokenException("Refresh Token이 일치하지 않습니다.");
        }

        // 4. 새 토큰 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(email, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

        // 5. 리프레시 토큰 갱신
        saved.update(newRefreshToken);
        refreshTokenRepository.save(saved);

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }

}
