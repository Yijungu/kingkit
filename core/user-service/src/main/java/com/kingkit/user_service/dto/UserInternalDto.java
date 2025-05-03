package com.kingkit.user_service.dto;

import com.kingkit.user_service.domain.User;
import lombok.Getter;

@Getter
public class UserInternalDto {
    private Long id;
    private String email;
    private String password;  // 포함
    private String nickname;
    private String profileImageUrl;
    private String role;

    public UserInternalDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword(); // ✅ 여기가 핵심
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.role = user.getRole();
    }
}
