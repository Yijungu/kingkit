package com.kingkit.billing_service.application.usecase;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookHandlerServiceTest {

    @Mock TossPaymentQueryService paymentQueryService;
    @Mock WebhookService webhookService;

    @InjectMocks WebhookHandlerService handler;

    private TossWebhookRequest successReq() {
        return new TossWebhookRequest(
                "SUBSCRIPTION_PAYMENT_SUCCESS",
                "billing-1",
                "user-1",
                "order-1",
                "2024-06-01T00:00:00Z",
                null
        );
    }

    @Test
    void paymentSuccessEventCallsWebhookOnVerifiedStatus() {
        when(paymentQueryService.getStatus("billing-1"))
                .thenReturn(PaymentStatus.SUCCESS);

        handler.handle(successReq());

        verify(webhookService).handlePaymentSuccess(any());
    }

    @Test
    void paymentSuccessEventIgnoredWhenStatusMismatch() {
        when(paymentQueryService.getStatus("billing-1"))
                .thenReturn(PaymentStatus.FAILED);

        handler.handle(successReq());

        verify(webhookService, never()).handlePaymentSuccess(any());
    }

    @Test
    void exceptionDuringVerificationResultsInNoCall() {
        when(paymentQueryService.getStatus("billing-1"))
                .thenThrow(new RuntimeException("boom"));

        handler.handle(successReq());

        verify(webhookService, never()).handlePaymentSuccess(any());
    }
}
