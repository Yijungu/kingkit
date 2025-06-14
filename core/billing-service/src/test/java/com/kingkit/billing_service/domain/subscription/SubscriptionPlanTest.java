package com.kingkit.billing_service.domain.subscription;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kingkit.billing_service.support.fixture.domain.SubscriptionPlanFixture;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionPlanTest {

    @Test
    @DisplayName("activate() 호출 시 isActive는 true가 된다")
    void activate_setsIsActiveTrue() {
        SubscriptionPlan plan = SubscriptionPlanFixture.inactivePlan();

        plan.activate();

        assertThat(plan.isActive()).isTrue();
    }

    @Test
    @DisplayName("deactivate() 호출 시 isActive는 false가 된다")
    void deactivate_setsIsActiveFalse() {
        SubscriptionPlan plan = SubscriptionPlanFixture.inactivePlan();

        plan.deactivate();

        assertThat(plan.isActive()).isFalse();
    }

    @Test
    @DisplayName("withPriceAndDuration() fixture는 price와 기간이 정확히 반영된다")
    void withPriceAndDuration_shouldSetCorrectFields() {
        SubscriptionPlan plan = SubscriptionPlanFixture.withPriceAndDuration(4900L, 7);

        assertThat(plan.getPrice()).isEqualTo(4900L);
        assertThat(plan.getDurationDays()).isEqualTo(7);
        assertThat(plan.isActive()).isTrue();
    }
}
