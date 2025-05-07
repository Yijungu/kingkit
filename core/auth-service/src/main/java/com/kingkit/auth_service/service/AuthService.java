package com.kingkit.auth_service.service;

import com.kingkit.auth_service.dto.LoginRequestDto;
import com.kingkit.auth_service.dto.LoginResponseDto;
import com.kingkit.auth_service.dto.ReissueRequestDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
    LoginResponseDto reissue(ReissueRequestDto request);
    void logout(String email);
}
