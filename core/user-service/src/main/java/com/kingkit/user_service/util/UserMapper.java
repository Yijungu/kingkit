package com.kingkit.user_service.util;

import com.kingkit.user_service.domain.User;
import com.kingkit.lib_dto.UserDto;

public final class UserMapper {

    private UserMapper() {} // 유틸 클래스이므로 생성자 private

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
