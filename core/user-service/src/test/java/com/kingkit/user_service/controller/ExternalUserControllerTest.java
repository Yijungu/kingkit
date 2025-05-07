package com.kingkit.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.user_service.controller.external.ExternalUserController;
import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.dto.UserRequestDto;
import com.kingkit.user_service.exception.DuplicateEmailException;
import com.kingkit.user_service.service.ExternalUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExternalUserController.class)
@AutoConfigureMockMvc(addFilters = false)  
@Import({com.kingkit.user_service.exception.GlobalExceptionHandler.class})
@TestPropertySource(properties = {
        "spring.config.location=classpath:/application-test.yml"
})
class ExternalUserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ExternalUserService userService;

    private String jwt;
    private final User testUser = User.builder()
            .id(1L)
            .email("test@example.com")
            .password("encoded-password")
            .nickname("tester")
            .profileImageUrl("http://image.com/profile.png")
            .role("USER")
            .build();

    @BeforeEach
    void setUp() {
        // 그냥 모의 JWT 문자열 사용 (실제 인증 로직 안탐)
        jwt = "mocked.jwt.token";
    }

    @Test
    @DisplayName("회원가입 성공 → 201 Created")
    void registerUser_정상() throws Exception {
        UserRequestDto request = new UserRequestDto("test@example.com", "pw123", "tester", "http://image.com");

        given(userService.registerUser(any(), any(), any(), any())).willReturn(testUser);

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nickname").value("tester"));
    }

    @Test
    @DisplayName("중복 이메일 → 409 Conflict")
    void registerUser_중복_예외() throws Exception {
        UserRequestDto request = new UserRequestDto("test@example.com", "pw123", "tester", "http://image.com");

        given(userService.registerUser(any(), any(), any(), any()))
                .willThrow(new DuplicateEmailException("이미 사용 중인 이메일입니다."));

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));
    }

    @Test
    @DisplayName("입력 누락 → 400 Bad Request (DTO Validation)")
    void registerUser_입력값_누락() throws Exception {
        UserRequestDto invalid = new UserRequestDto(null, "", "", "http://img.com");

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("이메일로 유저 조회 성공")
    void getUserByEmail_정상() throws Exception {
        given(userService.findByEmail("test@example.com")).willReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/email")
                        .header("Authorization", "Bearer " + jwt)
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("이메일 조회 → 유저 없음 → 400")
    void getUserByEmail_유저없음() throws Exception {
        given(userService.findByEmail("noone@example.com")).willReturn(Optional.empty());

        mockMvc.perform(get("/users/email")
                        .header("Authorization", "Bearer " + jwt)
                        .param("email", "noone@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("ID로 유저 조회 성공")
    void getUserById_정상() throws Exception {
        given(userService.findById(1L)).willReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/1")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("ID 조회 → 유저 없음 → 400")
    void getUserById_유저없음() throws Exception {
        given(userService.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(get("/users/999")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
