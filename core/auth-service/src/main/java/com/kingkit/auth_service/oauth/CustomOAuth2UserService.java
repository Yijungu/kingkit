package com.kingkit.auth_service.oauth;

import com.kingkit.auth_service.feign.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserClient userClient;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String nickname = oAuth2User.getAttribute("name");

        if (email == null || nickname == null) {
            throw new OAuth2AuthenticationException("OAuth2 응답에 email 또는 name 속성이 없습니다.");
        }

        // ✅ user-service 연동: 존재하지 않으면 회원가입 처리
        if (!userClient.existsByEmail(email)) {
            userClient.createOAuthUser(email, nickname, "SOCIAL");
        }

        // ✅ Spring Security가 인식할 수 있도록 OAuth2User 생성
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "email"
        );
    }
}
