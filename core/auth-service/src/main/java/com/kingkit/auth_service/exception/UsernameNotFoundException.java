package com.kingkit.auth_service.exception;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(String email) {
        super("해당 이메일의 유저를 찾을 수 없습니다: " + email);
    }
}
