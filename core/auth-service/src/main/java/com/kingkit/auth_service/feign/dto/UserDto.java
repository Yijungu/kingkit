package com.kingkit.auth_service.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String role;
    private String nickname;
    private String profileImageUrl;
}
