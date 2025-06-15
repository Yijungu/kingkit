package com.kingkit.billing_service.exception.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ───── Billing
    BILLING_KEY_NOT_FOUND  (HttpStatus.BAD_REQUEST,          "B001", "BillingKey가 존재하지 않거나 비활성 상태입니다."),
    DUPLICATE_ORDER_ID     (HttpStatus.CONFLICT,             "B002", "이미 존재하는 주문 ID입니다."),
    TOSS_API_ERROR         (HttpStatus.BAD_GATEWAY,          "B003", "PG사 통신 오류"),
    
    // ErrorCode.java
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "존재하지 않는 요금제입니다."),
    
    // ───── 공통
    INTERNAL_SERVER_ERROR  (HttpStatus.INTERNAL_SERVER_ERROR,"C000", "예상치 못한 서버 오류"),

    
    PAYMENT_FAILURE_NOT_FOUND(HttpStatus.NOT_FOUND, "BILLING-008", "결제 실패 내역이 없습니다."),
    ALREADY_RESOLVED_FAILURE(HttpStatus.BAD_REQUEST, "BILLING-009", "이미 처리된 결제 실패입니다."),
    RETRY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "BILLING-010", "재시도 가능 횟수를 초과하였습니다."),

    SUBSCRIPTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "SUBSCRIPTION_400_001", "해지 가능한 구독이 없습니다.");

    private final HttpStatus status;
    private final String     code;      // 프론트/클라이언트용 코드
    private final String     message;   // 기본 메시지
}
