package com.kingkit.billing_service.integration.manual_billing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.dto.request.ManualBillingRequestDto;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.FixtureFactory;
import com.kingkit.billing_service.support.fixture.ManualBillingRequestDtoFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ManualBillingIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FixtureFactory fixtureFactory;

    private static final String ENDPOINT = "/internal/billing/execute";
    private static final String INTERNAL_KEY = "valid-internal-key";

    @Test
    @DisplayName("✅ 유효한 수동 결제 요청 - 성공 처리")
    void manualBillingSuccess() throws Exception {
        // given
        var userId = 1001L;
        var billingKey = "billing-1001";
        fixtureFactory.createActiveSubscription(userId);

        ManualBillingRequestDto dto = ManualBillingRequestDtoFixtures.valid(
            userId, billingKey, "manual-20240520-001"
        );

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.paymentKey").exists())
              .andExpect(jsonPath("$.orderId").value(dto.orderId()))
              .andExpect(jsonPath("$.status").value(PaymentStatus.SUCCESS.name()))
              .andExpect(jsonPath("$.paidAt").exists());
    }

    @Test
    @DisplayName("❌ billingKey 없음 또는 일치하지 않음 - 400 Bad Request")
    void invalidBillingKey() throws Exception {
        // given
        ManualBillingRequestDto dto = ManualBillingRequestDtoFixtures.withInvalidBillingKey(2002L);

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("❌ orderId 중복 - 409 Conflict")
    void duplicateOrderId() throws Exception {
        // given
        var userId = 1003L;
        var billingKey = "billing-1003";
        fixtureFactory.createActiveSubscription(userId);

        ManualBillingRequestDto dto = ManualBillingRequestDtoFixtures.withDuplicateOrderId(
            userId, billingKey
        );

        // first success
        mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk());

        // when - second call with same orderId
        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isConflict());
    }

    @Test
    @DisplayName("❌ Toss 응답 실패 시 PaymentFailure 기록 + 상태 FAILED")
    void tossFailureHandled() throws Exception {
        // given
        var userId = 1004L;
        var billingKey = "billing-fail"; // TossClient mock이 이 키에 대해 실패 응답하도록 구성 필요
        fixtureFactory.createPaymentMethod(userId, billingKey);

        ManualBillingRequestDto dto = ManualBillingRequestDtoFixtures.valid(
            userId, billingKey, "manual-20240520-fail"
        );

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.status").value(PaymentStatus.FAILED.name()));
    }

    @Test
    @DisplayName("❌ 인증 키 누락 시 403 Forbidden")
    void missingInternalApiKey() throws Exception {
        // given
        ManualBillingRequestDto dto = ManualBillingRequestDtoFixtures.valid(
            9999L, "some-billing-key", "manual-20240520-auth"
        );

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isForbidden());
    }
}
