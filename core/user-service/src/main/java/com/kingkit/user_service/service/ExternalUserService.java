package com.kingkit.user_service.service;

import com.kingkit.user_service.domain.User;

import java.util.Optional;

public interface ExternalUserService {
    User registerUser(String email, String password, String nickname, String profileImageUrl);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
}
