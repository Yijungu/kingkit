package com.kingkit.billing_service.integration.billing_history;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.config.JwtTestUtilConfig;
import com.kingkit.billing_service.support.core.FixtureFactory;
import com.kingkit.lib_test_support.testsupport.util.JwtTestTokenProvider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(JwtTestUtilConfig.class)
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
        String token = jwtTestTokenProvider.generateUserToken(userId);
        var subscription = fixtureFactory.createActiveSubscription(userId);

        fixtureFactory.createPaymentHistory(
            subscription,
            "order-1",
            PaymentStatus.SUCCESS,
            LocalDateTime.now(),
            10900L,
            "5월 결제"
        );
        fixtureFactory.createPaymentHistory(
            subscription,
            "order-2",
            PaymentStatus.FAILED,
            LocalDateTime.now().minusMonths(1),
            10900L,
            "카드 한도 초과"
        );

        // when
        var result = mockMvc.perform(get(ENDPOINT)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())         // history → content
            .andExpect(jsonPath("$.content[0].orderId").value("order-1"));
    }

    @Test
    @DisplayName("✅ 결제 이력이 없는 경우 - 빈 배열 반환")
    void noBillingHistory() throws Exception {
        // given
        Long userId = 2002L;
        String token = jwtTestTokenProvider.generateUserToken(userId);
        fixtureFactory.createActiveSubscription(userId);

        // when
        var result = mockMvc.perform(get(ENDPOINT)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.content").isArray())
              .andExpect(jsonPath("$.content").isEmpty());
    }
}
