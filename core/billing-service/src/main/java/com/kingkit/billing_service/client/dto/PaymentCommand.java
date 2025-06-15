package com.kingkit.billing_service.client.dto;

import com.kingkit.billing_service.domain.subscription.Subscription;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 결제 관련 요청을 전달하기 위한 명령 객체.
 */
@Getter
@Builder
@EqualsAndHashCode   
public class PaymentCommand {

    public enum Type {
        BILLING, DELETE_KEY, CHECKOUT_URL, HEALTH_CHECK
    }

    private final Type type;

    // 공통 필드
    private final String billingKey;
    private final String orderId;
    private final Long amount;

    // Checkout URL용
    private final String customerKey;
    private final String successUrl;
    private final String failUrl;

    public static PaymentCommand billing(String billingKey, String orderId, long amount) {
        return PaymentCommand.builder()
                .type(Type.BILLING)
                .billingKey(billingKey)
                .orderId(orderId)
                .amount(amount)
                .build();
    }

    public static PaymentCommand deleteKey(String billingKey) {
        return PaymentCommand.builder()
                .type(Type.DELETE_KEY)
                .billingKey(billingKey)
                .build();
    }

    public static PaymentCommand checkoutUrl(String customerKey, String orderId, long amount, String successUrl, String failUrl) {
        return PaymentCommand.builder()
                .type(Type.CHECKOUT_URL)
                .customerKey(customerKey)
                .orderId(orderId)
                .amount(amount)
                .successUrl(successUrl)
                .failUrl(failUrl)
                .build();
    }

    public static PaymentCommand healthCheck() {
        return PaymentCommand.builder()
                .type(Type.HEALTH_CHECK)
                .build();
    }

    public static PaymentCommand from(Subscription subscription) {
        String billingKey = subscription.getPaymentMethod().getBillingKey();
        String orderId = subscription.generateNextOrderId();
        long amount = subscription.getAmountToBill();

        return billing(billingKey, orderId, amount);
    }

}
