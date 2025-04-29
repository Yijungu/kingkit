package com.kingkit.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor 
public class UserRequestDto {

    @Schema(description = "이메일 주소", example = "test@example.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "비밀번호", example = "password123")
    @NotBlank
    private String password;

    @Schema(description = "닉네임", example = "tester")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "http://image.com/profile.png")
    private String profileImageUrl;
}
