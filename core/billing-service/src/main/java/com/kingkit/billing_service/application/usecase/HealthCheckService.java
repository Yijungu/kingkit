package com.kingkit.billing_service.application.usecase;

import com.kingkit.billing_service.client.PaymentClient;
import com.kingkit.billing_service.dto.response.PgHealthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthCheckService {

    private final PaymentClient paymentClient;

    public PgHealthResponse checkPgHealth() {
        return checkPgHealth(null);
    }

    public PgHealthResponse checkPgHealth(String billingKey) {
        long startTime = System.currentTimeMillis();

        try {
            HttpStatus status = paymentClient.testConnection(billingKey); // billingKey 전달
            long elapsed = System.currentTimeMillis() - startTime;

            return PgHealthResponse.success(status.value(), elapsed);

        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;

            log.warn("❌ Toss PG 연동 실패: {}", e.getMessage());
            return PgHealthResponse.failure(HttpStatus.SERVICE_UNAVAILABLE.value(), "Toss 연동 실패: " + e.getMessage(), elapsed);
        }
    }
}
