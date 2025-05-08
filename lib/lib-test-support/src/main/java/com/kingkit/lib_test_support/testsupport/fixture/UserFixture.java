package com.kingkit.lib_test_support.testsupport.fixture;

import com.kingkit.lib_dto.UserDto;

public class UserFixture {

    public static UserDto sampleUser() {
        return new UserDto(
                1L,
                "test@example.com",
                "tester",
                "http://image.com/profile.png",
                "ROLE_USER"
        );
    }

    public static UserDto withEmail(String email) {
        return new UserDto(
                1L,
                email,
                "tester",
                "http://image.com/profile.png",
                "ROLE_USER"
        );
    }
}
