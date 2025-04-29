package com.kingkit.user_service.service;

import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder; 
    
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("회원가입(registerUser)")
    class RegisterUserTest {

        @Test
        @DisplayName("성공: 정상적으로 회원가입이 된다")
        void registerUserSuccess() {
            // given
            String email = "test@example.com";
            String password = "password";
            String nickname = "tester";
            String profileImageUrl = "http://example.com/profile.png";

            User savedUser = User.builder()
                    .id(1L)
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .profileImageUrl(profileImageUrl)
                    .build();

            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            // when
            User result = userService.registerUser(email, password, nickname, profileImageUrl);

            // then
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getPassword()).isEqualTo(password);
            assertThat(result.getNickname()).isEqualTo(nickname);
            assertThat(result.getProfileImageUrl()).isEqualTo(profileImageUrl);

            verify(userRepository, times(1)).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("이메일로 회원 조회(findByEmail)")
    class FindByEmailTest {

        @Test
        @DisplayName("성공: 이메일로 회원을 조회할 수 있다")
        void findByEmailSuccess() {
            // given
            String email = "test@example.com";
            User user = User.builder()
                    .id(1L)
                    .email(email)
                    .password("password")
                    .nickname("tester")
                    .build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            // when
            Optional<User> result = userService.findByEmail(email);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo(email);

            verify(userRepository, times(1)).findByEmail(email);
        }
    }
}
