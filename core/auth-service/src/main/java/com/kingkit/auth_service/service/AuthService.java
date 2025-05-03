package com.kingkit.auth_service.service;

import com.kingkit.auth_service.dto.LoginRequestDto;
import com.kingkit.auth_service.dto.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
}
