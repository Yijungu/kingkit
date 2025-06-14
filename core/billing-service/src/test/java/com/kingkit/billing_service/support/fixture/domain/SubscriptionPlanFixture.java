package com.kingkit.billing_service.support.fixture.domain;

import com.kingkit.billing_service.domain.subscription.SubscriptionPlan;

/**
 * SubscriptionPlan 테스트용 fixture 모음
 */
public class SubscriptionPlanFixture {

    public static SubscriptionPlan basicPlan() {
        return SubscriptionPlan.builder()
                .planCode("basic-monthly")
                .name("Basic Monthly Plan")
                .price(9900L)
                .durationDays(30)
                .isActive(true)
                .build();
    }

    public static SubscriptionPlan yearlyPlan() {
        return SubscriptionPlan.builder()
                .planCode("premium-yearly")
                .name("Premium Yearly Plan")
                .price(109000L)
                .durationDays(365)
                .isActive(true)
                .build();
    }

    public static SubscriptionPlan inactivePlan() {
        return SubscriptionPlan.builder()
                .planCode("inactive-plan")
                .name("Deactivated Plan")
                .price(0L)
                .durationDays(30)
                .isActive(false)
                .build();
    }

    public static SubscriptionPlan withDuration(int durationDays) {
        return SubscriptionPlan.builder()
                .planCode("custom-" + durationDays + "d")
                .name("Custom Plan " + durationDays + " days")
                .price(1000L * durationDays)  // 예시로 하루 1000원
                .durationDays(durationDays)
                .isActive(true)
                .build();
    }

    public static SubscriptionPlan withPriceAndDuration(long price, int durationDays) {
        return SubscriptionPlan.builder()
                .planCode("custom-plan")
                .name("Custom Plan")
                .price(price)
                .durationDays(durationDays)
                .isActive(true)
                .build();
    }
}
