package com.kingkit.billing_service.application.usecase;


import com.kingkit.billing_service.client.toss.TossPaymentQueryClient;
import com.kingkit.billing_service.domain.payment.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentQueryService {

    private final TossPaymentQueryClient client;

    /**
     * Toss API로부터 결제 상태를 조회하고 내부 상태로 변환하여 반환
     * @param paymentKey PG에서 전달된 paymentKey
     * @return 내부 도메인의 결제 상태 enum
     */
    public PaymentStatus getStatus(String paymentKey) {
        try {
            Map<String, Object> data = client.getPaymentDetail(paymentKey);
            String status = (String) data.get("status");

            return switch (status) {
                case "DONE"   -> PaymentStatus.SUCCESS;
                case "FAILED" -> PaymentStatus.FAILED;
                default       -> {
                    log.warn("❓ 알 수 없는 Toss 결제 상태: {}", status);
                    yield PaymentStatus.UNKNOWN;
                }
            };

        } catch (Exception e) {
            log.error("🔥 결제 상태 조회 실패 - paymentKey={}", paymentKey, e);
            return PaymentStatus.UNKNOWN;
        }
    }

    public HttpStatus testConnection() {
        return client.testConnection();
    }
}
