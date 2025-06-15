package com.kingkit.billing_service.domain.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {

    SUCCESS("SUCCESS"),         // Toss: 결제 성공
    FAILED("FAILED"),        // Toss: 결제 실패
    CANCELED("CANCELED"),    // Toss: 결제 취소 (실패의 일종으로 처리 가능)
    IN_PROGRESS("IN_PROGRESS"), // Toss: 결제 승인 중
    UNKNOWN("UNKNOWN");      // Toss 외 값 또는 에러

    private final String tossValue;

    PaymentStatus(String tossValue) {
        this.tossValue = tossValue;
    }

    /**
     * Toss의 응답 status를 내부 enum으로 변환
     */
    @JsonCreator
    public static PaymentStatus from(String tossValue) {
        for (PaymentStatus status : values()) {
            if (status.tossValue.equalsIgnoreCase(tossValue)) {
                return status;
            }
        }
        return UNKNOWN;
    }

    /**
     * API 응답 직렬화용
     */
    @JsonValue
    public String toValue() {
        return tossValue;
    }

    /**
     * 결제 성공 여부
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    /**
     * 결제 실패(취소 포함) 여부
     */
    public boolean isFailure() {
        return this == FAILED || this == CANCELED;
    }

    /**
     * 결제가 아직 완료되지 않은 상태인지 여부
     */
    public boolean isPending() {
        return this == IN_PROGRESS;
    }
}
