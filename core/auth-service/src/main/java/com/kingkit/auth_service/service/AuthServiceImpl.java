package com.kingkit.auth_service.service;

import com.kingkit.auth_service.dto.LoginRequestDto;
import com.kingkit.auth_service.dto.LoginResponseDto;
import com.kingkit.auth_service.exception.PasswordMismatchException;
import com.kingkit.auth_service.exception.UsernameNotFoundException;
import com.kingkit.auth_service.feign.UserClient;
import com.kingkit.auth_service.feign.dto.UserDto;
import com.kingkit.auth_service.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserClient userClient; // ✅ static 아님! DI 주입된 인스턴스
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        // 1. user-service API 호출
        UserDto user = userClient.getUserByEmail(request.getEmail());  // ✅ 인스턴스 메서드 호출

        if (user == null) {
            throw new UsernameNotFoundException("해당 이메일의 유저를 찾을 수 없습니다: " + request.getEmail());
        }

        System.out.println(">> user 전체: " + user);
        System.out.println(">> 입력한 비밀번호(plain): " + request.getPassword());
        System.out.println(">> 유저 비밀번호(DB): " + user.getPassword());
        System.out.println(">> matches 결과: " + passwordEncoder.matches(request.getPassword(), user.getPassword()));


        // 2. 비밀번호 수동 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException();  // ✅ 커스텀 예외 사용
        }

        // 3. 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        return new LoginResponseDto(accessToken, refreshToken);
    }
}
