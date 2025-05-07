package com.kingkit.user_service.service;

import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.exception.DuplicateEmailException;
import com.kingkit.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import org.mockito.MockitoAnnotations;

class ExternalUserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private ExternalUserServiceImpl externalUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 성공 시 저장된 유저 필드 확인")
    void register_성공() {
        // given
        String email = "user@example.com";
        String password = "raw123";
        String encodedPassword = "encoded123";
        String nickname = "nickname";

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        User user = externalUserService.registerUser(email, password, nickname, "");

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("중복 이메일 등록 시 DuplicateEmailException 발생")
    void register_중복이메일_예외() {
        // given
        String email = "duplicate@example.com";
        given(userRepository.existsByEmail(email)).willReturn(true);

        // expect
        assertThatThrownBy(() -> externalUserService.registerUser(email, "pwd", "nick", ""))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("이미 사용 중인 이메일");
    }

    @Test
    @DisplayName("비밀번호 인코딩이 실제로 적용되는지 확인")
    void password_인코딩_적용확인() {
        // given
        String rawPassword = "mypassword";
        String encoded = "encoded_pass";
        given(userRepository.existsByEmail(any())).willReturn(false);
        given(passwordEncoder.encode(rawPassword)).willReturn(encoded);
        given(userRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        User user = externalUserService.registerUser("email@test.com", rawPassword, "nick", "");

        // then
        assertThat(user.getPassword()).isEqualTo(encoded);
        assertThat(user.getPassword()).isNotEqualTo(rawPassword);
    }

    @Test
    @DisplayName("이메일로 유저 조회 성공")
    void findByEmail_성공() {
        // given
        String email = "user@test.com";
        User user = User.builder().id(1L).email(email).build();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when
        Optional<User> result = externalUserService.findByEmail(email);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("ID로 유저 조회 실패")
    void findById_실패() {
        // given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        Optional<User> result = externalUserService.findById(userId);

        // then
        assertThat(result).isEmpty();
    }
}
