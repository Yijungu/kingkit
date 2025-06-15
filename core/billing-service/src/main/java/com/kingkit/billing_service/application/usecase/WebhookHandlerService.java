package com.kingkit.billing_service.application.usecase;

import com.kingkit.billing_service.dto.request.TossWebhookRequest;
import com.kingkit.billing_service.domain.payment.PaymentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookHandlerService {

    private final TossPaymentQueryService paymentQueryService;
    private final WebhookService webhookService;

    public void handle(TossWebhookRequest request) {
        String eventType = request.eventType();
        
        switch (eventType) {
            case "SUBSCRIPTION_REGISTERED" -> {
                log.info("📥 SUBSCRIPTION_REGISTERED 이벤트 수신");
                // 별도 재검증 없음
                webhookService.handleSubscriptionRegistered(request);
            }

            case "SUBSCRIPTION_PAYMENT_SUCCESS" -> {
                log.info("📥 SUBSCRIPTION_PAYMENT_SUCCESS 이벤트 수신");
                if (verifyPaymentStatus(request, PaymentStatus.SUCCESS)) {
                    webhookService.handlePaymentSuccess(request);
                } else {
                    log.warn("❌ 결제 성공 검증 실패 - orderId={}, paymentKey={}", request.orderId(), request.billingKey());
                }
            }

            case "SUBSCRIPTION_PAYMENT_FAILED" -> {
                log.info("📥 SUBSCRIPTION_PAYMENT_FAILED 이벤트 수신");
                if (verifyPaymentStatus(request, PaymentStatus.FAILED)) {
                    webhookService.handlePaymentFailed(request);
                } else {
                    log.warn("❌ 결제 실패 검증 실패 - orderId={}, paymentKey={}", request.orderId(), request.billingKey());
                }
            }

            default -> log.warn("❓ 알 수 없는 이벤트 수신: {}", eventType);
        }
    }

    private boolean verifyPaymentStatus(TossWebhookRequest request, PaymentStatus expectedStatus) {
        try {
            PaymentStatus actualStatus = paymentQueryService.getStatus(request.billingKey());
            return expectedStatus.equals(actualStatus);
        } catch (Exception e) {
            log.error("🔥 결제 상태 재검증 실패 - {}", e.getMessage(), e);
            return false;
        }
    }
}
