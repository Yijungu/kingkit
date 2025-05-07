package com.kingkit.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.user_service.controller.external.ExternalUserController;
import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.dto.UserRequestDto;
import com.kingkit.user_service.service.ExternalUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExternalUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExternalUserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void registerUser_success() throws Exception {
        // given
        UserRequestDto request = new UserRequestDto("test@example.com", "password123", "nickname", "http://image.com/profile.png");
        User savedUser = User.builder()
                .id(1L)
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .profileImageUrl(request.getProfileImageUrl())
                .build();

        Mockito.when(userService.registerUser(any(), any(), any(), any()))
                .thenReturn(savedUser);

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.nickname").value("nickname"));
    }

    @Test
    @DisplayName("이메일로 유저 조회 성공")
    void getUserByEmail_success() throws Exception {
        // given
        String email = "test@example.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .password("password")
                .nickname("nickname")
                .profileImageUrl("http://image.com/profile.png")
                .build();

        Mockito.when(userService.findByEmail(email))
                .thenReturn(Optional.of(user));

        // when & then
        mockMvc.perform(get("/users/email")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @DisplayName("ID로 유저 조회 성공")
    void getUserById_success() throws Exception {
        // given
        Long id = 1L;
        User user = User.builder()
                .id(id)
                .email("test@example.com")
                .password("password")
                .nickname("nickname")
                .profileImageUrl("http://image.com/profile.png")
                .build();

        Mockito.when(userService.findById(id))
                .thenReturn(Optional.of(user));

        // when & then
        mockMvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }
}
