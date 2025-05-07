package com.kingkit.user_service.service;

import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.any;

import org.mockito.MockitoAnnotations;

class InternalUserServiceImplTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private InternalUserServiceImpl internalUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("existsByEmail() → true 반환")
    void existsByEmail_true() {
        // given
        String email = "test@example.com";
        given(userRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThat(internalUserService.existsByEmail(email)).isTrue();
    }

    @Test
    @DisplayName("existsByEmail() → false 반환")
    void existsByEmail_false() {
        // given
        String email = "new@example.com";
        given(userRepository.existsByEmail(email)).willReturn(false);

        // when & then
        assertThat(internalUserService.existsByEmail(email)).isFalse();
    }

    @Test
    @DisplayName("registerOAuthUser() → password는 빈 문자열이고 role은 SOCIAL")
    void registerOAuthUser_성공() {
        // given
        String email = "oauth@example.com";
        String nickname = "oauthUser";

        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        User user = internalUserService.registerOAuthUser(email, nickname);

        // then
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getPassword()).isEmpty();
        assertThat(user.getRole()).isEqualTo("SOCIAL");
    }

    @Test
    @DisplayName("registerOAuthUser() 중복 이메일 → Unique 제약 조건 예외 발생")
    void registerOAuthUser_중복예외() {
        // given
        String email = "duplicate@example.com";
        String nickname = "dupe";

        given(userRepository.save(any(User.class)))
                .willThrow(new EntityExistsException("unique constraint violation"));

        // expect
        assertThatThrownBy(() -> internalUserService.registerOAuthUser(email, nickname))
                .isInstanceOf(EntityExistsException.class)
                .hasMessageContaining("unique constraint");
    }

    @Test
    @DisplayName("findByEmail() → 유저 조회 성공")
    void findByEmail_성공() {
        // given
        String email = "found@example.com";
        User user = User.builder().id(1L).email(email).build();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when
        Optional<User> result = internalUserService.findByEmail(email);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }
}
