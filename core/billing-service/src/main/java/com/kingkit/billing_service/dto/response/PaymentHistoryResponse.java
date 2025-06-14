package com.kingkit.billing_service.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.kingkit.billing_service.domain.payment.PaymentHistory;
import com.kingkit.billing_service.domain.payment.PaymentStatus;

public record PaymentHistoryResponse(
        String orderId,
        String paymentKey,
        Long amount,
        String description,
        PaymentStatus status,
        LocalDateTime paidAt
) {
    public static PaymentHistoryResponse from(PaymentHistory history) {
        return new PaymentHistoryResponse(
                history.getOrderId(),
                history.getPaymentKey(),
                history.getAmount(),
                history.getDescription(),
                history.getStatus(),
                history.getPaidAt()
        );
    }

    /**
     * ✅ 테스트/예시용: 문자열 기반 팩토리
     */
    public static PaymentHistoryResponse test(
            String orderId,
            String paidAtIso,
            Long amount,
            String statusStr,
            String description
    ) {
        return new PaymentHistoryResponse(
                orderId,
                "pay-" + orderId,
                amount,
                description,
                PaymentStatus.valueOf(statusStr),
                LocalDateTime.parse(paidAtIso, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}
