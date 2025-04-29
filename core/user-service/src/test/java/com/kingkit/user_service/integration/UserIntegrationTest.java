package com.kingkit.user_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.user_service.domain.User;
import com.kingkit.user_service.dto.UserRequestDto;
import com.kingkit.user_service.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @Test
    @DisplayName("회원가입 통합 테스트")
    void registerUserIntegrationTest() throws Exception {
        // given
        UserRequestDto requestDto = new UserRequestDto(
                "integration@example.com",
                "password123",
                "integrationTester",
                "http://image.com/profile.png"
        );

        // when
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        // then
        User savedUser = userRepository.findByEmail("integration@example.com")
                .orElseThrow(() -> new RuntimeException("User not found after signup"));

        assertThat(savedUser.getEmail()).isEqualTo("integration@example.com");
        assertThat(savedUser.getNickname()).isEqualTo("integrationTester");
    }
}
