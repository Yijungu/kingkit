package com.kingkit.user_service.service;

import com.kingkit.user_service.domain.User;

import java.util.Optional;

public interface UserService {

    User registerUser(String email, String password, String nickname, String profileImageUrl);

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findById(Long id);
}
