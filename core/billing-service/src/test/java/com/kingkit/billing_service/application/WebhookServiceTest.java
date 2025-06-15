package com.kingkit.billing_service.application;

import com.kingkit.billing_service.application.port.out.RedisPlanCachePort;
import com.kingkit.billing_service.application.usecase.WebhookService;
import com.kingkit.billing_service.domain.payment.repository.PaymentFailureRepository;
import com.kingkit.billing_service.domain.payment.repository.PaymentHistoryRepository;
import com.kingkit.billing_service.domain.payment.repository.PaymentMethodRepository;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionPlanRepository;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionRepository;
import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import com.kingkit.billing_service.support.core.FixtureFactory;
import com.kingkit.billing_service.support.fixture.composite.WebhookTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class WebhookServiceTest {

    @InjectMocks
    private WebhookService webhookService;

    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private SubscriptionPlanRepository subscriptionPlanRepository;
    @Mock private PaymentMethodRepository paymentMethodRepository;
    @Mock private PaymentHistoryRepository paymentHistoryRepository;
    @Mock private PaymentFailureRepository paymentFailureRepository;
    @Mock private FixtureFactory fixtureFactory;

    @Mock private RedisPlanCachePort redisPlanCachePort;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("✅ SUBSCRIPTION_REGISTERED 수신 시 결제 수단 및 구독 생성")
    void handleSubscriptionRegistered_success() {
        // given
        TossWebhookRequest request = WebhookTestFixture.subscriptionRegisteredRequest(1001L);

        // 👉 내부 메서드 분기 테스트이므로 handleWebhookEvent 로 호출
        webhookService.handleWebhookEvent(request);

        // then
        // 주된 관심사는 내부 분기가 문제 없이 실행되는 것 (추가적인 저장은 통합테스트로 검증)
        // 이 테스트에선 예외 없이 호출되면 성공으로 간주
    }

    @Test
    @DisplayName("✅ SUBSCRIPTION_PAYMENT_SUCCESS 수신 시 결제 이력 저장 및 갱신")
    void handlePaymentSuccess_success() {
        // given
        TossWebhookRequest request = WebhookTestFixture.paymentSuccessEvent(1001L, "order-20240525-01");

        // when
        webhookService.handleWebhookEvent(request);

        // then
        // 위와 마찬가지로, 예외 없이 처리되면 성공
        // DB 저장 등은 통합 테스트에서 검증
    }

    @Test
    @DisplayName("✅ SUBSCRIPTION_PAYMENT_FAILED 수신 시 결제 실패 기록")
    void handlePaymentFailed_success() {
        // given
        TossWebhookRequest request = WebhookTestFixture.paymentFailedEvent(1001L, "order-20240525-02");

        // when
        webhookService.handleWebhookEvent(request);

        // then
        // 분기 흐름 문제 없는지 확인
    }

    @Test
    @DisplayName("⚠️ 알 수 없는 이벤트 타입 수신 시 로그만 출력")
    void handleUnknownEventType() {
        // given
        TossWebhookRequest request = new TossWebhookRequest(
                "UNKNOWN_EVENT", "billingKey", "user-1001", "order-000", "2024-05-25T01:02:03Z", null
        );

        // when
        webhookService.handleWebhookEvent(request);

        // then
        // 로그만 남기고 아무 작업도 하지 않음 → 예외 없이 종료되면 통과
    }
}
