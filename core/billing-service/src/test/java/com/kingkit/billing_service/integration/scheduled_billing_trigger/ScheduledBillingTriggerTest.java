package com.kingkit.billing_service.integration.scheduled_billing_trigger;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.dto.request.AdminBillingTriggerRequest;
import com.kingkit.billing_service.dto.request.BillingTriggerRequest;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.scheduler.ScheduledBillingTriggerJob;
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

@DisplayName("✅ Scheduled Billing Trigger 통합 테스트")
class ScheduledBillingTriggerTest extends IntegrationTestSupport {

    private static final String ENDPOINT = "/internal/billing/trigger";
    private static final String INTERNAL_KEY = "testkey-1234";

    @Autowired ObjectMapper objectMapper;
    @Autowired FixtureFactory fixtureFactory;

    @Autowired ScheduledBillingTriggerJob billingTriggerJob;  // 스케줄러 job 주입

    @BeforeEach
    void setUpStubs() {
        TossMockStub.stub200ResponseFor("billing-2001");
        TossMockStub.stub200ResponseFor("billing-3001");
        TossMockStub.stubFailResponseFor("billing-2002", 401, "Unauthorized");
    }

    /* ----------------------------------------------------------------------
       ✅ 성공 케이스 - 정상 결제
    ----------------------------------------------------------------------- */
    @Test
    @DisplayName("✅ 정기 결제 트리거 성공 - AdminBillingTriggerRequest 기반")
    void scheduledBillingTriggerSuccess() throws Exception {
        Long userId = 2001L;
        fixtureFactory.createActiveSubscriptionWithNextBilling(userId, LocalDate.now().atStartOfDay());

        AdminBillingTriggerRequest request = AdminBillingTriggerRequest.builder()
            .userIds(List.of(userId))
            .targetDate(LocalDate.now())
            .build();

        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.successCount").value(1))
            .andExpect(jsonPath("$.failureCount").value(0))
            .andExpect(jsonPath("$.failures").isArray());
    }

    @Test
    @DisplayName("❌ Toss 응답 실패 시 FAILED 처리 + PaymentFailure 저장")
    void scheduledBillingTriggerTossFail() throws Exception {
        Long userId = 2002L;
        fixtureFactory.createActiveSubscriptionWithNextBilling(userId, LocalDate.now().atStartOfDay());

        AdminBillingTriggerRequest request = AdminBillingTriggerRequest.builder()
            .userIds(List.of(userId))
            .targetDate(LocalDate.now())
            .build();

        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.successCount").value(0))
            .andExpect(jsonPath("$.failureCount").value(1))
            .andExpect(jsonPath("$.failures[0].userId").value(userId))
            .andExpect(jsonPath("$.failures[0].status").value("FAILED"))
            .andExpect(jsonPath("$.failures[0].reason").exists());
    }

    /* ----------------------------------------------------------------------
       ❌ 실패 케이스 - billingKey 없음
    ----------------------------------------------------------------------- */
    @Test
    @DisplayName("❌ billingKey 없음 - 실패 처리")
    void scheduledBillingTriggerNoBillingKey() throws Exception {
        String uniquePlanCode = "no-billing-plan-" + System.currentTimeMillis(); // 중복 방지
        fixtureFactory.createPlan(uniquePlanCode, 10000, 30);
        fixtureFactory.createSubscriptionWithoutBillingKey(2002L, uniquePlanCode, LocalDateTime.now());


        var request = new AdminBillingTriggerRequest(LocalDate.now(), List.of(2002L));
        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.successCount").value(0))
            .andExpect(jsonPath("$.failureCount").value(1));
    }

    /* ----------------------------------------------------------------------
       ✅ 스케줄러 Job 직접 실행 테스트
    ----------------------------------------------------------------------- */
    @Test
    @DisplayName("✅ 스케줄러 Job 실행 - 오늘 결제 대상 구독들에 대해 트리거 수행")
    void runScheduledBillingTriggerJob() {
        // given: 당일 결제 대상 구독 2개 생성
        fixtureFactory.createActiveSubscriptionWithNextBilling(3001L, LocalDate.now().atStartOfDay());
        fixtureFactory.createActiveSubscriptionWithNextBilling(3002L, LocalDate.now().atTime(10, 0));

        // when: 실제 Job 실행
        billingTriggerJob.run(LocalDate.now());

        // then: 예외 발생 없으면 성공 (로깅/추적은 log 또는 DB 기반 확인)
    }
}
