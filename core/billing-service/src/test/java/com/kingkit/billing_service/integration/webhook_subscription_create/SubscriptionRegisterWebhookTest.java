package com.kingkit.billing_service.integration.webhook_subscription_create;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.client.PaymentClient;
import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.fixture.composite.WebhookTestFixture;
import com.kingkit.billing_service.support.fixture.dto.TossWebhookRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SubscriptionRegisterWebhookTest extends IntegrationTestSupport {

    private static final String ENDPOINT = "/webhook/toss";

    @Autowired
    private ObjectMapper objectMapper;
    

    @Test
    @DisplayName("✅ 카드 등록 성공 Webhook 처리 - Subscription 및 PaymentMethod 생성")
    void handleSubscriptionRegisteredWebhook() throws Exception {
        // given
        TossWebhookRequest request = TossWebhookRequestFixture.registeredEvent(1001L, "order-1001", "billing-1001");
        String body = objectMapper.writeValueAsString(request);
        String signature = WebhookTestFixture.validSignature(body);

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .header("Toss-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("❌ 잘못된 customerKey 또는 Redis plan 없음 → 무시하고 200 OK")
    void handleInvalidCustomerKeyOrMissingPlan() throws Exception {
        // given
        TossWebhookRequest request = TossWebhookRequestFixture.registeredEvent(9999L, "order-missing", "billing-9999");
        String body = objectMapper.writeValueAsString(request);
        String signature = WebhookTestFixture.validSignature(body);

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
                .header("Toss-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        result.andExpect(status().isOk());
    }

    
}
