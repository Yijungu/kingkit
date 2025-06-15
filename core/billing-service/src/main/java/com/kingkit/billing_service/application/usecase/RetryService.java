package com.kingkit.billing_service.application.usecase;

import com.kingkit.billing_service.client.PaymentClient;
import com.kingkit.billing_service.client.dto.PaymentCommand;
import com.kingkit.billing_service.domain.payment.*;
import com.kingkit.billing_service.domain.payment.repository.PaymentFailureRepository;
import com.kingkit.billing_service.domain.payment.repository.PaymentHistoryRepository;
import com.kingkit.billing_service.domain.payment.repository.PaymentMethodRepository;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionRepository;
import com.kingkit.billing_service.dto.request.RetryPaymentRequest;
import com.kingkit.billing_service.dto.response.RetryPaymentResponse;
import com.kingkit.billing_service.exception.domain.billing.InvalidBillingKeyException;
import com.kingkit.billing_service.exception.domain.billing.PaymentFailureNotFoundException;
import com.kingkit.billing_service.exception.domain.billing.RetryLimitExceededException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetryService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentFailureRepository paymentFailureRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentClient paymentClient;

    private static final int RETRY_LIMIT = 3;

    @Transactional
    public RetryPaymentResponse retryFailedPayment(RetryPaymentRequest request, Long userId) {
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new IllegalArgumentException("구독을 찾을 수 없습니다. ID=" + request.getSubscriptionId()));
        
        PaymentFailure failure = paymentFailureRepository.findTopBySubscriptionOrderByFailedAtDesc(subscription)
                .orElseThrow(() -> new PaymentFailureNotFoundException("결제 실패 내역이 존재하지 않습니다."));

        if (failure.isResolved()) {
        throw new RetryLimitExceededException("이미 처리된 결제 실패입니다.");
        }
        if (failure.getRetryCount() >= RETRY_LIMIT) {
            log.warn("재시도 한도 초과. subscriptionId={}, retryCount={}", subscription.getId(), failure.getRetryCount());
            throw new RetryLimitExceededException("재시도 가능 횟수를 초과하였습니다"); // ✅ 수정
        }

        PaymentMethod method = paymentMethodRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new InvalidBillingKeyException("유효한 결제 수단이 없습니다."));

        String paymentKey = paymentClient.execute(
            PaymentCommand.billing(
                method.getBillingKey(),
                request.getOrderId(),
                request.getAmount()
            )
        ).getPaymentKey();

        PaymentStatus status = (paymentKey != null) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        log.info("재시도 시도. billingKey={}, paymentKey={}, status={}", method.getBillingKey(), paymentKey,status);

        paymentHistoryRepository.save(PaymentHistory.builder()
                .subscription(subscription)
                .paymentKey(paymentKey)
                .orderId(request.getOrderId())
                .paidAt(LocalDateTime.now())
                .status(status)
                .amount(request.getAmount())
                .description("재시도 결제 처리")
                .pgResponseRaw("{}")
                .retryCount(failure.getRetryCount() + 1)
                .build());

        log.info("재시도 완료. subscriptionId={}, retryCount={}", subscription.getUserId(), failure.getRetryCount());

        if (status == PaymentStatus.SUCCESS) {
            failure.markResolved();
            subscription.renewNextBilling();
            return RetryPaymentResponse.success(paymentKey);
        } else {
            failure.scheduleNextRetry();
            return RetryPaymentResponse.failed(request.getOrderId());
        }
    }

    @Transactional
    public RetryPaymentResponse retryAutomatically(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new IllegalArgumentException("구독을 찾을 수 없습니다. ID=" + subscriptionId));

        PaymentFailure failure = paymentFailureRepository.findTopBySubscriptionOrderByFailedAtDesc(subscription)
                .orElseThrow(() -> new IllegalArgumentException("결제 실패 내역이 존재하지 않습니다."));

        if (failure.isResolved() || failure.getRetryCount() >= RETRY_LIMIT ||
            failure.getRetryScheduledAt().isAfter(LocalDateTime.now())) {
            return RetryPaymentResponse.failed("auto-retry-skipped");
        }

        PaymentMethod method = paymentMethodRepository.findByUserIdAndIsActiveTrue(subscription.getUserId())
                .orElseThrow(() -> new InvalidBillingKeyException("유효한 결제 수단이 없습니다."));

        String orderId = "auto-retry-" + System.currentTimeMillis();

        String paymentKey = paymentClient.execute(
            PaymentCommand.billing(
                method.getBillingKey(),
                orderId,
                subscription.getPlan().getPrice()
            )
        ).getPaymentKey();

        PaymentStatus status = (paymentKey != null) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        paymentHistoryRepository.save(PaymentHistory.builder()
                .subscription(subscription)
                .paymentKey(paymentKey)
                .orderId(orderId)
                .paidAt(LocalDateTime.now())
                .status(status)
                .amount(subscription.getPlan().getPrice())
                .description("자동 재시도 결제")
                .pgResponseRaw("{}")
                .retryCount(failure.getRetryCount() + 1)
                .build());

        if (status == PaymentStatus.SUCCESS) {
            failure.markResolved();
            subscription.renewNextBilling();
            return RetryPaymentResponse.success(paymentKey);
        } else {
            failure.scheduleNextRetry();
            return RetryPaymentResponse.failed(orderId);
        }
    }
}
