package com.kingkit.billing_service.api.webhook;

import com.kingkit.billing_service.application.usecase.TossPaymentQueryService;
import com.kingkit.billing_service.application.usecase.WebhookService;
import com.kingkit.billing_service.domain.payment.PaymentStatus;
import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossWebhookProcessor {

    private final TossPaymentQueryService queryService;
    private final WebhookService webhookService;

    public void process(TossWebhookRequest request) {
        String eventType = request.eventType();
        String paymentKey = request.billingKey();

        log.info("📬 Toss Webhook 수신 - eventType={}, orderId={}", eventType, request.orderId());

        switch (eventType) {

            case "SUBSCRIPTION_REGISTERED" -> {
                // ✅ 별도 검증 없이 처리
                webhookService.handleSubscriptionRegistered(request);
            }

            case "SUBSCRIPTION_PAYMENT_SUCCESS" -> {
                // ✅ Toss API로 재검증
                PaymentStatus verifiedStatus = queryService.getStatus(paymentKey);

                if (verifiedStatus == PaymentStatus.SUCCESS) {
                    webhookService.handlePaymentSuccess(request);
                } else {
                    log.warn("❌ Toss에 재검증 실패 - paymentKey={}, verifiedStatus={}", paymentKey, verifiedStatus);
                }
            }

            case "SUBSCRIPTION_PAYMENT_FAILED" -> {
                // ✅ Toss API로 재검증
                PaymentStatus verifiedStatus = queryService.getStatus(paymentKey);

                if (verifiedStatus == PaymentStatus.FAILED) {
                    webhookService.handlePaymentFailed(request);
                } else {
                    log.warn("❌ Toss에 재검증 실패 - paymentKey={}, verifiedStatus={}", paymentKey, verifiedStatus);
                }
            }

            default -> {
                log.warn("❓ 알 수 없는 이벤트 타입: {}", eventType);
            }
        }
    }
}
