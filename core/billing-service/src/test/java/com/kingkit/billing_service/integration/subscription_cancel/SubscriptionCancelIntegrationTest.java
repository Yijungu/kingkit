package com.kingkit.billing_service.integration.subscription_cancel;

import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.SubscriptionStatus;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionRepository;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.FixtureFactory;
import com.kingkit.lib_test_support.testsupport.util.JwtTestTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SubscriptionCancelIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private FixtureFactory fixtureFactory;

    @Autowired
    private JwtTestTokenProvider jwtTestTokenProvider;


    @Test
    @DisplayName("✅ 구독이 활성 상태인 사용자는 성공적으로 구독을 해지할 수 있다")
    void cancelActiveSubscription() throws Exception {
        // given
        Long userId = 1001L;
        Subscription subscription = fixtureFactory.createActiveSubscription(userId);
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
        subscription.markCanceled(); // 수동 상태 전이
        subscriptionRepository.save(subscription);

        String token = jwtTestTokenProvider.generateUserToken(userId);

        // when
        ResultActions result = mockMvc.perform(delete("/subscription")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.message").value("이미 취소된 구독입니다."));
    }

    @Test
    @DisplayName("❌ 인증되지 않은 사용자는 구독 해지를 요청할 수 없다")
    void cancelSubscriptionWithoutToken() throws Exception {
        // when
        ResultActions result = mockMvc.perform(delete("/subscription")
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("❌ 활성 구독이 존재하지 않으면 400 반환")
    void cancelSubscriptionWhenNotExists() throws Exception {
        // given
        Long userId = 9999L; // 존재하지 않는 유저
        String token = jwtTestTokenProvider.generateUserToken(userId);

        // when
        ResultActions result = mockMvc.perform(delete("/subscription")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.message").value("구독이 존재하지 않습니다."));
    }
}
