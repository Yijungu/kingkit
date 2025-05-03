package com.kingkit.auth_service.controller;

import com.kingkit.auth_service.dto.LoginRequestDto;
import com.kingkit.auth_service.dto.LoginResponseDto;
import com.kingkit.auth_service.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 반환합니다.")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
