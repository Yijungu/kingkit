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
import lombok.extern.slf4j.Slf4j;

import feign.FeignException;
import jakarta.transaction.Transactional;

import com.kingkit.auth_service.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
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
    @Transactional
    public LoginResponseDto reissue(ReissueRequestDto request) {
        final String refreshToken = request.getRefreshToken();

        // 1. 유효성 검증
        if (!jwtTokenProvider.isTokenValid(refreshToken)) {
            throw new InvalidTokenException("유효하지 않은 Refresh Token입니다.");
        }

        // 2. 사용자 정보 추출
        final String email = jwtTokenProvider.getUserId(refreshToken);

        // 3. 저장된 토큰 조회
        final RefreshToken saved = refreshTokenRepository.findByEmail(email)
            .orElseThrow(() -> new InvalidTokenException("저장된 Refresh Token이 없습니다."));

        // 4. 재사용 방지 (토큰 재사용 감지 가능)
        if (!saved.getToken().equals(refreshToken)) {
            // Optional: saved 토큰을 폐기 처리해도 됨 (토큰 탈취 가능성 있음)
            refreshTokenRepository.delete(saved);
            throw new InvalidTokenException("Refresh Token이 일치하지 않습니다.");
        }

        // 5. role은 직접 담지 않고, 필요 시 외부에서 다시 조회
        String role = saved.getRole(); // 저장해둔 경우
        // 또는: userClient.getUserRole(email);

        // 6. 새 토큰 발급
        final String newAccessToken = jwtTokenProvider.createAccessToken(email, role);
        final String newRefreshToken = jwtTokenProvider.createRefreshToken(email);

        // 7. 토큰 갱신 (or invalidate 이전 토큰)
        saved.update(newRefreshToken);
        refreshTokenRepository.save(saved);

        // 8. 로깅 (감사 추적)
        log.info("Refresh token 재발급: email={}, newAccessTokenIssued=true", email);

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }


    @Override
    public void logout(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }

}
