package com.kingkit.user_service.service;

import java.util.Optional;

import com.kingkit.user_service.domain.User;

public interface InternalUserService {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User registerOAuthUser(String email, String nickname);
}
