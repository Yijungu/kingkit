package com.kingkit.billing_service.api.webhook;

import com.kingkit.billing_service.application.usecase.TossPaymentQueryService;
import com.kingkit.billing_service.application.usecase.WebhookService;
import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TossWebhookProcessorTest {

    @Mock
    TossPaymentQueryService queryService;
    @Mock
    WebhookService webhookService;

    @InjectMocks
    TossWebhookProcessor processor;

    TossWebhookRequest request;

    @BeforeEach
    void setUp() {
        request = new TossWebhookRequest(
                "SUBSCRIPTION_REGISTERED",
                "key",
                "user-1",
                "order",
                "2024-01-01T00:00:00",
                new TossWebhookRequest.CardInfo("VISA","****")
        );
    }

    @Test
    void handlesSubscriptionRegistered() {
        processor.process(request);
        verify(webhookService).handleSubscriptionRegistered(request);
    }

    @Test
    void handlesPaymentSuccessWhenVerified() {
        request = new TossWebhookRequest(
                "SUBSCRIPTION_PAYMENT_SUCCESS",
                "key",
                "user-1",
                "order",
                "2024-01-01T00:00:00",
                new TossWebhookRequest.CardInfo("VISA","****")
        );
        when(queryService.getStatus("key")).thenReturn(PaymentStatus.SUCCESS);
        processor.process(request);
        verify(webhookService).handlePaymentSuccess(request);
    }

    @Test
    void skipsPaymentSuccessWhenNotVerified() {
        request = new TossWebhookRequest(
                "SUBSCRIPTION_PAYMENT_SUCCESS",
                "key",
                "user-1",
                "order",
                "2024-01-01T00:00:00",
                new TossWebhookRequest.CardInfo("VISA","****")
        );
        when(queryService.getStatus("key")).thenReturn(PaymentStatus.FAILED);
        processor.process(request);
        verify(webhookService, never()).handlePaymentSuccess(any());
    }

    @Test
    void handlesPaymentFailedWhenVerified() {
        request = new TossWebhookRequest(
                "SUBSCRIPTION_PAYMENT_FAILED",
                "key",
                "user-1",
                "order",
                "2024-01-01T00:00:00",
                new TossWebhookRequest.CardInfo("VISA","****")
        );
        when(queryService.getStatus("key")).thenReturn(PaymentStatus.FAILED);
        processor.process(request);
        verify(webhookService).handlePaymentFailed(request);
    }

    @Test
    void ignoresUnknownEvent() {
        request = new TossWebhookRequest(
                "OTHER",
                "key",
                "user-1",
                "order",
                "2024-01-01T00:00:00",
                new TossWebhookRequest.CardInfo("VISA","****")
        );
        processor.process(request);
        verifyNoInteractions(webhookService);
    }
}
