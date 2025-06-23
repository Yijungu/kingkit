package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentMethod;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.SubscriptionPlan;
import com.kingkit.billing_service.domain.subscription.SubscriptionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionStatusResponseDtoTest {

    @Test
    void fromActiveSubscription() {
        PaymentMethod method = PaymentMethod.create(1L,"bkey","cc","1234");
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .id(1L).planCode("code").name("plan")
                .price(1000L).durationDays(30).isActive(true).build();
        Subscription sub = Subscription.builder()
                .id(2L).userId(1L)
                .plan(plan)
                .paymentMethod(method)
                .startedAt(LocalDateTime.now())
                .nextBillingAt(LocalDateTime.now().plusDays(30))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        SubscriptionStatusResponseDto dto = SubscriptionStatusResponseDto.from(sub);
        assertThat(dto.isActive()).isTrue();
        assertThat(dto.getPlanName()).isEqualTo("plan");
        assertThat(dto.getCardInfo().getCardCompany()).isEqualTo("cc");
    }

    @Test
    void inactiveFactory() {
        SubscriptionStatusResponseDto dto = SubscriptionStatusResponseDto.inactive();
        assertThat(dto.isActive()).isFalse();
    }

    @Test
    void sampleFactory() {
        SubscriptionStatusResponseDto dto = SubscriptionStatusResponseDto.sample(true,"plan","cc","****");
        assertThat(dto.getPlanName()).isEqualTo("plan");
        assertThat(dto.getCardInfo().getCardCompany()).isEqualTo("cc");
    }
}
