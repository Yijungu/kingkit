package com.kingkit.billing_service.domain.subscription;

import com.kingkit.billing_service.domain.payment.PaymentMethod;
import com.kingkit.billing_service.support.fixture.domain.PaymentMethodFixture;
import com.kingkit.billing_service.support.fixture.domain.SubscriptionPlanFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubscriptionTest {

    private Subscription createActiveSubscription(LocalDateTime nextBillingAt, int durationDays) {
        PaymentMethod method = PaymentMethodFixture.active(100L);
        SubscriptionPlan plan = SubscriptionPlanFixture.withDuration(durationDays);
        return Subscription.builder()
                .userId(100L)
                .plan(plan)
                .paymentMethod(method)
                .status(SubscriptionStatus.ACTIVE)
                .startedAt(LocalDateTime.now().minusDays(1))
                .nextBillingAt(nextBillingAt)
                .build();
    }

    @Test
    @DisplayName("markCanceled() sets status to CANCELED")
    void markCanceled_setsStatus() {
        Subscription sub = createActiveSubscription(LocalDateTime.now(), 30);
        sub.markCanceled();
        assertThat(sub.getStatus()).isEqualTo(SubscriptionStatus.CANCELED);
    }

    @Test
    @DisplayName("markExpired() sets status to EXPIRED")
    void markExpired_setsStatus() {
        Subscription sub = createActiveSubscription(LocalDateTime.now(), 30);
        sub.markExpired();
        assertThat(sub.getStatus()).isEqualTo(SubscriptionStatus.EXPIRED);
    }

    @Test
    @DisplayName("renewNextBilling() adds plan duration")
    void renewNextBilling_addsDuration() {
        LocalDateTime next = LocalDateTime.now();
        Subscription sub = createActiveSubscription(next, 7);

        sub.renewNextBilling();

        assertThat(sub.getNextBillingAt()).isAfter(next.plusDays(6));
        assertThat(sub.getNextBillingAt()).isBefore(next.plusDays(8));
    }

    @Test
    @DisplayName("markBillingSuccess updates startedAt when null and renews next billing")
    void markBillingSuccess_updatesFields() {
        LocalDateTime next = LocalDateTime.now();
        PaymentMethod method = PaymentMethodFixture.active(100L);
        SubscriptionPlan plan = SubscriptionPlanFixture.withDuration(30);
        Subscription sub = Subscription.builder()
                .userId(100L)
                .plan(plan)
                .paymentMethod(method)
                .status(SubscriptionStatus.ACTIVE)
                .startedAt(null)
                .nextBillingAt(next)
                .build();

        LocalDateTime paidAt = LocalDateTime.now();
        sub.markBillingSuccess(paidAt);

        assertThat(sub.getStartedAt()).isEqualTo(paidAt);
        assertThat(sub.getNextBillingAt()).isAfter(next);
    }

    @Test
    @DisplayName("markBillingSuccess throws when subscription is not active")
    void markBillingSuccess_inactive_throws() {
        Subscription sub = Subscription.builder()
                .userId(1L)
                .plan(SubscriptionPlanFixture.basicPlan())
                .paymentMethod(PaymentMethodFixture.active(1L))
                .status(SubscriptionStatus.CANCELED)
                .startedAt(LocalDateTime.now())
                .nextBillingAt(LocalDateTime.now())
                .build();

        assertThatThrownBy(() -> sub.markBillingSuccess(LocalDateTime.now()))
                .isInstanceOf(IllegalStateException.class);
    }
}
