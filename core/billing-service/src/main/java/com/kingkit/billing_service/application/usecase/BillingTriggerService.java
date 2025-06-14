package com.kingkit.billing_service.application.usecase;

import com.kingkit.billing_service.domain.payment.PaymentFailure;
import com.kingkit.billing_service.domain.payment.PaymentHistory;
import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.domain.payment.repository.PaymentFailureRepository;
import com.kingkit.billing_service.domain.payment.repository.PaymentHistoryRepository;
import com.kingkit.billing_service.domain.payment.PaymentMethod;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.repository.SubscriptionRepository;
import com.kingkit.billing_service.dto.request.AdminBillingTriggerRequest;
import com.kingkit.billing_service.dto.response.AdminBillingTriggerResponse;
import com.kingkit.billing_service.dto.response.BillingTriggerDetail;
import com.kingkit.billing_service.client.PaymentClient;
import com.kingkit.billing_service.client.dto.PaymentCommand;
import com.kingkit.billing_service.client.dto.PaymentResult;
import com.kingkit.billing_service.logging.TriggerLogger;
import com.kingkit.billing_service.util.DateRange;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingTriggerService {

    private final SubscriptionRepository subscriptionRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentFailureRepository paymentFailureRepository;
    private final PaymentClient paymentClient;
    private final TriggerLogger triggerLogger;

    @Transactional
    public AdminBillingTriggerResponse triggerScheduledBilling(AdminBillingTriggerRequest request) {
        LocalDate targetDate = request.getTargetDate() != null ? request.getTargetDate() : LocalDate.now();
        List<Long> userIds = request.getUserIds();
        return trigger(targetDate, userIds);
    }

    @Transactional
    public AdminBillingTriggerResponse trigger(LocalDate targetDate, List<Long> userIds) {
        LocalDateTime from = DateRange.startOf(targetDate);
        LocalDateTime to = DateRange.endOf(targetDate);

        List<Subscription> subscriptions = (userIds != null && !userIds.isEmpty())
                ? subscriptionRepository.findByUserIdInAndNextBillingAtBetween(userIds, from, to)
                : subscriptionRepository.findByNextBillingAtBetween(from, to);

        List<BillingTriggerDetail> successList = new ArrayList<>();
        List<BillingTriggerDetail> failureList = new ArrayList<>();

        for (Subscription subscription : subscriptions) {
            try {
                triggerBilling(subscription, successList, failureList);
            } catch (Exception e) {
                log.warn("[BillingTrigger] 예외 처리되지 않은 구독 실패: subscriptionId={}, reason={}", subscription.getId(), e.getMessage(), e);
            }
        }

        return AdminBillingTriggerResponse.of(failureList, successList.size());
    }

    private void triggerBilling(
            Subscription subscription,
            List<BillingTriggerDetail> successList,
            List<BillingTriggerDetail> failureList
    ) {
        Long subscriptionId = subscription.getId();
        Long userId = subscription.getUserId();
        PaymentMethod method = subscription.getPaymentMethod();

        if (method == null || !method.isActive()) {
            log.warn("[BillingTrigger] billingKey 없음: userId={}, subscriptionId={}", userId, subscriptionId);
            handleFailure(subscription, "billingKey 없음", failureList);
            return;
        }

        PaymentCommand command = PaymentCommand.from(subscription);
        PaymentResult result;

        try {
            result = paymentClient.execute(command);
        } catch (Exception e) {
            String reason = e.getMessage() != null ? e.getMessage() : "결제 요청 실패";
            log.error("[BillingTrigger] 결제 예외 발생: userId={}, reason={}", userId, reason, e);
            handleFailure(subscription, reason, failureList);
            return;
        }

        boolean isSuccess = result.isSuccess();

        if (isSuccess) {
            handleSuccess(subscription, command, result, successList);
        } else {
            String reason = "결제 실패: status=" + result.getStatus();
            handleFailure(subscription, reason, failureList);
        }
    }

    private void handleSuccess(
            Subscription subscription,
            PaymentCommand command,
            PaymentResult result,
            List<BillingTriggerDetail> successList
    ) {
        subscription.markBillingSuccess(result.getPaidAt());
        subscriptionRepository.save(subscription);

        paymentHistoryRepository.save(PaymentHistory.builder()
                .subscription(subscription)
                .paymentKey(result.getPaymentKey())
                .orderId(command.getOrderId())
                .paidAt(result.getPaidAt())
                .status(PaymentStatus.SUCCESS)
                .amount(command.getAmount())
                .description("Scheduled Billing Success")
                .pgResponseRaw("raw-response")  // TODO: 실제 raw 필요 시 수정
                .retryCount(0)
                .build());

        log.info("[BillingTrigger] 결제 성공: userId={}, billingKey={}", subscription.getUserId(), subscription.getPaymentMethod().getBillingKey());

        successList.add(new BillingTriggerDetail(
                subscription.getId(),
                subscription.getUserId(),
                PaymentStatus.SUCCESS,
                null
        ));
    }

    private void handleFailure(
            Subscription subscription,
            String reason,
            List<BillingTriggerDetail> failureList
    ) {
        String paymentKey = "fail-" + subscription.getId() + "-" + UUID.randomUUID();

        paymentHistoryRepository.save(PaymentHistory.builder()
                .subscription(subscription)
                .paymentKey(paymentKey)
                .orderId("auto-" + UUID.randomUUID())
                .paidAt(LocalDateTime.now())
                .status(PaymentStatus.FAILED)
                .amount(subscription.getAmountToBill())
                .description("Scheduled Billing Failure")
                .pgResponseRaw("raw-response") // TODO: 실제 응답 저장 가능 시 적용
                .retryCount(0)
                .build());

        paymentFailureRepository.save(PaymentFailure.builder()
                .subscription(subscription)
                .failedAt(LocalDateTime.now())
                .reason(reason)
                .retryCount(0)
                .retryScheduledAt(LocalDateTime.now().plusHours(1))
                .resolved(false)
                .build());

        triggerLogger.logFailure(subscription, reason);

        failureList.add(new BillingTriggerDetail(
                subscription.getId(),
                subscription.getUserId(),
                PaymentStatus.FAILED,
                reason
        ));
    }
}
