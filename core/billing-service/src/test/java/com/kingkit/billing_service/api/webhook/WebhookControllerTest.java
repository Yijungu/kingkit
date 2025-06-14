package com.kingkit.billing_service.api.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kingkit.billing_service.application.usecase.TossPaymentQueryService;
import com.kingkit.billing_service.application.usecase.WebhookHandlerService;
import com.kingkit.billing_service.application.usecase.WebhookService;
import com.kingkit.billing_service.config.webhook.WebhookSecurityConfig;
import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import com.kingkit.billing_service.support.fixture.composite.WebhookTestFixture;
import com.kingkit.billing_service.util.TossSignatureVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
@AutoConfigureMockMvc(addFilters = false) 
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebhookService webhookService;

    @MockBean
    private TossSignatureVerifier tossSignatureVerifier;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean WebhookHandlerService webhookHandlerService;

    @MockBean TossPaymentQueryService tossPaymentQueryService;


    @BeforeEach
    void setUp() {
        // ✅ 모든 테스트에서 서명 검증 통과 처리
        Mockito.when(tossSignatureVerifier.verify(Mockito.any(), Mockito.anyString()))
               .thenReturn(true);
    }

    @Test
    @DisplayName("✅ 구독 등록 Webhook 처리")
    void handleSubscriptionRegistered() throws Exception {
        TossWebhookRequest request = WebhookTestFixture.subscriptionRegisteredWebhook();

        mockMvc.perform(post("/webhook/toss")
                        .header("Toss-Signature", "dummy-signature")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(webhookService).handleSubscriptionRegistered(request);
    }

    @Test
    @DisplayName("✅ 결제 성공 Webhook 처리")
    void handlePaymentSuccess() throws Exception {
        TossWebhookRequest request = WebhookTestFixture.paymentSuccessWebhook();

        // 👉 반드시 상태값 설정 필요
        Mockito.when(tossPaymentQueryService.getStatus(request.billingKey()))
            .thenReturn(PaymentStatus.SUCCESS);

        mockMvc.perform(post("/webhook/toss")
                        .header("Toss-Signature", "dummy-signature")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(webhookService).handlePaymentSuccess(request);
    }

    @Test
    @DisplayName("✅ 결제 실패 Webhook 처리")
    void handlePaymentFailed() throws Exception {
        TossWebhookRequest request = WebhookTestFixture.paymentFailedWebhook();

        // 👉 반드시 상태값 설정 필요
        Mockito.when(tossPaymentQueryService.getStatus(request.billingKey()))
            .thenReturn(PaymentStatus.FAILED);

        mockMvc.perform(post("/webhook/toss")
                        .header("Toss-Signature", "dummy-signature")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(webhookService).handlePaymentFailed(request);
    }
}
