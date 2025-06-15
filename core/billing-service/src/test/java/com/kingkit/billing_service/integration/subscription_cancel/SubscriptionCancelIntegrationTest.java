package com.kingkit.billing_service.integration.subscription_cancel;

import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.SubscriptionStatus;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionRepository;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.config.JwtTestUtilConfig;
import com.kingkit.billing_service.support.core.FixtureFactory;
import com.kingkit.billing_service.support.stub.TossMockStub;
import com.kingkit.lib_test_support.testsupport.util.JwtTestTokenProvider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import(JwtTestUtilConfig.class)
class SubscriptionCancelIntegrationTest extends IntegrationTestSupport {

    @Autowired private SubscriptionRepository subscriptionRepository;
    @Autowired private FixtureFactory fixtureFactory;
    @Autowired private JwtTestTokenProvider jwtTestTokenProvider;

    @Test
    @DisplayName("✅ 구독이 활성 상태인 사용자는 성공적으로 구독을 해지할 수 있다")
    void cancelActiveSubscription() throws Exception {
        // given
        Long userId = 1001L;
        Subscription subscription = fixtureFactory.createActiveSubscription(userId);
        String billingKey = subscription.getPaymentMethod().getBillingKey();

        TossMockStub.stubDeleteBillingKeySuccess(billingKey);  // billingKey 실제값으로 Stub 등록

        String token = jwtTestTokenProvider.generateUserToken(userId);
        // when
        ResultActions result = mockMvc.perform(delete("/subscription")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.planName").value(subscription.getPlan().getName()))
              .andExpect(jsonPath("$.cancelledAt").exists())
              .andExpect(jsonPath("$.message").value("구독이 성공적으로 해지되었습니다."));

        // 상태 변화 검증
        Subscription updated = subscriptionRepository.findById(subscription.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(SubscriptionStatus.CANCELED);
        assertThat(updated.getPaymentMethod().isActive()).isFalse();
    }

    @Test
    @DisplayName("❌ 이미 해지된 구독은 다시 해지할 수 없다")
    void cancelAlreadyCanceledSubscription() throws Exception {
        // given
        Long userId = 1002L;
        Subscription subscription = fixtureFactory.createActiveSubscription(userId);
        subscription.markCanceled();
        subscriptionRepository.save(subscription);

        String token = jwtTestTokenProvider.generateUserToken(userId);

        // when
        ResultActions result = mockMvc.perform(delete("/subscription")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.message").value("해지 가능한 구독이 없습니다."));
    }

   @Test
    @DisplayName("❌ 활성 구독이 존재하지 않으면 400 반환")
    void cancelSubscriptionWhenNotExists() throws Exception {
        // given
        Long userId = 9999L;
        String token = jwtTestTokenProvider.generateUserToken(userId);

        // when
        ResultActions result = mockMvc.perform(delete("/subscription")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("해지 가능한 구독이 없습니다."));
    }


    @Test
    @DisplayName("❌ 결제 수단이 없거나 비활성화된 경우 - 해지 실패")
    void cancelSubscriptionWithoutBillingKey() throws Exception {
        // given
        Long userId = 1003L;
        String planCode = "plan-" + System.currentTimeMillis();
        fixtureFactory.createPlan(planCode, 10000, 30);
        fixtureFactory.createSubscriptionWithoutBillingKey(userId, planCode);

        String token = jwtTestTokenProvider.generateUserToken(userId);

        // when
        ResultActions result = mockMvc.perform(delete("/subscription")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("유효하지 않은 billingKey: billingKey 없음"));
    }

}
