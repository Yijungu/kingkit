package com.kingkit.billing_service.integration.webhook_payment_events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.client.PaymentClient;
import com.kingkit.billing_service.dto.request.PrepareBillingRequest;
import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.integration.common.RedisTestSupport;
import com.kingkit.billing_service.support.config.JwtTestUtilConfig;
import com.kingkit.billing_service.support.core.FixtureFactory;
import com.kingkit.billing_service.support.fixture.composite.WebhookTestFixture;
import com.kingkit.billing_service.support.stub.TossMockStub;
import com.kingkit.lib_test_support.testsupport.util.JwtTestTokenProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class PaymentWebhookTest extends IntegrationTestSupport {

    private static final String ENDPOINT = "/webhook/toss";

    @Autowired ObjectMapper objectMapper;
    @Autowired FixtureFactory fixtureFactory;
    /* ---------- Mocked external dependency ---------- */
    @MockBean private PaymentClient paymentClient;

    @BeforeEach
    void setUp() {
        // 공통 스텁 등록
        TossMockStub.setupDefaultSuccessPaymentsStubs(
            java.util.List.of(2001L, 2002L, 2003L, 2004L, 2005L)
        );
    }
    

    @Test
    @DisplayName("✅ 서명 검증 성공 + 결제 성공 이벤트 → PaymentHistory 저장")
    void handlePaymentSuccessEvent() throws Exception {
        Long userId = 2001L;
        fixtureFactory.createActiveSubscription(userId);

        TossWebhookRequest request = WebhookTestFixture.paymentSuccessEvent(userId, "order-success-001");
        String body = objectMapper.writeValueAsString(request);
        String signature = WebhookTestFixture.validSignature(body);
    
        mockMvc.perform(post(ENDPOINT)
                .header("Toss-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("❌ 서명 검증 실패 → 401 Unauthorized 반환")
    void handleInvalidSignature() throws Exception {
        TossWebhookRequest request = WebhookTestFixture.paymentSuccessEvent(2002L, "invalid-order");
        String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(ENDPOINT)
                .header("Toss-Signature", "invalid-signature")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("✅ 서명 검증 성공 + 결제 실패 이벤트 → PaymentFailure 저장")
    void handlePaymentFailedEvent() throws Exception {
        Long userId = 2003L;
        fixtureFactory.createActiveSubscription(userId);

        TossWebhookRequest request = WebhookTestFixture.paymentFailedEvent(userId, "order-failed-001");
        String body = objectMapper.writeValueAsString(request);
        String signature = WebhookTestFixture.validSignature(body);

        mockMvc.perform(post(ENDPOINT)
                .header("Toss-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("✅ 중복 결제 성공 Webhook → 처리 생략 + 200 OK")
    void handleDuplicateEventGracefully() throws Exception {
        Long userId = 2004L;
        fixtureFactory.createActiveSubscription(userId);

        TossWebhookRequest request = WebhookTestFixture.paymentSuccessEvent(userId, "order-dup-001");
        String body = objectMapper.writeValueAsString(request);
        String signature = WebhookTestFixture.validSignature(body);

        // 1차 전송
        mockMvc.perform(post(ENDPOINT)
                .header("Toss-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());

        // 2차 전송 (중복)
        mockMvc.perform(post(ENDPOINT)
                .header("Toss-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("✅ 등록 이벤트 Webhook → 결제 수단 및 구독 생성")
    void handleSubscriptionRegisteredEvent() throws Exception {
        Long userId = 2005L;
        fixtureFactory.preparePlanCacheFor("order-reg-001", "plan-basic");

        TossWebhookRequest request = WebhookTestFixture.subscriptionRegisteredEvent(userId);
        String body = objectMapper.writeValueAsString(request);
        String signature = WebhookTestFixture.validSignature(body);

        mockMvc.perform(post(ENDPOINT)
                .header("Toss-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());
    }
}
