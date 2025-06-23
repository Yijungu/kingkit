package com.kingkit.billing_service.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kingkit.billing_service.support.fixture.domain.PaymentMethodFixture;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMethodTest {

    @Test
    @DisplayName("비활성화 시 isActive는 false로 바뀐다")
    void deactivate_setsInactive() {
        // given
        PaymentMethod method = PaymentMethodFixture.active(1L);

        // when
        method.deactivate();

        // then
        assertThat(method.isActive()).isFalse();
        assertThat(method.isActivated()).isFalse();
    }

    @Test
    @DisplayName("활성화 시 isActive는 true로 바뀐다")
    void activate_setsActive() {
        // given
        PaymentMethod method = PaymentMethodFixture.inactive(1L);

        // when
        method.activate();

        // then
        assertThat(method.isActive()).isTrue();
        assertThat(method.isActivated()).isTrue();
    }

    @Test
    @DisplayName("isActivated()는 isActive 값을 반환한다")
    void isActivated_worksCorrectly() {
        // given
        PaymentMethod method = PaymentMethodFixture.active(1L);

        // expect
        assertThat(method.isActivated()).isTrue();

        // when
        method.deactivate();

        // then
        assertThat(method.isActivated()).isFalse();
    }

    @Test
    @DisplayName("create() 정적 팩토리는 필드를 채우고 활성화된 PaymentMethod를 반환한다")
    void create_staticFactory_returnsActiveMethod() {
        PaymentMethod method = PaymentMethod.create(10L, "key-123", "Visa", "****-****-****-1111");

        assertThat(method.getUserId()).isEqualTo(10L);
        assertThat(method.getBillingKey()).isEqualTo("key-123");
        assertThat(method.getCardCompany()).isEqualTo("Visa");
        assertThat(method.getCardNumberMasked()).isEqualTo("****-****-****-1111");
        assertThat(method.isActive()).isTrue();
        assertThat(method.getRegisteredAt()).isNotNull();
    }
}
