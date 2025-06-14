package com.kingkit.billing_service.integration.billing_prepare;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.client.PaymentClient;
import com.kingkit.billing_service.dto.request.PrepareBillingRequest;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.integration.common.RedisTestSupport;
import com.kingkit.billing_service.support.config.JwtTestUtilConfig;
import com.kingkit.billing_service.support.core.FixtureFactory;
import com.kingkit.lib_test_support.testsupport.util.JwtTestTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Billing `/billing/prepare` 엔드포인트 통합 테스트
 *
 * ▫️ 외부 결제사(Toss) 호출은 `@MockBean PaymentClient`로 차단  
 * ▫️ Redis · DB 등 나머지는 실제(in-memory/H2)로 동작
 */
@Import(JwtTestUtilConfig.class)
class BillingPrepareIntegrationTest extends IntegrationTestSupport {

    private static final String ENDPOINT = "/billing/prepare";

    /* ---------- Autowired beans ---------- */
    @Autowired private ObjectMapper       objectMapper;
    @Autowired private RedisTestSupport   redis;
    @Autowired private JwtTestTokenProvider jwtTokenProvider;
    @Autowired private FixtureFactory     fixture;
    
    /* ---------- Mocked external dependency ---------- */
    @MockBean private PaymentClient paymentClient;

    /* ====================================================================== */
    /* ========================== Happy-path ================================ */
    /* ====================================================================== */
    

        @Test
        @DisplayName("Checkout URL 반환 & Redis 저장 성공")
        void prepareBilling_success() throws Exception {
            // ── given ───────────────────────────────────────────────────────────
            String planCode = "basic-monthly";
            long   userId   = 1001L;

            // DB – 요금제 사전 세팅
            fixture.createSubscriptionPlan(planCode, 10_900L);

            // JWT – 인증 토큰
            String token = jwtTokenProvider.generateUserToken(userId);

            // API 요청 DTO
            PrepareBillingRequest req = fixture.buildRequest(planCode);

            /* 외부 결제 Stub
               orderId는 서비스 내부에서 난수로 생성되므로 anyString() 사용 */
            given(paymentClient.requestCheckoutUrl(
                    eq(req.successUrl()),
                    eq(req.failUrl()),
                    eq("user-" + userId),
                    anyString(),
                    eq(10_900L)
            )).willReturn("https://mock-checkout.toss.com/checkout");

            // ── when ────────────────────────────────────────────────────────────
            ResultActions result = invoke(token, req);

            // ── then ────────────────────────────────────────────────────────────
            result.andExpect(status().isOk())
                  .andExpect(jsonPath("$.checkoutUrl")
                            .value("https://mock-checkout.toss.com/checkout"))
                  .andExpect(jsonPath("$.orderId").exists())
                  .andExpect(jsonPath("$.customerKey").value("user-" + userId));

            // Redis - planCode 저장 확인
            String orderId = body(result, "orderId");
            String redisKey = "billing:order:" + orderId;   // ← 서비스 코드에 맞춰 prefix
            String cached = redis.get(redisKey);
            assertThat(cached).isEqualTo(planCode);
        }
    

        @Test
        @DisplayName("존재하지 않는 planCode → 404")
        void planCodeNotFound() throws Exception {
            // given
            long   userId = 2002L;
            String token  = jwtTokenProvider.generateUserToken(userId);
            PrepareBillingRequest req = fixture.buildRequest("non-existent-plan");

            // when - then
            invoke(token, req).andExpect(status().isNotFound());
        }
    

    /* ====================================================================== */
    /* ======================== Helper methods ============================== */
    /* ====================================================================== */
    private ResultActions invoke(String token, PrepareBillingRequest req) throws Exception {
        return mockMvc.perform(post(ENDPOINT)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
    }

    private String body(ResultActions ra, String field) throws Exception {
        return objectMapper.readTree(ra.andReturn()
                                       .getResponse()
                                       .getContentAsString())
                           .get(field)
                           .asText();
    }
}
