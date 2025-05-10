package com.kingkit.auth_service.fixture;

import com.kingkit.auth_service.dto.LoginRequestDto;

public class LoginRequestFixture {

    public static LoginRequestDto valid() {
        return new LoginRequestDto("test@email.com", "password");
    }

    public static LoginRequestDto withEmail(String email) {
        return new LoginRequestDto(email, "password");
    }

    public static LoginRequestDto withPassword(String password) {
        return new LoginRequestDto("test@email.com", password);
    }

    public static LoginRequestDto custom(String email, String password) {
        return new LoginRequestDto(email, password);
    }
}
