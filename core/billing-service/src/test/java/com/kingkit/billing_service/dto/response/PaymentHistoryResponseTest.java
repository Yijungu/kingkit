package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentHistory;
import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.domain.subscription.Subscription;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentHistoryResponseTest {

    @Test
    void fromEntity() {
        PaymentHistory history = PaymentHistory.builder()
                .id(1L)
                .subscription(Subscription.builder().build())
                .paymentKey("pk")
                .orderId("order")
                .paidAt(LocalDateTime.now())
                .status(PaymentStatus.SUCCESS)
                .amount(1000L)
                .description("desc")
                .pgResponseRaw("{}")
                .retryCount(0)
                .build();
        PaymentHistoryResponse res = PaymentHistoryResponse.from(history);
        assertThat(res.orderId()).isEqualTo("order");
        assertThat(res.paymentKey()).isEqualTo("pk");
        assertThat(res.status()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void testFactory() {
        PaymentHistoryResponse res = PaymentHistoryResponse.test("o1","2024-01-01T00:00:00",500L,"SUCCESS","memo");
        assertThat(res.orderId()).isEqualTo("o1");
        assertThat(res.paymentKey()).isEqualTo("pay-o1");
        assertThat(res.status()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(res.description()).isEqualTo("memo");
        assertThat(res.paidAt()).isEqualTo(LocalDateTime.parse("2024-01-01T00:00:00"));
    }
}
