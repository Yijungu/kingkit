package com.kingkit.billing_service.exception.core;

import com.kingkit.billing_service.exception.domain.billing.AlreadyResolvedFailureException;
import com.kingkit.billing_service.exception.domain.billing.DuplicateOrderIdException;
import com.kingkit.billing_service.exception.domain.billing.InvalidBillingKeyException;
import com.kingkit.billing_service.exception.domain.billing.PaymentFailureNotFoundException;
import com.kingkit.billing_service.exception.domain.billing.RetryLimitExceededException;
import com.kingkit.billing_service.exception.domain.billing.TossApiException;
import com.kingkit.billing_service.exception.domain.plan.PlanNotFoundException;

import java.time.OffsetDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /* ───────────── 비즈니스 예외 ───────────── */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex, HttpServletRequest req) {

        ErrorCode ec = ex.getErrorCode();
        log.warn("[Business] {} - {}", ec.getCode(), ex.getMessage());

        return build(ec, ex.getMessage(), req.getRequestURI());
    }

    /* ───────────── Toss 연동 실패 (502) ───────────── */
    @ExceptionHandler(TossApiException.class)
    public ResponseEntity<ErrorResponse> handleToss(
            TossApiException ex, HttpServletRequest req) {

        log.error("[TossAPI] {}", ex.getMessage(), ex);

        return build(ErrorCode.TOSS_API_ERROR,
                     ex.getMessage(), req.getRequestURI());
    }

    /* ───────────── orderId 중복 (409) ───────────── */
    @ExceptionHandler(DuplicateOrderIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateOrderId(
            DuplicateOrderIdException ex, HttpServletRequest req) {

        log.warn("[DuplicateOrderId] {}", ex.getMessage());

        return build(ErrorCode.DUPLICATE_ORDER_ID,
                     ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(PaymentFailureNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentFailureNotFound(
            PaymentFailureNotFoundException ex, HttpServletRequest req) {
        log.warn("[PaymentFailureNotFound] {}", ex.getMessage());
        return build(ErrorCode.PAYMENT_FAILURE_NOT_FOUND, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(AlreadyResolvedFailureException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyResolved(
            AlreadyResolvedFailureException ex, HttpServletRequest req) {
        log.warn("[AlreadyResolvedFailure] {}", ex.getMessage());
        return build(ErrorCode.ALREADY_RESOLVED_FAILURE, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(RetryLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRetryLimitExceeded(
            RetryLimitExceededException ex, HttpServletRequest req) {
        log.warn("[RetryLimitExceeded] {}", ex.getMessage());
        return build(ErrorCode.RETRY_LIMIT_EXCEEDED, ex.getMessage(), req.getRequestURI());
    }

    /* ───────────── 요금제 없음 (404) ───────────── */
    @ExceptionHandler(PlanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlanNotFound(
            PlanNotFoundException ex, HttpServletRequest req) {

        log.warn("[PlanNotFound] {}", ex.getMessage());

        return build(ErrorCode.PLAN_NOT_FOUND,
                     ex.getMessage(), req.getRequestURI());
    }

    /* ───────────── billingKey 오류 (400) ───────────── */
    @ExceptionHandler(InvalidBillingKeyException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBillingKey(
            InvalidBillingKeyException ex, HttpServletRequest req) {

        log.warn("[InvalidBillingKey] {}", ex.getMessage());

        return build(ErrorCode.BILLING_KEY_NOT_FOUND,
                     ex.getMessage(), req.getRequestURI());
    }

    /* ───────────── 모든 기타 예외 (500) ───────────── */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(
            Exception ex, HttpServletRequest req) {

        log.error("[Unknown] {} - {}", ex.getClass(), ex.getMessage(), ex);

        return build(ErrorCode.INTERNAL_SERVER_ERROR,
                     ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                     req.getRequestURI());
    }

    /* ───────────── 공통 Response 빌더 ───────────── */
    private ResponseEntity<ErrorResponse> build(
            ErrorCode ec, String msg, String path) {

        ErrorResponse body = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now().toString())
                .status(ec.getStatus().value())
                .error(ec.getStatus().getReasonPhrase())
                .code(ec.getCode())
                .message(msg)
                .path(path)
                .build();

        return ResponseEntity.status(ec.getStatus()).body(body);
    }    
}
