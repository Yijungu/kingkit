package com.kingkit.billing_service.api.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.application.usecase.BillingHistoryService;
import com.kingkit.billing_service.application.usecase.BillingService;
import com.kingkit.billing_service.application.usecase.RetryService;
import com.kingkit.billing_service.config.AuthenticationPrincipalResolverConfig;
import com.kingkit.billing_service.config.SecurityConfig;
import com.kingkit.billing_service.config.external.ExternalSecurityConfig;
import com.kingkit.billing_service.config.external.JwtConfig;
import com.kingkit.billing_service.dto.response.*;
import com.kingkit.billing_service.support.config.JwtTestUtilConfig;
import com.kingkit.billing_service.util.TossSignatureVerifier;
import com.kingkit.lib_security.jwt.JwtTokenProvider;
import com.kingkit.lib_test_support.testsupport.util.JwtTestTokenProvider;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.kingkit.billing_service.support.fixture.dto.PrepareBillingRequestFixture.defaultRequest;
import static com.kingkit.billing_service.support.fixture.dto.RetryPaymentRequestFixture.defaultRetryRequest;
import static org.mockito.Answers.valueOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(ExternalBillingController.class)
@ActiveProfiles("test")
@Import({JwtConfig.class, JwtTestUtilConfig.class, ExternalSecurityConfig.class, SecurityConfig.class, AuthenticationPrincipalResolverConfig.class})
@EnableAutoConfiguration(excludeName = {
        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
        "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration",
        "org.springframework.boot.autoconfigure.data.jpa.JpaAuditingAutoConfiguration"
})
class ExternalBillingControllerTest {

    private static final String AUTH_HEADER = "Authorization";
    private static final long USER_ID = 1001L;
    private static final String ROLE = "USER";
    private String token;

    @Resource private MockMvc mockMvc;
    @Resource private ObjectMapper objectMapper;

    @MockBean private BillingService billingService;
    @MockBean private RetryService retryService;
    @MockBean private BillingHistoryService billingHistoryService;

    @Autowired private JwtTokenProvider tokenProvider;
    @MockBean private TossSignatureVerifier tossSignatureVerifier;
    
    @BeforeEach
    void setUp() {
        this.token = "Bearer " + tokenProvider.createAccessToken(String.valueOf(USER_ID), ROLE);
    }

    @Test
    @DisplayName("✅ prepareBilling: 결제 수단 등록 요청 시 Checkout URL 반환")
    void prepareBilling_shouldReturnCheckoutUrl() throws Exception {
        var request = defaultRequest();
        var response = new PrepareBillingResponse("https://checkout.toss.com", "order-123", "user-1001");

        when(billingService.prepareBilling(eq(USER_ID), eq(request))).thenReturn(response);

        mockMvc.perform(post("/billing/prepare")
                        .header(AUTH_HEADER, token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutUrl").value("https://checkout.toss.com"));
    }

    @Test
    @DisplayName("✅ getSubscriptionStatus: 구독 상태 조회 성공")
    void getSubscriptionStatus_shouldReturnSubscriptionInfo() throws Exception {
        var dto = SubscriptionStatusResponseDto.sample(true, "basic-monthly", "국민카드", "****-****-****-1234");

        when(billingService.getSubscriptionStatus(USER_ID)).thenReturn(dto);

        mockMvc.perform(get("/subscription").header(AUTH_HEADER, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("✅ cancelSubscription: 구독 해지 요청 처리 성공")
    void cancelSubscription_shouldReturnCanceledStatus() throws Exception {
        var dto = SubscriptionCancelResponseDto.simple("이미 해지된 구독입니다.");

        when(billingService.cancelSubscription(USER_ID)).thenReturn(dto);

        mockMvc.perform(delete("/subscription").header(AUTH_HEADER, token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }

    @Test
    @DisplayName("✅ retryPayment: 결제 실패 재시도 요청 처리 성공")
    void retryPayment_shouldReturnSuccessStatus() throws Exception {
        var request = defaultRetryRequest();
        var response = RetryPaymentResponse.test("pay-123", "order-789", "SUCCESS", "2024-01-01T00:00:00");

         // ✅ 매처 완화
        when(retryService.retryFailedPayment(any(), anyLong()))
            .thenReturn(response);

        mockMvc.perform(post("/billing/retry")
                        .header(AUTH_HEADER, token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                        .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("✅ getBillingHistory: 결제 이력 조회 (페이징 포함)")
    void getBillingHistory_shouldReturnPagedHistory() throws Exception {
        var samples = List.of(
            PaymentHistoryResponse.test("order-1", "2024-05-01T10:00:00", 10900L, "SUCCESS", "5월 결제"),
            PaymentHistoryResponse.test("order-2", "2024-04-01T10:00:00", 10900L, "FAILED", "카드 한도 초과")
        );
        Page<PaymentHistoryResponse> page = new PageImpl<>(samples);

        when(billingHistoryService.getUserBillingHistory(eq(USER_ID), any())).thenReturn(page);

        mockMvc.perform(get("/billing/history")
                        .header(AUTH_HEADER, token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].orderId").value("order-1"))
                .andExpect(jsonPath("$.content[1].status").value("FAILED"));
    }
}
