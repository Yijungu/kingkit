package com.kingkit.user_service.service;

import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.dto.UserRequestDto;
import com.kingkit.user_service.exception.DuplicateEmailException;
import com.kingkit.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ✅ 추가

    @Override
    @Transactional
    public User registerUser(String email, String password, String nickname, String profileImageUrl) {
        // ✅ 1. 이메일 중복 검사
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다: " + email);
        }

        // ✅ 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // ✅ 3. User 생성 및 저장
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
