package com.kingkit.billing_service.integration.billing_key_delete;

import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BillingKeyDeleteTest extends IntegrationTestSupport {

    private static final String ENDPOINT = "/internal/billing-key/{billingKey}";
    private static final String INTERNAL_KEY = "valid-internal-key";

    @Autowired
    private FixtureFactory fixtureFactory;

    @Test
    @DisplayName("✅ billingKey 삭제 성공 → PaymentMethod isActive = false")
    void deleteBillingKeySuccess() throws Exception {
        // given
        Long userId = 1001L;
        String billingKey = "billing-success";
        fixtureFactory.createPaymentMethod(userId, billingKey);

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT, billingKey)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.billingKey").value(billingKey))
              .andExpect(jsonPath("$.deleted").value(true))
              .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("✅ Toss 404 응답 (이미 삭제된 billingKey) → 무시")
    void deleteBillingKeyNotFoundInToss() throws Exception {
        // given
        Long userId = 1002L;
        String billingKey = "billing-not-found"; // TossClientMock이 404 반환하게 구성
        fixtureFactory.createPaymentMethod(userId, billingKey);

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT, billingKey)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.deleted").value(true));
    }

    @Test
    @DisplayName("❌ Toss 서버 오류 발생 → 실패 처리")
    void deleteBillingKeyTossError() throws Exception {
        // given
        Long userId = 1003L;
        String billingKey = "billing-error"; // TossClientMock이 500 반환하게 구성
        fixtureFactory.createPaymentMethod(userId, billingKey);

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT, billingKey)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("❌ 존재하지 않는 billingKey → 400 반환")
    void deleteBillingKeyNotExist() throws Exception {
        // given
        String billingKey = "not-exist-key";

        // when
        ResultActions result = mockMvc.perform(delete(ENDPOINT, billingKey)
            .header("X-Internal-API-Key", INTERNAL_KEY)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }
}
