package com.kingkit.auth_service.fixture;

import com.kingkit.auth_service.dto.LoginRequestDto;
import com.kingkit.auth_service.dto.ReissueRequestDto;

public class AuthDtoFixture {

    public static LoginRequestDto login(String email, String password) {
        return new LoginRequestDto(email, password);
    }

    public static ReissueRequestDto reissue(String token) {
        return new ReissueRequestDto(token);
    }
}
