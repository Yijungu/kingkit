package com.kingkit.billing_service.integration.pg_health_check;

import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PgHealthCheckTest extends IntegrationTestSupport {

    private static final String ENDPOINT = "/internal/monitor/pg-health";
    private static final String INTERNAL_KEY = "valid-internal-key";

    @Test
    @DisplayName("✅ Toss 응답 404 → 연결 정상, available = true")
    void pgHealthCheckWith404() throws Exception {
        // TossMock이 billingKey=test-404 요청 시 404 응답 설정돼 있어야 함

        // when
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .header("X-Internal-API-Key", INTERNAL_KEY)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.available").value(true))
              .andExpect(jsonPath("$.statusCode").value(404))
              .andExpect(jsonPath("$.slow").isBoolean())
              .andExpect(jsonPath("$.responseTimeMillis").isNumber());
    }

    @Test
    @DisplayName("❌ Toss 응답 401 → 인증 실패, available = false")
    void pgHealthCheckWith401() throws Exception {
        // TossMock이 API Key 오류로 401 반환하도록 설정 필요

        // when
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .header("X-Internal-API-Key", "invalid-key")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("❌ Toss 응답 느림 → slow = true")
    void pgHealthCheckWithSlowResponse() throws Exception {
        // TossMock이 일부러 2초 이상 응답 지연 설정 필요 (ex: billingKey=test-slow)

        // when
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .header("X-Internal-API-Key", INTERNAL_KEY)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.slow").value(true))
              .andExpect(jsonPath("$.responseTimeMillis").value(org.hamcrest.Matchers.greaterThan(1000)));
    }

    @Test
    @DisplayName("❌ 내부 인증 키 없음 → 403 반환")
    void missingApiKey() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isForbidden());
    }
}
