package com.kingkit.user_service.controller.internal;

import com.kingkit.user_service.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kingkit.user_service.dto.UserInternalDto;
import com.kingkit.user_service.dto.UserResponseDto;
import com.kingkit.user_service.service.InternalUserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/users")
@Tag(name = "Internal API", description = "내부 시스템 연동 전용 API")
@RequiredArgsConstructor
public class UserInternalController {

    private final InternalUserService userService;

    @GetMapping("/email")
    public ResponseEntity<UserInternalDto> getUserInternalByEmail(@RequestParam String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return ResponseEntity.ok(new UserInternalDto(user));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    @PostMapping("/oauth")
    public ResponseEntity<UserResponseDto> createOAuthUser(
            @RequestParam String email,
            @RequestParam String nickname,
            @RequestParam(required = false) String provider
    ) {
        User user = userService.registerOAuthUser(email, nickname);
        return ResponseEntity.ok(new UserResponseDto(user));
    }
}
