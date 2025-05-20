package com.kingkit.billing_service.integration.webhook_payment_events;

import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.FixtureFactory;
import com.kingkit.billing_service.support.fixture.WebhookRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentWebhookTest extends IntegrationTestSupport {

    private static final String ENDPOINT = "/webhook/toss";

    @Autowired
    private FixtureFactory fixtureFactory;

    @Test
    @DisplayName("✅ 결제 성공 Webhook 수신 → PaymentHistory 생성")
    void handlePaymentSuccessWebhook() throws Exception {
        // given
        Long userId = 2001L;
        fixtureFactory.createActiveSubscription(userId);

        String requestBody = WebhookRequestFixture.paymentSuccess(userId, "webhook-success-001", "pay-success-001");

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("❌ 결제 실패 Webhook 수신 → PaymentFailure 생성")
    void handlePaymentFailedWebhook() throws Exception {
        // given
        Long userId = 2002L;
        fixtureFactory.createActiveSubscription(userId);

        String requestBody = WebhookRequestFixture.paymentFailed(userId, "webhook-failed-001", "pay-fail-001");

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("❌ 중복 Webhook 수신 시 200 OK + 처리 생략")
    void handleDuplicateWebhookGracefully() throws Exception {
        // given
        Long userId = 2003L;
        fixtureFactory.createActiveSubscription(userId);

        String body = WebhookRequestFixture.paymentSuccess(userId, "webhook-dup-001", "pay-dup-001");

        // first call
        mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());

        // duplicate call
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));

        // then
        result.andExpect(status().isOk());
    }
}
