package com.kingkit.billing_service.integration.subscription_status;

import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.SubscriptionStatus;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionRepository;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.config.JwtTestUtilConfig;
import com.kingkit.billing_service.support.core.FixtureFactory;
import com.kingkit.lib_test_support.testsupport.util.JwtTestTokenProvider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Import(JwtTestUtilConfig.class)
class SubscriptionStatusQueryTest extends IntegrationTestSupport {

    @Autowired private JwtTestTokenProvider jwtTokenProvider;
    @Autowired private FixtureFactory fixtureFactory;
    @Autowired private SubscriptionRepository subscriptionRepository;

    @Test
    @DisplayName("✅ JWT 인증된 사용자의 구독 상태 조회 성공")
    void getSubscriptionStatus_withValidToken() throws Exception {
        // given
        Long userId = 1001L;
        Subscription subscription = fixtureFactory.createActiveSubscription(userId);  // ACTIVE + Plan + PaymentMethod
        String token = jwtTokenProvider.generateToken(userId, "ROLE_USER");

        // when
        ResultActions result = mockMvc.perform(get("/subscription")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        );
        result.andDo(print());

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.active").value(true))
              .andExpect(jsonPath("$.planName").value(subscription.getPlan().getName()))
              .andExpect(jsonPath("$.cardInfo.cardCompany").value(subscription.getPaymentMethod().getCardCompany()))
              .andExpect(jsonPath("$.cardInfo.cardNumberMasked").value(subscription.getPaymentMethod().getCardNumberMasked()));

        // 상태 정합성 검증
        Subscription loaded = subscriptionRepository.findById(subscription.getId()).orElseThrow();
        assertThat(loaded.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
    }

    @Test
    @DisplayName("✅ 활성 구독이 없을 경우 isActive=false로 응답")
    void getSubscriptionStatus_withoutActiveSubscription() throws Exception {
        // given
        Long userId = 9999L;
        String token = jwtTokenProvider.generateToken(userId, "ROLE_USER");
        // ❌ 구독 미생성

        // when
        ResultActions result = mockMvc.perform(get("/subscription")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        );
        result.andDo(print());
        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.active").value(false))
              .andExpect(jsonPath("$.planName").doesNotExist())
              .andExpect(jsonPath("$.cardInfo").doesNotExist());
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
