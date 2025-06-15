package com.kingkit.billing_service.support.fixture.domain;

import com.kingkit.billing_service.domain.payment.PaymentMethod;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.SubscriptionPlan;
import com.kingkit.billing_service.domain.subscription.SubscriptionStatus;

import java.time.LocalDateTime;

public class SubscriptionFixture {

    public static Subscription active(Long userId, SubscriptionPlan plan, PaymentMethod method) {
        return Subscription.builder()
                .userId(userId)
                .plan(plan)
                .paymentMethod(method)
                .status(SubscriptionStatus.ACTIVE)
                .startedAt(LocalDateTime.now().minusDays(10))
                .nextBillingAt(LocalDateTime.now().plusDays(1))
                .build();
    }

    public static Subscription withStatus(SubscriptionStatus status) {
        return Subscription.builder()
                .userId(100L)
                .plan(SubscriptionPlanFixture.basicPlan())
                .paymentMethod(PaymentMethodFixture.active(100L))
                .status(status)
                .startedAt(LocalDateTime.now())
                .nextBillingAt(LocalDateTime.now().plusDays(5))
                .build();
    }

    public static Subscription withNextBillingAt(LocalDateTime nextBillingAt, int durationDays) {
        SubscriptionPlan plan = SubscriptionPlanFixture.withDuration(durationDays);
        return Subscription.builder()
                .userId(100L)
                .plan(plan)
                .paymentMethod(PaymentMethodFixture.active(100L))
                .status(SubscriptionStatus.ACTIVE)
                .startedAt(LocalDateTime.now())
                .nextBillingAt(nextBillingAt)
                .build();
    }
}
