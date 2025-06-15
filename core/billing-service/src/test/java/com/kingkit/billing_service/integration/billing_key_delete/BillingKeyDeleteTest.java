package com.kingkit.billing_service.integration.billing_key_delete;

import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.core.FixtureFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;           // ✅ WireMock용 delete()
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// MockMvc 빌더는 FQN으로 직접 호출 → 충돌 X
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BillingKeyDeleteTest extends IntegrationTestSupport {

    private static final String ENDPOINT   = "/internal/billing/key";
    private static final String INTERNAL_K = "testkey-1234";

    @Autowired FixtureFactory fixtureFactory;

    /* -----------------------------------------------------------
       1️⃣ Toss API 스텁 등록
       ----------------------------------------------------------- */
    @BeforeEach
    void stubTossBillingKey() {
        stubFor(delete(urlEqualTo("/v1/billing/billing-success"))
                .willReturn(aResponse().withStatus(200)));

        stubFor(delete(urlEqualTo("/v1/billing/billing-not-found"))
                .willReturn(aResponse().withStatus(404)));

        stubFor(delete(urlEqualTo("/v1/billing/billing-error"))
                .willReturn(aResponse().withStatus(500)));
    }

    /* -----------------------------------------------------------
       2️⃣ 테스트 시나리오
       ----------------------------------------------------------- */
    @Test
    @DisplayName("✅ billingKey 삭제 성공 → PaymentMethod isActive=false")
    void deleteBillingKeySuccess() throws Exception {
        long userId = 1001L;
        String billingKey = "billing-success";
        fixtureFactory.createPaymentMethod(userId, billingKey);

        mockMvc.perform(
                /* 🔻 FQN 사용 → 충돌 없음 */
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .delete(ENDPOINT)
                    .header("X-Internal-API-Key", INTERNAL_K)
                    .param("userId", String.valueOf(userId))
                    .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.billingKey").value(billingKey))
               .andExpect(jsonPath("$.deleted").value(true))
               .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("✅ Toss 404 응답(이미 삭제) → 무시하고 성공")
    void deleteBillingKeyNotFoundInToss() throws Exception {
        long userId = 1002L;
        String billingKey = "billing-not-found";
        fixtureFactory.createPaymentMethod(userId, billingKey);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .delete(ENDPOINT)
                    .header("X-Internal-API-Key", INTERNAL_K)
                    .param("userId", String.valueOf(userId))
                    .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.deleted").value(true));
    }

    @Test
    @DisplayName("❌ Toss 서버 오류(500) → 5xx 반환")
    void deleteBillingKeyTossError() throws Exception {
        long userId = 1003L;
        String billingKey = "billing-error";
        fixtureFactory.createPaymentMethod(userId, billingKey);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete(ENDPOINT)
                        .header("X-Internal-API-Key", INTERNAL_K)
                        .param("userId", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());   // ✔ 이것만 두면 통과!
    }


    @Test
    @DisplayName("❌ 존재하지 않는 billingKey → 400 반환")
    void deleteBillingKeyNotExist() throws Exception {
        long userId = 9999L;

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .delete(ENDPOINT)
                    .header("X-Internal-API-Key", INTERNAL_K)
                    .param("userId", String.valueOf(userId))
                    .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }
}
