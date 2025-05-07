package com.kingkit.user_service.controller;

import com.kingkit.user_service.controller.internal.InternalUserController;
import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.exception.GlobalExceptionHandler;
import com.kingkit.user_service.service.InternalUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InternalUserController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class InternalUserValidationTest {

    @Autowired MockMvc mockMvc;
    @MockBean InternalUserService internalUserService;

    @Test
    @DisplayName("이메일 누락 시 → 400 Bad Request")
    void getUserByEmail_missingEmail() throws Exception {
        mockMvc.perform(get("/internal/users/email"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ILLEGAL_ARGUMENT"));
    }

    @Test
    @DisplayName("존재 여부 확인: 이메일 누락 시 → 400 Bad Request")
    void existsByEmail_missingEmail() throws Exception {
        mockMvc.perform(get("/internal/users/exists"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ILLEGAL_ARGUMENT"));
    }

    @Test
    @DisplayName("OAuth 회원가입: email 없음 → 400 Bad Request")
    void registerOAuthUser_missingEmail() throws Exception {
        mockMvc.perform(post("/internal/users/oauth")
                        .param("nickname", "tester"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ILLEGAL_ARGUMENT"));
    }

    @Test
    @DisplayName("OAuth 회원가입: nickname 없음 → 400 Bad Request")
    void registerOAuthUser_missingNickname() throws Exception {
        mockMvc.perform(post("/internal/users/oauth")
                        .param("email", "test@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ILLEGAL_ARGUMENT"));
    }

    @Test
    @DisplayName("정상 요청 시 → 200 OK")
    void registerOAuthUser_success() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("tester")
                .role("ROLE_USER")
                .build();

        given(internalUserService.registerOAuthUser(any(), any()))
                .willReturn(user);

        mockMvc.perform(post("/internal/users/oauth")
                        .param("email", "test@example.com")
                        .param("nickname", "tester")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nickname").value("tester"));
    }
}
