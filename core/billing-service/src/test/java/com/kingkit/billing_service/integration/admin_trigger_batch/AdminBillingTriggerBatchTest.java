package com.kingkit.billing_service.integration.admin_trigger_batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.dto.request.AdminBillingTriggerRequest;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminBillingTriggerBatchTest extends IntegrationTestSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FixtureFactory fixtureFactory;

    private static final String ENDPOINT = "/internal/billing/trigger";
    private static final String INTERNAL_KEY = "valid-internal-key";

    @Test
    @DisplayName("✅ 날짜와 userIds를 함께 보낼 경우 교집합 유저만 결제 시도됨")
    void triggerWithTargetDateAndUserIds() throws Exception {
        // given
        var userIds = List.of(1001L, 1002L, 1003L);
        for (Long userId : userIds) {
            fixtureFactory.createActiveSubscriptionWithNextBilling(userId, LocalDate.now());
        }

        var request = new AdminBillingTriggerRequest(LocalDate.now(), userIds);

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .header("X-Internal-API-Key", INTERNAL_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.successCount").value(3))
              .andExpect(jsonPath("$.failureCount").value(0))
              .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("✅ targetDate만 보낸 경우 전체 유저 대상으로 결제 시도됨")
    void triggerWithTargetDateOnly() throws Exception {
        fixtureFactory.createActiveSubscriptionWithNextBilling(2001L, LocalDate.now());
        fixtureFactory.createActiveSubscriptionWithNextBilling(2002L, LocalDate.now());

        var request = new AdminBillingTriggerRequest(LocalDate.now(), null);

        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .header("X-Internal-API-Key", INTERNAL_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.successCount").value(2))
              .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("✅ userIds만 보낸 경우 해당 유저들만 결제 시도됨")
    void triggerWithUserIdsOnly() throws Exception {
        fixtureFactory.createActiveSubscriptionWithNextBilling(3001L, LocalDate.now().plusDays(1)); // tomorrow
        fixtureFactory.createActiveSubscriptionWithNextBilling(3002L, LocalDate.now().plusDays(2)); // the day after

        var request = new AdminBillingTriggerRequest(null, List.of(3001L, 3002L));

        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .header("X-Internal-API-Key", INTERNAL_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.successCount").value(2));
    }

    @Test
    @DisplayName("❌ billingKey 없는 유저 포함된 경우 → 실패 응답 포함")
    void triggerWithMissingBillingKey() throws Exception {
        fixtureFactory.createActiveSubscriptionWithNextBilling(4001L, LocalDate.now()); // 정상
        fixtureFactory.createPlan("no-billing-plan", 10000, 30);
        fixtureFactory.createSubscriptionWithoutBillingKey(4002L, "no-billing-plan", LocalDate.now());

        var request = new AdminBillingTriggerRequest(LocalDate.now(), List.of(4001L, 4002L));

        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .header("X-Internal-API-Key", INTERNAL_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.successCount").value(1))
              .andExpect(jsonPath("$.failureCount").value(1))
              .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    @DisplayName("❌ 인증 키 누락 시 403 Forbidden")
    void triggerWithoutApiKey() throws Exception {
        var request = new AdminBillingTriggerRequest(LocalDate.now(), List.of(9999L));

        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isForbidden());
    }
}
