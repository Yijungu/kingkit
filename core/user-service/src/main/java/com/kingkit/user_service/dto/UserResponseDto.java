package com.kingkit.user_service.dto;

import com.kingkit.user_service.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UserResponseDto {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일 주소", example = "test@example.com")
    private String email;

    @Schema(description = "사용자 닉네임", example = "tester")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "http://image.com/profile.png")
    private String profileImageUrl;

    @Schema(description = "사용자 권한", example = "ROLE_USER")
    private String role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.role = user.getRole();
    }
}
