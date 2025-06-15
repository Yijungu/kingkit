package com.kingkit.billing_service.domain.payment;

import com.kingkit.billing_service.domain.subscription.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class PaymentHistoryTest {

    private Subscription dummySub;

    @BeforeEach
    void setUp() {
        dummySub = Subscription.builder()
                .id(1L)
                .userId(100L)
                .status(null)
                .nextBillingAt(LocalDateTime.now().plusDays(1))
                .startedAt(LocalDateTime.now().minusDays(7))
                .plan(null)
                .paymentMethod(null)
                .build();
    }

    @Test
    @DisplayName("success() 정적 팩토리 메서드는 SUCCESS 상태로 PaymentHistory를 생성한다")
    void createSuccessHistory() {
        PaymentHistory history = PaymentHistory.success(
                dummySub,
                "pay-key",
                "order-123",
                1000L,
                "테스트 결제",
                "{\"response\": \"ok\"}",
                0
        );

        assertThat(history.getPaymentKey()).isEqualTo("pay-key");
        assertThat(history.getOrderId()).isEqualTo("order-123");
        assertThat(history.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(history.getRetryCount()).isEqualTo(0);
        assertThat(history.getPgResponseRaw()).contains("response");
    }

    @Test
    @DisplayName("failed() 정적 팩토리 메서드는 FAILED 상태로 PaymentHistory를 생성한다")
    void createFailedHistory() {
        PaymentHistory history = PaymentHistory.failed(
                dummySub,
                "fail-key",
                "order-999",
                1500L,
                "카드 오류",
                "{\"error\": \"limit\"}",
                1
        );

        assertThat(history.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(history.getRetryCount()).isEqualTo(1);
        assertThat(history.getDescription()).isEqualTo("카드 오류");
    }

    @Test
    @DisplayName("increaseRetry() 호출 시 retryCount가 1 증가한다")
    void increaseRetryCount() {
        PaymentHistory history = PaymentHistory.success(
                dummySub,
                "pay-key",
                "order-000",
                500L,
                null,
                null,
                2
        );

        history.increaseRetry();
        assertThat(history.getRetryCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("of(record) 메서드는 Request 레코드로부터 PaymentHistory를 올바르게 생성한다")
    void createFromRecord() {
        PaymentHistory.Request req = new PaymentHistory.Request(
                "pay-key-123",
                "order-456",
                2000L,
                "구독 결제",
                "{\"result\": \"ok\"}"
        );

        PaymentHistory history = PaymentHistory.of(dummySub, req, PaymentStatus.SUCCESS, 0);

        assertThat(history.getPaymentKey()).isEqualTo("pay-key-123");
        assertThat(history.getAmount()).isEqualTo(2000L);
        assertThat(history.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(history.getRetryCount()).isZero();
        assertThat(history.getDescription()).isEqualTo("구독 결제");
    }
}
