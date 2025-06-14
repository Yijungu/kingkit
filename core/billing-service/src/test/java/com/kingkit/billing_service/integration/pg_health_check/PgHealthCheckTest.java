package com.kingkit.billing_service.integration.pg_health_check;

import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.stub.TossMockStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PgHealthCheckTest extends IntegrationTestSupport {

    private static final String ENDPOINT = "/internal/pg/health";
    private static final String INTERNAL_KEY = "testkey-1234";

    @BeforeEach
    void setup() {
        TossMockStub.stub404Response();     // billingKey=test-404 → 404
        TossMockStub.stub401Response();     // billingKey=test-401 → 401
        TossMockStub.stubSlowResponse();    // billingKey=test-slow → 지연 + 200
    }

    @Test
    @DisplayName("✅ Toss 응답 404 → 연결 정상, available = true")
    void pgHealthCheckWith404() throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("billingKey", "test-404")
                .header("X-Internal-API-Key", INTERNAL_KEY)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(jsonPath("$.available").value(true))
              .andExpect(jsonPath("$.statusCode").value(404))
              .andExpect(jsonPath("$.slow").isBoolean())
              .andExpect(jsonPath("$.responseTimeMillis").isNumber());
    }

    @Test
    @DisplayName("❌ Toss 응답 401 → 인증 실패, available = false")
    void pgHealthCheckWith401() throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("billingKey", "test-401")
                .header("X-Internal-API-Key", INTERNAL_KEY)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(jsonPath("$.statusCode").value(401))
              .andExpect(jsonPath("$.slow").isBoolean())
              .andExpect(jsonPath("$.responseTimeMillis").isNumber());
    }

    @Test
    @DisplayName("❌ Toss 응답 느림 → slow = true")
    void pgHealthCheckWithSlowResponse() throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("billingKey", "test-slow")
                .header("X-Internal-API-Key", INTERNAL_KEY)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.slow").value(true))
              .andExpect(jsonPath("$.responseTimeMillis").value(greaterThan(2000)));
    }

    @Test
    @DisplayName("❌ 내부 인증 키 없음 → 401 반환")
    void missingApiKey() throws Exception {
        ResultActions result = mockMvc.perform(get(ENDPOINT)
                .param("billingKey", "test-404")  // 있어도 인증 실패니까 무시됨
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }
}
