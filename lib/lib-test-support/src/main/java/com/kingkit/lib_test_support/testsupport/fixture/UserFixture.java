package com.kingkit.lib_test_support.testsupport.fixture;

import com.kingkit.lib_dto.UserDto;

public class UserFixture {

    public static UserDto sampleUser() {
        return new UserDto(
                1L,
                "test@example.com",
                "encoded-password",
                "ROLE_USER",
                "tester",
                "http://image.com/profile.png"
        );
    }

    public static UserDto withEmail(String email) {
        return new UserDto(
                1L,
                email,
                "encoded-password",
                "ROLE_USER",
                "tester",
                "http://image.com/profile.png"
        );
    }

    public static UserDto withId(Long id) {
        return new UserDto(
                id,
                "test@example.com",
                "encoded-password",
                "ROLE_USER",
                "tester",
                "http://image.com/profile.png"
        );
    }

    public static UserDto withRole(String role) {
        return new UserDto(
                1L,
                "test@example.com",
                "encoded-password",
                role,
                "tester",
                "http://image.com/profile.png"
        );
    }

    public static UserDto withNickname(String nickname) {
        return new UserDto(
                1L,
                "test@example.com",
                "encoded-password",
                "ROLE_USER",
                nickname,
                "http://image.com/profile.png"
        );
    }

    public static UserDto withProfileImage(String imageUrl) {
        return new UserDto(
                1L,
                "test@example.com",
                "encoded-password",
                "ROLE_USER",
                "tester",
                imageUrl
        );
    }

    public static UserDto custom(
            Long id,
            String email,
            String password,
            String role,
            String nickname,
            String profileImageUrl
    ) {
        return new UserDto(id, email, password, role, nickname, profileImageUrl);
    }
}
