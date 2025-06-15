package com.kingkit.billing_service.application;

import com.kingkit.billing_service.application.usecase.RetryService;
import com.kingkit.billing_service.client.PaymentClient;
import com.kingkit.billing_service.client.dto.PaymentCommand;
import com.kingkit.billing_service.client.dto.PaymentResult;
import com.kingkit.billing_service.domain.payment.*;
import com.kingkit.billing_service.domain.payment.repository.PaymentFailureRepository;
import com.kingkit.billing_service.domain.payment.repository.PaymentHistoryRepository;
import com.kingkit.billing_service.domain.payment.repository.PaymentMethodRepository;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionRepository;
import com.kingkit.billing_service.dto.request.RetryPaymentRequest;
import com.kingkit.billing_service.dto.response.RetryPaymentResponse;
import com.kingkit.billing_service.exception.domain.billing.InvalidBillingKeyException;
import com.kingkit.billing_service.exception.domain.billing.RetryLimitExceededException;
import com.kingkit.billing_service.support.fixture.composite.WebhookTestFixture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RetryServiceTest {

    @InjectMocks
    private RetryService retryService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PaymentFailureRepository paymentFailureRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private PaymentClient paymentClient;   // TossClient 대신 PaymentClient mock

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("✅ 유효한 재시도 요청이 성공 처리된다")
    void retryPaymentSuccess() {
        // ──────────────── given ────────────────
        Long userId = 1001L;
        RetryPaymentRequest request =
            new RetryPaymentRequest(1L, "retry-order-001", 10_900L);

        Subscription   subscription = WebhookTestFixture.subscription(userId);
        PaymentFailure failure      = WebhookTestFixture.unresolvedFailure(subscription, 0);
        PaymentMethod  method       = WebhookTestFixture.activePaymentMethod(userId);

        when(subscriptionRepository.findById(1L))
            .thenReturn(Optional.of(subscription));
        when(paymentFailureRepository.findTopBySubscriptionOrderByFailedAtDesc(subscription))
            .thenReturn(Optional.of(failure));
        when(paymentMethodRepository.findByUserIdAndIsActiveTrue(userId))
            .thenReturn(Optional.of(method));

        // (1) PaymentClient 가 반환할 결과 객체 준비
        PaymentResult result = PaymentResult.builder()
                                            .paymentKey("pay-retry-001")
                                            .build();

        // (2) 올바른 메서드 / 시그니처로 스텁
        when(paymentClient.execute(any(PaymentCommand.class)))
            .thenReturn(result);

        when(paymentHistoryRepository.save(any()))
            .thenAnswer(inv -> inv.getArgument(0));

        // ──────────────── when ────────────────
        RetryPaymentResponse response =
            retryService.retryFailedPayment(request, userId);

        // ──────────────── then ────────────────
        assertThat(response.getPaymentKey()).isEqualTo("pay-retry-001");
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.SUCCESS);

        // execute() 가 정확히 한 번 호출됐는지 검증 (옵션)
        verify(paymentClient, times(1)).execute(any(PaymentCommand.class));
    }


    @Test
    @DisplayName("❌ 유효하지 않은 billingKey일 경우 예외를 던진다")
    void retryInvalidBillingKey() {
        // given
        Long userId = 9999L;
        RetryPaymentRequest request = new RetryPaymentRequest(9L, "invalid-order", 10000L);
        Subscription subscription = WebhookTestFixture.subscription(userId);
        PaymentFailure failure = WebhookTestFixture.unresolvedFailure(subscription, 0);

        when(subscriptionRepository.findById(9L)).thenReturn(Optional.of(subscription));
        when(paymentFailureRepository.findTopBySubscriptionOrderByFailedAtDesc(subscription)).thenReturn(Optional.of(failure));
        when(paymentMethodRepository.findByUserIdAndIsActiveTrue(userId)).thenReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> retryService.retryFailedPayment(request, userId))
                .isInstanceOf(InvalidBillingKeyException.class);
    }

    @Test
    @DisplayName("❌ 재시도 횟수 초과 시 예외를 던진다")
    void retryLimitExceeded() {
        // given
        Long userId = 1234L;
        RetryPaymentRequest request = new RetryPaymentRequest(5L, "limit-order", 10900L);
        Subscription subscription = WebhookTestFixture.subscription(userId);
        PaymentFailure failure = WebhookTestFixture.unresolvedFailure(subscription, 3);

        when(subscriptionRepository.findById(5L)).thenReturn(Optional.of(subscription));
        when(paymentFailureRepository.findTopBySubscriptionOrderByFailedAtDesc(subscription)).thenReturn(Optional.of(failure));

        // expect
        assertThatThrownBy(() -> retryService.retryFailedPayment(request, userId))
                .isInstanceOf(RetryLimitExceededException.class);
    }
}
