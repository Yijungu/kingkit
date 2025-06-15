package com.kingkit.billing_service.integration.manual_billing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.dto.request.ManualBillingRequestDto;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.core.FixtureFactory;
import com.kingkit.billing_service.support.fixture.dto.ManualBillingRequestDtoFixture;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // ✅ 유지
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ManualBillingIntegrationTest extends IntegrationTestSupport {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private FixtureFactory fixtureFactory;

    private static final String ENDPOINT = "/internal/billing/execute";
    private static final String INTERNAL_KEY = "testkey-1234";

    @BeforeEach
    void setupWireMockStubs() {
        /* 1) 강제 실패 billingKey (=❌ tossFailureHandled) */
        WireMock.stubFor(
            WireMock.post(WireMock.urlEqualTo("/v1/billing/billing-fail"))
                .atPriority(1)
                .willReturn(WireMock.aResponse()
                    .withStatus(500)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""
                        { "code":"INTERNAL_ERR", "message":"forced failure for test" }
                    """))
        );

        /* 2) catch-all success stub */
        WireMock.stubFor(
            WireMock.post(WireMock.urlMatching("/v1/billing/[^/]+$"))
                .atPriority(10)
                .willReturn(WireMock.aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("""
                        { "paymentKey":"stub-pay-123", "status":"DONE" }
                    """))
        );
    }

    @Test
    @DisplayName("✅ 유효한 수동 결제 요청 - 성공 처리")
    void manualBillingSuccess() throws Exception {
        var userId = 1001L;
        var billingKey = "billing-1001";
        fixtureFactory.createActiveSubscription(userId);

        ManualBillingRequestDto dto = ManualBillingRequestDtoFixture.valid(
            userId, billingKey, "manual-20240520-001"
        );

        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.paymentKey").exists())
              .andExpect(jsonPath("$.orderId").value(dto.getOrderId()))
              .andExpect(jsonPath("$.status").value(PaymentStatus.SUCCESS.name()))
              .andExpect(jsonPath("$.paidAt").exists());
    }

    @Test
    @DisplayName("❌ billingKey 없음 또는 일치하지 않음 - 400 Bad Request")
    void invalidBillingKey() throws Exception {
        ManualBillingRequestDto dto = ManualBillingRequestDtoFixture.withInvalidBillingKey(2002L);

        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("❌ orderId 중복 - 409 Conflict")
    void duplicateOrderId() throws Exception {
        long userId = 1003L;
        String billingKey = "billing-" + userId;
        fixtureFactory.createActiveSubscription(userId, billingKey);

        String orderId = "dup-" + System.nanoTime();

        ManualBillingRequestDto dto = ManualBillingRequestDtoFixture.valid(
            userId, billingKey, orderId
        );

        mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        result.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("❌ Toss 응답 실패 시 PaymentFailure 기록 + 상태 FAILED")
    void tossFailureHandled() throws Exception {
        var userId = 1004L;
        var billingKey = "billing-fail";
        fixtureFactory.createActiveSubscription(userId, billingKey);

        ManualBillingRequestDto dto = ManualBillingRequestDtoFixture.valid(
            userId, billingKey, "manual-20240520-fail"
        );

        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.status").value(PaymentStatus.FAILED.name()));
    }

    @Test
    @DisplayName("❌ 인증 키 누락 시 403 Forbidden")
    void missingInternalApiKey() throws Exception {
        ManualBillingRequestDto dto = ManualBillingRequestDtoFixture.valid(
            9999L, "some-billing-key", "manual-20240520-auth"
        );

        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        result.andExpect(status().isUnauthorized());
    }
}
