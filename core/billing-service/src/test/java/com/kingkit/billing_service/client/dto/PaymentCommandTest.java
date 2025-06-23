package com.kingkit.billing_service.client.dto;

import com.kingkit.billing_service.domain.payment.PaymentMethod;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.SubscriptionPlan;
import com.kingkit.billing_service.domain.subscription.SubscriptionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentCommandTest {

    @Test
    void billingFactorySetsFields() {
        PaymentCommand cmd = PaymentCommand.billing("bkey","order",1000L);
        assertThat(cmd.getType()).isEqualTo(PaymentCommand.Type.BILLING);
        assertThat(cmd.getBillingKey()).isEqualTo("bkey");
        assertThat(cmd.getOrderId()).isEqualTo("order");
        assertThat(cmd.getAmount()).isEqualTo(1000L);
    }

    @Test
    void deleteKeyFactory() {
        PaymentCommand cmd = PaymentCommand.deleteKey("bkey");
        assertThat(cmd.getType()).isEqualTo(PaymentCommand.Type.DELETE_KEY);
        assertThat(cmd.getBillingKey()).isEqualTo("bkey");
        assertThat(cmd.getOrderId()).isNull();
    }

    @Test
    void checkoutUrlFactory() {
        PaymentCommand cmd = PaymentCommand.checkoutUrl(
                "cust","order",500L,"ok","fail");
        assertThat(cmd.getType()).isEqualTo(PaymentCommand.Type.CHECKOUT_URL);
        assertThat(cmd.getCustomerKey()).isEqualTo("cust");
        assertThat(cmd.getAmount()).isEqualTo(500L);
        assertThat(cmd.getSuccessUrl()).isEqualTo("ok");
        assertThat(cmd.getFailUrl()).isEqualTo("fail");
    }

    @Test
    void healthCheckFactory() {
        PaymentCommand cmd = PaymentCommand.healthCheck();
        assertThat(cmd.getType()).isEqualTo(PaymentCommand.Type.HEALTH_CHECK);
    }

    @Test
    void createFromSubscription() {
        PaymentMethod method = PaymentMethod.create(1L,"bkey","cc","****-1234");
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(10L)
                .planCode("basic")
                .name("Basic")
                .price(3000L)
                .durationDays(30)
                .isActive(true)
                .build();
        Subscription sub = Subscription.builder()
                .id(20L)
                .userId(1L)
                .plan(plan)
                .paymentMethod(method)
                .startedAt(LocalDateTime.now())
                .nextBillingAt(LocalDateTime.now().plusDays(30))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        PaymentCommand cmd = PaymentCommand.from(sub);
        assertThat(cmd.getType()).isEqualTo(PaymentCommand.Type.BILLING);
        assertThat(cmd.getBillingKey()).isEqualTo("bkey");
        assertThat(cmd.getAmount()).isEqualTo(3000L);
        assertThat(cmd.getOrderId()).startsWith("order-");
    }
}
