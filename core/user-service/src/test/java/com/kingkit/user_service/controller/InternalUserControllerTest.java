package com.kingkit.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.user_service.controller.internal.InternalUserController;
import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.exception.GlobalExceptionHandler;
import com.kingkit.user_service.service.InternalUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InternalUserController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)  
class InternalUserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean InternalUserService internalUserService;

    @Test
    @DisplayName("OAuth 회원가입 성공 → 201 Created")
    void registerOAuthUser_성공() throws Exception {
        // given
        String email = "test@example.com";
        String nickname = "tester";

        User user = User.builder()
                .id(1L)
                .email(email)
                .password("")
                .nickname(nickname)
                .profileImageUrl("http://image.com/profile.png")
                .role("ROLE_USER")
                .build();

        given(internalUserService.registerOAuthUser(email, nickname))
                .willReturn(user);

        // when & then
        mockMvc.perform(post("/internal/users/oauth")
                        .param("email", email)
                        .param("nickname", nickname)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.nickname").value(nickname))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("이메일로 유저 조회 성공 → 200 OK")
    void findByEmail_성공() throws Exception {
        // given
        String email = "find@example.com";

        User user = User.builder()
                .id(2L)
                .email(email)
                .password("")
                .nickname("founder")
                .profileImageUrl("http://image.com/profile2.png")
                .role("SOCIAL")
                .build();

        given(internalUserService.findByEmail(email)).willReturn(Optional.of(user));

        // when & then
        mockMvc.perform(get("/internal/users/email")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.nickname").value("founder"));
    }

    @Test
    @DisplayName("이메일로 유저 조회 실패 → 400 Bad Request")
    void findByEmail_실패() throws Exception {
        // given
        String email = "notfound@example.com";
        given(internalUserService.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/internal/users/email")
                        .param("email", email))
                .andExpect(status().isBadRequest());
    }
}
