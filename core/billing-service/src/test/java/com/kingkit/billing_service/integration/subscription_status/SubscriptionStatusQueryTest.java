package com.kingkit.billing_service.integration.subscription_status;

import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.lib_test_support.testsupport.util.JwtTestTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SubscriptionStatusQueryTest extends IntegrationTestSupport {

    @Autowired
    private JwtTestTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("✅ JWT 인증된 사용자의 구독 상태 조회 성공")
    void getSubscriptionStatus_withValidToken() throws Exception {
        // given
        Long userId = 1001L;
        String token = jwtTokenProvider.generateToken(userId, "ROLE_USER");

        // TODO: 테스트용 데이터로 ACTIVE 상태의 Subscription, Plan, PaymentMethod가 DB에 있어야 함

        // when
        ResultActions result = mockMvc.perform(get("/subscription")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.isActive").value(true))
              .andExpect(jsonPath("$.planName").isString())
              .andExpect(jsonPath("$.cardInfo.cardCompany").isString())
              .andExpect(jsonPath("$.cardInfo.cardNumberMasked").isString());
    }

    @Test
    @DisplayName("✅ 활성 구독이 없을 경우 isActive=false로 응답")
    void getSubscriptionStatus_withoutActiveSubscription() throws Exception {
        // given
        Long userId = 9999L; // 구독 없는 유저
        String token = jwtTokenProvider.generateToken(userId, "ROLE_USER");

        // when
        ResultActions result = mockMvc.perform(get("/subscription")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    @DisplayName("❌ 인증되지 않은 요청은 401을 반환한다")
    void getSubscriptionStatus_withoutToken() throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/subscription")
            .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isUnauthorized());
    }
}
