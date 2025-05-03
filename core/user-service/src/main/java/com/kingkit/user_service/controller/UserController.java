package com.kingkit.user_service.controller;

import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.dto.UserInternalDto;
import com.kingkit.user_service.dto.UserRequestDto;
import com.kingkit.user_service.dto.UserResponseDto;
import com.kingkit.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/users")
@Tag(name = "User API", description = "회원 관리 API")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRequestDto request) {
        User user = userService.registerUser(
                request.getEmail(),
                request.getPassword(),
                request.getNickname(),
                request.getProfileImageUrl()
        );
        return ResponseEntity.ok(new UserResponseDto(user));
    }

    @Operation(summary = "이메일로 회원 조회", description = "이메일을 이용해 회원 정보를 조회합니다.")
    @GetMapping("/email")
    public ResponseEntity<UserResponseDto> getUserByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return ResponseEntity.ok(new UserResponseDto(user));
    }

    @Operation(summary = "ID로 회원 조회", description = "ID를 이용해 회원 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return ResponseEntity.ok(new UserResponseDto(user));
    }

    @Operation(summary = "[Internal] 이메일로 회원 조회 (비밀번호 포함)", 
           description = "Feign 등 내부 시스템에서 이메일로 회원 정보를 가져올 때 사용합니다. 비밀번호가 포함됩니다.")
    @GetMapping("/internal/email")
    public ResponseEntity<UserInternalDto> getUserInternalByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return ResponseEntity.ok(new UserInternalDto(user));
    }

}
