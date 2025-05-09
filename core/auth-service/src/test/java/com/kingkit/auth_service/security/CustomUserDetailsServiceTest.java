package com.kingkit.auth_service.security;

import com.kingkit.auth_service.feign.UserClient;
import com.kingkit.lib_dto.UserDto;
import com.kingkit.lib_test_support.testsupport.fixture.UserFixture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock UserClient userClient;
    @InjectMocks CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("정상적으로 유저 정보를 받아 UserPrincipal 반환")
    void loadUserByUsername_정상() {
        // given
        UserDto user = UserFixture.sampleUser();
        given(userClient.getUserByEmail(user.getEmail())).willReturn(user);

        // when
        UserDetails result = userDetailsService.loadUserByUsername(user.getEmail());

        // then
        assertThat(result).isInstanceOf(UserPrincipal.class);
        assertThat(result.getUsername()).isEqualTo(user.getEmail());
        assertThat(result.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    @DisplayName("UserClient 예외 시 UsernameNotFoundException 발생")
    void loadUserByUsername_예외() {
        // given
        String email = "unknown@email.com";
        given(userClient.getUserByEmail(email)).willThrow(new RuntimeException());

        // expect
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
