package com.kingkit.billing_service.client;

import org.springframework.http.HttpStatus;

import com.kingkit.billing_service.client.dto.PaymentCommand;
import com.kingkit.billing_service.client.dto.PaymentResult;

/**
 * 결제 서비스 클라이언트 공통 인터페이스
 * - Toss 외 다른 PG사 도입 시 확장 가능
 * - 테스트 환경에서도 Stub 으로 교체 가능
 */
public interface PaymentClient {

    /**
     * 실제 결제 실행
     * @param command 결제 요청 정보
     * @return 결제 결과 (성공: paymentKey 포함 / 실패: 에러 정보 포함)
     */
    PaymentResult execute(PaymentCommand command);

    /**
     * 결제 페이지(Checkout) URL 요청
     * @param success 결제 성공 후 리다이렉션 주소
     * @param fail 결제 실패 시 리다이렉션 주소
     * @param customerKey 고객 고유키
     * @param orderId 주문 번호
     * @param amount 결제 금액
     * @return 사용자 결제 페이지 URL
     */
    String requestCheckoutUrl(String success, String fail, String customerKey, String orderId, long amount);

    /**
     * 빌링 키 삭제 (결제 해지 등)
     * @param billingKey 삭제할 빌링 키
     */
    void deleteBillingKey(String billingKey);

    HttpStatus testConnection(String billingKey);


}
