package com.kingkit.billing_service.integration.scheduled_billing_trigger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.dto.request.BillingTriggerRequest;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ScheduledBillingTriggerTest extends IntegrationTestSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FixtureFactory fixtureFactory;

    private static final String ENDPOINT = "/internal/billing/trigger";
    private static final String INTERNAL_KEY = "valid-internal-key";

    @Test
    @DisplayName("✅ 정기 결제 트리거 성공")
    void scheduledBillingTriggerSuccess() throws Exception {
        Long userId = 2001L;
        Subscription subscription = fixtureFactory.createActiveSubscription(userId);

        BillingTriggerRequest request = BillingTriggerRequest.builder()
            .subscriptionId(subscription.getId())
            .build();

        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.subscriptionId").value(subscription.getId()))
              .andExpect(jsonPath("$.status").value("SUCCESS"))
              .andExpect(jsonPath("$.paymentKey").exists())
              .andExpect(jsonPath("$.orderId").exists())
              .andExpect(jsonPath("$.paidAt").exists());
    }

    @Test
    @DisplayName("❌ Toss 응답 실패 시 FAILED 처리 + PaymentFailure 저장")
    void scheduledBillingTriggerTossFail() throws Exception {
        Long userId = 2002L;
        fixtureFactory.createPaymentMethod(userId, "billing-fail");
        Subscription subscription = fixtureFactory.createSubscriptionWithoutPlan(userId);

        BillingTriggerRequest request = BillingTriggerRequest.builder()
            .subscriptionId(subscription.getId())
            .build();

        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.status").value("FAILED"))
              .andExpect(jsonPath("$.reason").exists());
    }

    @Test
    @DisplayName("❌ billingKey 없음 - 실패 처리")
    void scheduledBillingTriggerNoBillingKey() throws Exception {
        Long userId = 2003L;
        Subscription subscription = fixtureFactory.createSubscriptionWithoutPayment(userId);

        BillingTriggerRequest request = BillingTriggerRequest.builder()
            .subscriptionId(subscription.getId())
            .build();

        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.status").value("FAILED"))
              .andExpect(jsonPath("$.reason").value("billingKey 없음"));
    }
}
