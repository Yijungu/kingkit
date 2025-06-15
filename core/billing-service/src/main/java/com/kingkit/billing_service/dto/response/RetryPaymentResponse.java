package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 결제 실패 재시도 응답 DTO
 */
@Getter
@Builder
public class RetryPaymentResponse {
    private final String paymentKey;
    private final String orderId;
    private final PaymentStatus status;      // SUCCESS or FAILED
    private final LocalDateTime paidAt;      // 결제 승인 시각 (실패 시 null)

    // ✅ 성공 팩토리 메서드
    public static RetryPaymentResponse success(String paymentKey) {
        return RetryPaymentResponse.builder()
                .paymentKey(paymentKey)
                .orderId(null) // 필요 시 오버로딩으로 다른 버전도 만들 수 있음
                .status(PaymentStatus.SUCCESS)
                .paidAt(LocalDateTime.now())
                .build();
    }

        // ✅ 성공 팩토리 메서드
    public static RetryPaymentResponse success(String paymentKey, String orderId, LocalDateTime paidAt) {
        return RetryPaymentResponse.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .status(PaymentStatus.SUCCESS)
                .paidAt(paidAt)
                .build();
    }

    // ✅ 실패 팩토리 메서드
    public static RetryPaymentResponse failed(String orderId) {
        return RetryPaymentResponse.builder()
                .paymentKey(null)
                .orderId(orderId)
                .status(PaymentStatus.FAILED)
                .paidAt(null)
                .build();
    }

    // ✅ 테스트용 응답 (간단한 Mock 또는 예제 출력 시 사용)
    public static RetryPaymentResponse test(String paymentKey, String orderId, String status, String paidAtIso) {
        return RetryPaymentResponse.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .status(PaymentStatus.valueOf(status))
                .paidAt(paidAtIso != null ? LocalDateTime.parse(paidAtIso) : null)
                .build();
    }
}
