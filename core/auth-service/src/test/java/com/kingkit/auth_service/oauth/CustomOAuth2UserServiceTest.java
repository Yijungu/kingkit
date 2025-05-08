package com.kingkit.auth_service.oauth;

import com.kingkit.auth_service.feign.UserClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomOAuth2UserServiceTest {

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Mock
    private UserClient userClient;

    @Mock
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customOAuth2UserService = new CustomOAuth2UserService(userClient);

        // VisibleForTesting 용도
        customOAuth2UserService.setDelegate(delegate);
    }

    @Test
    @DisplayName("정상 로그인: 이메일, 닉네임 포함 → DefaultOAuth2User 생성")
    void loadUser_success() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "test@example.com");
        attributes.put("name", "tester");  // nickname → name 변경!

        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.singleton(new OAuth2UserAuthority(attributes)),
                attributes,
                "email"
        );

        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        when(delegate.loadUser(userRequest)).thenReturn(oAuth2User);
        when(userClient.existsByEmail("test@example.com")).thenReturn(true);

        // when
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // then
        String email = result.getAttribute("email");
        String name = result.getAttribute("name");
        assertThat(email).isEqualTo("test@example.com");
        assertThat(name).isEqualTo("tester");
        assertThat(result.getAuthorities())
                .extracting("authority")
                .contains("ROLE_USER");
    }

    @Test
    @DisplayName("실패: 이메일 없음 → OAuth2AuthenticationException 발생")
    void loadUser_missingEmail() {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "tester");  // email intentionally missing

        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.singleton(new OAuth2UserAuthority(attributes)),
                attributes,
                "name"
        );

        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        when(delegate.loadUser(userRequest)).thenReturn(oAuth2User);

        // when & then
        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(OAuth2AuthenticationException.class)
                .hasMessageContaining("email 또는 name");
    }
}
