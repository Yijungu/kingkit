package com.kingkit.user_service.exception;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FakeExceptionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ComponentScan(basePackages = "com.kingkit.user_service.exception") 
class GlobalExceptionHandlerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    // ✅ DuplicateEmailException → 409
    @Test
    void duplicateEmailException_테스트() throws Exception {
        mockMvc.perform(get("/test/duplicate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));
    }

    // ✅ IllegalArgumentException → 400
    @Test
    void illegalArgumentException_테스트() throws Exception {
        mockMvc.perform(get("/test/illegal"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("유저가 없습니다."));
    }

    // ✅ Validation 실패 → 400
    @Test
    void validationException_테스트() throws Exception {
        String json = """
            {
              "email": ""
            }
        """;

        mockMvc.perform(post("/test/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("이메일은 필수입니다."));
    }

    @Test
    void missingServletRequestParameterException_테스트() throws Exception {
        mockMvc.perform(get("/test/missing")) // email 쿼리 파라미터 누락
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Required request parameter 'email' for method parameter type String is not present"));
    }

    @Test
    void constraintViolationException_테스트() throws Exception {
        mockMvc.perform(get("/test/constraint?age=-1")) // age가 음수 → 제약 위반
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("0 이상이어야 합니다")));
    }

    @Test
    void globalException_테스트() throws Exception {
        mockMvc.perform(get("/test/global"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

}
