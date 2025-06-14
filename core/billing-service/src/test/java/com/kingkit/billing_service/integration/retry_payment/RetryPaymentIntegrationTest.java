package com.kingkit.billing_service.integration.retry_payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.domain.payment.repository.PaymentFailureRepository;
import com.kingkit.billing_service.dto.request.RetryPaymentRequest;
import com.kingkit.billing_service.integration.common.IntegrationTestSupport;
import com.kingkit.billing_service.support.config.JwtTestUtilConfig;
import com.kingkit.billing_service.support.core.FixtureFactory;
import com.kingkit.billing_service.support.stub.TossMockStub;
import com.kingkit.lib_test_support.testsupport.util.JwtTestTokenProvider;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Slf4j
@Import(JwtTestUtilConfig.class)
class RetryPaymentIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FixtureFactory fixtureFactory;

    @Autowired
    private PaymentFailureRepository failureRepository;

    @Autowired
    private JwtTestTokenProvider tokenProvider;

    private static final String ENDPOINT = "/billing/retry";

    @BeforeEach
    void setup() {
        TossMockStub.stub200ResponseBilling2001();     // billingKey=test-404 → 404
    }

    @Test
    @DisplayName("✅ 재시도 성공 → resolved 처리")
    void retrySuccess() throws Exception {
        // given
        Long userId = 2001L;
        var sub = fixtureFactory.createActiveSubscription(userId);
        var failure = fixtureFactory.createPaymentFailure(sub, false, 0);
        var token = tokenProvider.generateUserToken(userId);

        var dto = new RetryPaymentRequest(sub.getId(), "retry-20240519-001", 10900L);
    
        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isOk())
              .andExpect(jsonPath("$.status").value(PaymentStatus.SUCCESS.name()));
        assertThat(failureRepository.findById(failure.getId()).get().isResolved()).isTrue();
    }

    @Test
    @DisplayName("❌ retryCount >= 3이면 재시도 중단 (200 OK)")
    void retryLimitReached() throws Exception {
        // given
        Long userId = 2002L;
        var sub = fixtureFactory.createActiveSubscription(userId);
        var token = tokenProvider.generateUserToken(userId);
        var failure = fixtureFactory.createPaymentFailure(sub, false, 3);

        var dto = new RetryPaymentRequest(sub.getId(), "retry-limit", 10900L);

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isBadRequest())
              .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("❌ resolved 상태이면 재시도 불가 (400)")
    void alreadyResolved() throws Exception {
        // given
        Long userId = 2003L;
        var sub = fixtureFactory.createActiveSubscription(userId);
        fixtureFactory.createPaymentFailure(sub, true, 1);
        var token = tokenProvider.generateUserToken(userId);
        var failure = fixtureFactory.createPaymentFailure(sub, true, 3);
        var dto = new RetryPaymentRequest(sub.getId(), "retry-resolved", 10900L);

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("❌ 인증 없음 - 401 Unauthorized")
    void unauthenticatedRequest() throws Exception {
        // given
        var dto = new RetryPaymentRequest(999L, "unauth-test", 10900L);

        // when
        ResultActions result = mockMvc.perform(post(ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)));

        // then
        result.andExpect(status().isUnauthorized());
    }
}
