package com.kingkit.billing_service.integration.billing_prepare;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.dto.request.PrepareBillingRequest;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.integration.common.RedisTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BillingPrepareIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTestSupport redisTestSupport;

    @Test
    @DisplayName("✅ 유효한 요청으로 Checkout URL을 응답받고, Redis에 planId가 저장된다")
    void prepareBilling_success() throws Exception {
        // given
        String planCode = "basic-monthly";
        Long userId = 1001L;

        PrepareBillingRequest request = new PrepareBillingRequest(
                userId,
                planCode,
                "https://success.com",
                "https://fail.com"
        );

        // when
        ResultActions result = mockMvc.perform(post("/billing/prepare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.checkoutUrl").exists())
              .andExpect(jsonPath("$.orderId").exists())
              .andExpect(jsonPath("$.customerKey").value("user-" + userId));

        // Redis 저장 확인
        String responseContent = result.andReturn().getResponse().getContentAsString();
        String orderId = objectMapper.readTree(responseContent).get("orderId").asText();

        String savedPlanCode = redisTestSupport.get(orderId);
        assertThat(savedPlanCode).isEqualTo(planCode);
    }

    @Test
    @DisplayName("❌ 존재하지 않는 planId를 전달하면 404를 반환한다")
    void prepareBilling_invalidPlanId() throws Exception {
        // given
        PrepareBillingRequest request = new PrepareBillingRequest(
                1001L,
                "non-existent-plan",
                "https://success.com",
                "https://fail.com"
        );

        // when
        ResultActions result = mockMvc.perform(post("/billing/prepare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isNotFound());
    }
}
