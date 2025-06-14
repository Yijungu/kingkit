package com.kingkit.billing_service.client.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * 결제 클라이언트 응답 결과를 담는 객체.
 */
@Getter
@Builder
public class PaymentResult {

    public enum Status {
        SUCCESS,
        FAILED
    }

    private final Status status;

    private final String paymentKey;     // 결제 성공 시 반환
    private final String checkoutUrl;    // checkout URL 요청 시 반환
    private final HttpStatus httpStatus; // Toss 응답 상태 코드
    private final String reason;         // 실패 사유
    private final boolean slow;          // Health Check 시 응답 지연 여부
    private final LocalDateTime paidAt;  // 결제 완료 일시


    public boolean isSuccess() {
        return status == Status.SUCCESS && paymentKey != null;
    }


    /**
     * 결제 성공 응답 (결제 키 + 결제 완료 시각 포함)
     */
    public static PaymentResult successWithPaymentKey(String paymentKey) {
        return PaymentResult.builder()
                .status(Status.SUCCESS)
                .paymentKey(paymentKey)
                .paidAt(LocalDateTime.now())
                .httpStatus(HttpStatus.OK)
                .build();
    }

    /**
     * Checkout URL 발급 성공 응답
     */
    public static PaymentResult successWithCheckoutUrl(String checkoutUrl) {
        return PaymentResult.builder()
                .status(Status.SUCCESS)
                .checkoutUrl(checkoutUrl)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    /**
     * 헬스 체크 응답
     */
    public static PaymentResult healthCheck(HttpStatus status, boolean slow) {
        return PaymentResult.builder()
                .status(Status.SUCCESS)
                .httpStatus(status)
                .slow(slow)
                .build();
    }

    /**
     * 결제 실패 응답
     */
    public static PaymentResult fail(HttpStatus status, String reason) {
        return PaymentResult.builder()
                .status(Status.FAILED)
                .httpStatus(status)
                .reason(reason)
                .build();
    }
}
