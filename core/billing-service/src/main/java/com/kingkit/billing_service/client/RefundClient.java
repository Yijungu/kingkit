package com.kingkit.billing_service.client;

import org.springframework.http.HttpStatus;

/**
 * 결제 환불 클라이언트 공통 인터페이스
 * - Toss 등 PG사 환불 API 연동 시 구현
 * - 테스트 환경에서 Stub 대체 가능
 */
public interface RefundClient {

    /**
     * 단건 결제 환불 요청
     *
     * @param paymentKey PG사 결제 키
     * @param cancelReason 사용자 또는 시스템 환불 사유
     * @param cancelAmount 환불할 금액 (전체 환불: 전체 금액)
     * @return true: 성공 / false: 실패 또는 예외
     */
    boolean requestRefund(String paymentKey, String cancelReason, long cancelAmount);

    /**
     * PG API 연결 테스트
     * - PG 설정 오류 점검용
     *
     * @return HTTP 상태 코드
     */
    HttpStatus testConnection();
}
