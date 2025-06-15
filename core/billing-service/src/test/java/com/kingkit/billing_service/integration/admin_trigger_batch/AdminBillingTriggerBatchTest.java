package com.kingkit.billing_service.integration.admin_trigger_batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.dto.request.AdminBillingTriggerRequest;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.core.FixtureFactory;
import com.kingkit.billing_service.support.stub.TossMockStub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class AdminBillingTriggerBatchTest extends IntegrationTestSupport {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private FixtureFactory fixtureFactory;

    private static final String ENDPOINT = "/internal/billing/trigger";
    private static final String INTERNAL_KEY = "testkey-1234";

    @BeforeEach
    void setup() {
        // 테스트 대상 유저 ID 등록
        List<Long> allUserIds = List.of(1001L, 1002L, 1003L, 2001L, 2002L, 3001L, 3002L, 4001L);

        TossMockStub.setupDefaultSuccessBillingStubs(allUserIds);
    }


    @Test
    @DisplayName("✅ 날짜와 userIds를 함께 보낼 경우 교집합 유저만 결제 시도됨")
    void triggerWithTargetDateAndUserIds() throws Exception {
        // given
        var userIds = List.of(1001L, 1002L, 1003L);
        for (Long userId : userIds) {
            fixtureFactory.createActiveSubscriptionWithNextBilling(userId, LocalDateTime.now());
        }

        var request = new AdminBillingTriggerRequest(LocalDate.now(), userIds);

        // when & then
        performTrigger(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.successCount").value(3))
            .andExpect(jsonPath("$.failureCount").value(0));
    }

    @Test
    @DisplayName("✅ targetDate만 보낸 경우 전체 유저 대상으로 결제 시도됨")
    void triggerWithTargetDateOnly() throws Exception {
        fixtureFactory.createActiveSubscriptionWithNextBilling(2001L, LocalDateTime.now());
        fixtureFactory.createActiveSubscriptionWithNextBilling(2002L, LocalDateTime.now());

        var request = new AdminBillingTriggerRequest(LocalDate.now(), null);

        performTrigger(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.successCount").value(2));
    }

    @Test
    @DisplayName("✅ userIds만 보낸 경우 해당 유저들만 결제 시도됨")
    void triggerWithUserIdsOnly() throws Exception {
        fixtureFactory.createActiveSubscriptionWithNextBilling(3001L, LocalDateTime.now());
        fixtureFactory.createActiveSubscriptionWithNextBilling(3002L, LocalDateTime.now());

        var request = new AdminBillingTriggerRequest(null, List.of(3001L, 3002L));

        performTrigger(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.successCount").value(2));
    }

    @Test
    @DisplayName("❌ billingKey 없는 유저 포함된 경우 → 실패 응답 포함")
    void triggerWithMissingBillingKey() throws Exception {
        fixtureFactory.createActiveSubscriptionWithNextBilling(4001L, LocalDateTime.now());
        String uniquePlanCode = "no-billing-plan-" + System.currentTimeMillis(); // 중복 방지
        fixtureFactory.createPlan(uniquePlanCode, 10000, 30);
        fixtureFactory.createSubscriptionWithoutBillingKey(4002L, uniquePlanCode, LocalDateTime.now());


        var request = new AdminBillingTriggerRequest(LocalDate.now(), List.of(4001L, 4002L));

        performTrigger(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.successCount").value(1))
            .andExpect(jsonPath("$.failureCount").value(1));
    }

    @Test
    @DisplayName("❌ 인증 키 누락 시 401 Unauthorized")
    void triggerWithoutApiKey() throws Exception {
        var request = new AdminBillingTriggerRequest(LocalDate.now(), List.of(9999L));

        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isUnauthorized());   
    }

    // ✅ 공통 로직 추출
    private ResultActions performTrigger(AdminBillingTriggerRequest request) throws Exception {
        return mockMvc.perform(post(ENDPOINT)
                .header("X-Internal-API-Key", INTERNAL_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }
}
