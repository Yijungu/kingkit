package com.kingkit.auth_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException(String email) {
        super("해당 유저의 리프레시 토큰이 존재하지 않습니다: " + email);
    }
}
