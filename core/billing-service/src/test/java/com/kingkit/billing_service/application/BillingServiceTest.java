package com.kingkit.billing_service.application;

import com.kingkit.billing_service.application.usecase.BillingService;
import com.kingkit.billing_service.client.PaymentClient;
import com.kingkit.billing_service.client.dto.PaymentCommand;
import com.kingkit.billing_service.client.dto.PaymentResult;
import com.kingkit.billing_service.domain.payment.*;
import com.kingkit.billing_service.domain.payment.repository.*;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionRepository;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.dto.request.ManualBillingRequestDto;
import com.kingkit.billing_service.dto.response.ManualBillingResponseDto;
import com.kingkit.billing_service.exception.domain.billing.DuplicateOrderIdException;
import com.kingkit.billing_service.exception.domain.billing.InvalidBillingKeyException;
import com.kingkit.billing_service.util.OrderIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static com.kingkit.billing_service.support.fixture.dto.ManualBillingRequestDtoFixture.*;
import static com.kingkit.billing_service.support.fixture.domain.PaymentMethodFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class BillingServiceTest {

    @InjectMocks
    private BillingService billingService;

    @Mock private PaymentClient paymentClient;
    @Mock private PaymentMethodRepository paymentMethodRepository;
    @Mock private PaymentHistoryRepository paymentHistoryRepository;
    @Mock private OrderIdGenerator orderIdGenerator;
    @Mock private PaymentFailureRepository paymentFailureRepository;
    @Mock private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
    }

        @Test
        @DisplayName("✅ 유효한 수동 결제 요청이 성공적으로 처리된다")
        void manualBillingSuccess() {
        // given
        ManualBillingRequestDto dto = defaultRequest();
        PaymentMethod method = activeMethod(dto.getUserId(), dto.getBillingKey());

        given(paymentMethodRepository.findByUserIdAndBillingKey(dto.getUserId(), dto.getBillingKey()))
                .willReturn(Optional.of(method));
        given(paymentHistoryRepository.existsByOrderId(dto.getOrderId()))
                .willReturn(false);
        given(paymentClient.execute(any(PaymentCommand.class)))
        .willReturn(PaymentResult.successWithPaymentKey("toss-payment-key"));
        given(subscriptionRepository.findByUserIdAndPaymentMethod(dto.getUserId(), method))
        .willReturn(Optional.of(mock(Subscription.class)));
        given(paymentHistoryRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ManualBillingResponseDto result = billingService.executeManualBilling(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(result.getOrderId()).isEqualTo(dto.getOrderId());
        assertThat(result.getPaymentKey()).isEqualTo("toss-payment-key");
        assertThat(result.getPaidAt()).isNotNull();
    }


    @Test
    @DisplayName("❌ 중복 orderId는 예외를 던진다")
    void duplicateOrderId() {
        // given
        ManualBillingRequestDto dto = ManualBillingRequestDto.of(1L, "billing-001", "order-dup", 1000L, "중복");

        
        given(paymentMethodRepository.findByUserIdAndBillingKey(1L, "billing-001"))
                .willReturn(Optional.of(activeMethod(1L, "billing-001")));
        given(paymentHistoryRepository.existsByOrderId("order-dup"))
                .willReturn(true);
        
        // expect
        assertThatThrownBy(() -> billingService.executeManualBilling(dto))
                .isInstanceOf(DuplicateOrderIdException.class);
    }

    @Test
    @DisplayName("❌ billingKey가 없거나 유효하지 않으면 예외를 던진다")
    void invalidBillingKey() {
        // given
        ManualBillingRequestDto dto = ManualBillingRequestDto.of(2L, "invalid-key", "order-002", 10900L, "실패");

        given(paymentMethodRepository.findByUserIdAndBillingKey(2L, "invalid-key"))
                .willReturn(Optional.empty());

        // expect
        assertThatThrownBy(() -> billingService.executeManualBilling(dto))
                .isInstanceOf(InvalidBillingKeyException.class);
    }

        @Test
        @DisplayName("❌ Toss 결제 실패 시 FAILED 상태로 처리된다")
        void tossPaymentFailHandled() {
        // given
        ManualBillingRequestDto dto = ManualBillingRequestDto.of(3L, "billing-fail", "order-fail", 1000L, "테스트");
        PaymentMethod method = activeMethod(dto.getUserId(), dto.getBillingKey());

        given(paymentMethodRepository.findByUserIdAndBillingKey(dto.getUserId(), dto.getBillingKey()))
                .willReturn(Optional.of(method));
        given(paymentHistoryRepository.existsByOrderId(dto.getOrderId()))
                .willReturn(false);
        given(paymentClient.execute(any(PaymentCommand.class)))
        .willReturn(PaymentResult.fail(HttpStatus.BAD_REQUEST, "PG 오류"));
        given(subscriptionRepository.findByUserIdAndPaymentMethod(dto.getUserId(), method))
        .willReturn(Optional.of(mock(Subscription.class)));
        given(paymentHistoryRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));
        given(paymentFailureRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ManualBillingResponseDto result = billingService.executeManualBilling(dto);

        // then
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.FAILED);
        }

}
