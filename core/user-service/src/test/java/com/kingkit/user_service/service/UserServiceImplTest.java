package com.kingkit.user_service.service;

import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.exception.DuplicateEmailException;
import com.kingkit.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExternalUserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입이 성공적으로 이루어짐")
    void registerUser_Success() {
        // given
        String email = "test@example.com";
        String password = "secure1234!";
        String nickname = "tester";
        String profileImageUrl = "http://image.com/profile.png";

        User dummyUser = User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(dummyUser);

        // when
        User savedUser = userService.registerUser(email, password, nickname, profileImageUrl);

        // then
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getPassword()).isEqualTo(password);
        assertThat(savedUser.getNickname()).isEqualTo(nickname);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 시 DuplicateEmailException이 발생한다")
    void registerUser_DuplicateEmail_ThrowsException() {
        // given
        String email = "duplicate@example.com";
        String password = "secure1234!";
        String nickname = "testuser";

        // 이미 존재하는 이메일을 가진 유저가 리포지토리에 있다고 가정
        given(userRepository.findByEmail(email)).willReturn(Optional.of(User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build()));

        // when, then
        assertThatThrownBy(() -> userService.registerUser(email, password, nickname, null))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageStartingWith("이미 사용 중인 이메일입니다");
    }
}
