package com.kingkit.billing_service.application.usecase;

import com.kingkit.billing_service.client.PaymentClient;
import com.kingkit.billing_service.client.dto.PaymentCommand;
import com.kingkit.billing_service.client.dto.PaymentResult;
import com.kingkit.billing_service.domain.payment.*;
import com.kingkit.billing_service.domain.payment.repository.PaymentFailureRepository;
import com.kingkit.billing_service.domain.payment.repository.PaymentHistoryRepository;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.SubscriptionPlan;
import com.kingkit.billing_service.domain.subscription.SubscriptionStatus;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionRepository;
import com.kingkit.billing_service.dto.response.AdminBillingTriggerResponse;
import com.kingkit.billing_service.logging.TriggerLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingTriggerServiceTest {

    @Mock SubscriptionRepository subscriptionRepository;
    @Mock PaymentHistoryRepository paymentHistoryRepository;
    @Mock PaymentFailureRepository paymentFailureRepository;
    @Mock PaymentClient paymentClient;
    @Mock TriggerLogger triggerLogger;

    @InjectMocks BillingTriggerService service;

    @BeforeEach
    void mockSaveReturns() {
        lenient().when(paymentHistoryRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        lenient().when(paymentFailureRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        lenient().when(subscriptionRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));
    }

    private Subscription subscriptionWithMethod(boolean active) {
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L)
                .planCode("p")
                .name("plan")
                .price(1000L)
                .durationDays(30)
                .isActive(true)
                .build();
        PaymentMethod method = PaymentMethod.builder()
                .id(1L)
                .userId(1L)
                .billingKey("bkey")
                .cardCompany("c")
                .cardNumberMasked("****")
                .registeredAt(LocalDateTime.now())
                .isActive(active)
                .build();
        return Subscription.builder()
                .id(1L)
                .userId(1L)
                .plan(plan)
                .paymentMethod(method)
                .status(SubscriptionStatus.ACTIVE)
                .startedAt(LocalDateTime.now())
                .nextBillingAt(LocalDateTime.now())
                .build();
    }

    @Test
    void triggerBillingSuccess() {
        Subscription sub = subscriptionWithMethod(true);
        when(subscriptionRepository.findByNextBillingAtBetween(any(), any()))
                .thenReturn(List.of(sub));
        when(paymentClient.execute(any(PaymentCommand.class)))
                .thenReturn(PaymentResult.successWithPaymentKey("pay"));

        AdminBillingTriggerResponse resp = service.trigger(LocalDate.now(), null);

        assertThat(resp.getSuccessCount()).isEqualTo(1);
        assertThat(resp.getFailureCount()).isEqualTo(0);
    }

    @Test
    void triggerBillingFailsWhenMethodInactive() {
        Subscription sub = subscriptionWithMethod(false);
        when(subscriptionRepository.findByNextBillingAtBetween(any(), any()))
                .thenReturn(List.of(sub));

        AdminBillingTriggerResponse resp = service.trigger(LocalDate.now(), null);

        assertThat(resp.getSuccessCount()).isZero();
        assertThat(resp.getFailureCount()).isEqualTo(1);
        verify(paymentClient, never()).execute(any());
    }

    @Test
    void triggerBillingHandlesClientException() {
        Subscription sub = subscriptionWithMethod(true);
        when(subscriptionRepository.findByNextBillingAtBetween(any(), any()))
                .thenReturn(List.of(sub));
        when(paymentClient.execute(any(PaymentCommand.class)))
                .thenThrow(new RuntimeException("boom"));

        AdminBillingTriggerResponse resp = service.trigger(LocalDate.now(), null);

        assertThat(resp.getFailureCount()).isEqualTo(1);
        verify(triggerLogger).logFailure(eq(sub), any());
    }
}
