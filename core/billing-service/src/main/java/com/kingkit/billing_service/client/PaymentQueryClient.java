package com.kingkit.billing_service.client;

import org.springframework.http.HttpStatus;

import java.util.Map;

public interface PaymentQueryClient {

    /**
     * 결제 단건 조회
     * @param paymentKey 결제 키
     * @return 결제 응답 Map (status, method, amount 등 포함)
     */
    Map<String, Object> getPaymentDetail(String paymentKey);

    /**
     * 결제 성공 여부 확인
     * @param paymentKey 결제 키
     * @return true: 결제 성공(DONE), false: 실패/예외
     */
    boolean isPaymentSuccessful(String paymentKey);

    /**
     * 결제 상태 확인용 테스트 핑
     */
    HttpStatus testConnection();
}
