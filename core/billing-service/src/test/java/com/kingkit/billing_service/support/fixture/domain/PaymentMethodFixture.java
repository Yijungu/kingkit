package com.kingkit.billing_service.support.fixture.domain;

import java.time.LocalDateTime;

import com.kingkit.billing_service.domain.payment.PaymentMethod;

public class PaymentMethodFixture {

    public static PaymentMethod active(Long userId) {
        return PaymentMethod.builder()
                .userId(userId)
                .billingKey("billing-key-" + userId)
                .cardCompany("Toss")
                .cardNumberMasked("****-****-****-1234")
                .registeredAt(LocalDateTime.now())
                .isActive(true)
                .build();
    }

    public static PaymentMethod inactive(Long userId) {
        return PaymentMethod.builder()
                .userId(userId)
                .billingKey("billing-key-" + userId)
                .cardCompany("Kakao")
                .cardNumberMasked("****-****-****-5678")
                .registeredAt(LocalDateTime.now().minusDays(1))
                .isActive(false)
                .build();
    }

    public static PaymentMethod withBillingKey(String billingKey) {
        return PaymentMethod.builder()
                .userId(999L)
                .billingKey(billingKey)
                .cardCompany("Shinhan")
                .cardNumberMasked("****-****-****-9999")
                .registeredAt(LocalDateTime.now())
                .isActive(true)
                .build();
    }

    public static PaymentMethod activeMethod(Long userId, String billingKey) {
    return PaymentMethod.builder()
            .userId(userId)
            .billingKey(billingKey)
            .cardCompany("국민카드")
            .cardNumberMasked("****-****-****-1234")
            .registeredAt(LocalDateTime.now().minusDays(1))
            .isActive(true)
            .build();
}

}
