package com.kingkit.auth_service.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException() {
        super("비밀번호가 일치하지 않습니다.");
    }

    public PasswordMismatchException(String message) {
        super(message);
    }
}
