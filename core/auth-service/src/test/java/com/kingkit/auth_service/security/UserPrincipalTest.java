package com.kingkit.auth_service.security;

import com.kingkit.lib_dto.UserDto;
import com.kingkit.lib_test_support.testsupport.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPrincipalTest {

    @Test
    @DisplayName("UserPrincipal 생성 및 UserDetails 메서드 동작 확인")
    void 사용자정보_매핑_및_UserDetails_구현_확인() {
        // given
        UserDto userDto = UserFixture.sampleUser();

        // when
        UserPrincipal principal = new UserPrincipal(userDto);

        // then
        assertThat(principal.getId()).isEqualTo(userDto.getId());
        assertThat(principal.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(principal.getPassword()).isEqualTo(userDto.getPassword());
        assertThat(principal.getRole()).isEqualTo(userDto.getRole());
        assertThat(principal.getNickname()).isEqualTo(userDto.getNickname());
        assertThat(principal.getProfileImageUrl()).isEqualTo(userDto.getProfileImageUrl());

        assertThat(principal.getUsername()).isEqualTo(userDto.getEmail());
        assertThat(principal.getAuthorities()).isEmpty();
        assertThat(principal.isAccountNonExpired()).isTrue();
        assertThat(principal.isAccountNonLocked()).isTrue();
        assertThat(principal.isCredentialsNonExpired()).isTrue();
        assertThat(principal.isEnabled()).isTrue();
    }
}
