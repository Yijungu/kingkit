package com.kingkit.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.auth_service.exception.GlobalExceptionHandler;
import com.kingkit.auth_service.fixture.AuthDtoFixture;
import com.kingkit.auth_service.service.AuthService;
import com.kingkit.lib_test_support.testsupport.util.MockRequestBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerValidationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AuthService authService;

    @Nested
    @DisplayName("LoginRequestDto 검증")
    class LoginDtoValidation {

        @Test @DisplayName("이메일이 null → 400 / message 확인")
        void emailIsNull() throws Exception {
            var dto = AuthDtoFixture.login(null, "password123");

            mockMvc.perform(MockRequestBuilder.postJson("/auth/login", dto, objectMapper))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.status").value(400))
                   .andExpect(jsonPath("$.message").exists());
        }

        @Test @DisplayName("비밀번호가 null → 400 / message 확인")
        void passwordIsNull() throws Exception {
            var dto = AuthDtoFixture.login("test@example.com", null);

            mockMvc.perform(MockRequestBuilder.postJson("/auth/login", dto, objectMapper))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.status").value(400))
                   .andExpect(jsonPath("$.message").exists());
        }
    }

    @Nested
    @DisplayName("ReissueRequestDto 검증")
    class ReissueDtoValidation {

        @Test @DisplayName("refreshToken이 null → 400 / message 확인")
        void refreshTokenIsNull() throws Exception {
            var dto = AuthDtoFixture.reissue(null);

            mockMvc.perform(MockRequestBuilder.postJson("/auth/reissue", dto, objectMapper))
                   .andExpect(status().isBadRequest())
                   .andExpect(jsonPath("$.status").value(400))
                   .andExpect(jsonPath("$.message").exists());
        }
    }
}
