package com.kingkit.user_service.service;

import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternalUserServiceImpl implements InternalUserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public User registerOAuthUser(String email, String nickname) {
        User user = User.builder()
                .email(email)
                .password("") // OAuth2는 패스워드가 없음
                .nickname(nickname)
                .role("SOCIAL")
                .build();

        return userRepository.save(user);
    }
}
