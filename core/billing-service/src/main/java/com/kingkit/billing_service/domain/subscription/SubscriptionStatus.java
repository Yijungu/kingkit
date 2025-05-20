package com.kingkit.billing_service.domain.subscription;

public enum SubscriptionStatus {

    /**
     * 구독이 정상적으로 활성 상태
     */
    ACTIVE,

    /**
     * 사용자가 명시적으로 구독을 취소함 (billingKey 폐기 포함)
     */
    CANCELED,

    /**
     * 결제 실패 등으로 인해 구독이 만료됨 (billingKey는 살아있을 수 있음)
     */
    EXPIRED
}
