package com.kingkit.user_service.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Builder를 통해 User 객체를 생성할 수 있다")
    void userBuilder_정상_생성() {

        // when
        User user = User.builder()
                .email("test@example.com")
                .password("securepassword")
                .nickname("tester")
                .profileImageUrl("http://image.com/profile.png")
                .build();

        // then
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("securepassword");
        assertThat(user.getRole()).isEqualTo("ROLE_USER"); // @Builder.Default
        assertThat(user.getNickname()).isEqualTo("tester");
        assertThat(user.getProfileImageUrl()).isEqualTo("http://image.com/profile.png");
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("preUpdate 메서드가 updatedAt을 변경한다")
    void preUpdate_업데이트시간_갱신() throws InterruptedException {
        // given
        User user = User.builder()
                .email("test@example.com")
                .password("securepassword")
                .build();

        LocalDateTime before = user.getUpdatedAt();

        // wait 10ms to ensure timestamp changes
        Thread.sleep(10);

        // when
        user.preUpdate();

        // then
        assertThat(user.getUpdatedAt()).isAfter(before);
    }
}
