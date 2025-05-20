package com.kingkit.billing_service.integration.billing_history;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.FixtureFactory;
import com.kingkit.lib_test_support.testsupport.util.JwtTestTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BillingHistoryQueryTest extends IntegrationTestSupport {


    @Autowired
    private FixtureFactory fixtureFactory;

    @Autowired
    private JwtTestTokenProvider jwtTestTokenProvider;

    private static final String ENDPOINT = "/billing/history";

    @Test
    @DisplayName("✅ 유저 결제 이력 조회 - 정상 응답")
    void getBillingHistorySuccess() throws Exception {
        // given
        Long userId = 1001L;
        var token = jwtTestTokenProvider.generateUserToken(userId);
        var subscription = fixtureFactory.createActiveSubscription(userId);

        fixtureFactory.createPaymentHistory(subscription, "order-1", PaymentStatus.SUCCESS, LocalDateTime.now(), 10900L, "5월 결제");
        fixtureFactory.createPaymentHistory(subscription, "order-2", PaymentStatus.FAILED, LocalDateTime.now().minusMonths(1), 10900L, "카드 한도 초과");

        // when
        var result = mockMvc.perform(get(ENDPOINT)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.history").isArray())
              .andExpect(jsonPath("$.history[0].orderId").value("order-1"));
    }

    @Test
    @DisplayName("✅ 결제 이력이 없는 경우 - 빈 배열 반환")
    void noBillingHistory() throws Exception {
        // given
        Long userId = 2002L;
        var token = jwtTestTokenProvider.generateUserToken(userId);
        fixtureFactory.createActiveSubscription(userId); // 결제 이력 없이 구독만 생성

        // when
        var result = mockMvc.perform(get(ENDPOINT)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.history").isEmpty());
    }

    @Test
    @DisplayName("❌ 타인 결제 이력 접근 시도 - 403 Forbidden")
    void accessOthersHistoryForbidden() throws Exception {
        // given
        Long userId = 3003L;
        fixtureFactory.createActiveSubscription(userId);
        var attackerToken = jwtTestTokenProvider.generateUserToken(9999L); // 다른 유저의 토큰

        // when
        var result = mockMvc.perform(get(ENDPOINT)
            .header("Authorization", "Bearer " + attackerToken)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isForbidden());
    }
}
