package com.kingkit.billing_service.logging;

import com.kingkit.billing_service.domain.subscription.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Billing Trigger 실패 기록 유틸리티
 */
@Slf4j
@Component
public class TriggerLogger {

    public void logFailure(Subscription subscription, String reason) {
        log.warn("[BillingTrigger][FAILURE] subscriptionId={} userId={} reason={}",
                subscription.getId(), subscription.getUserId(), reason);
    }
}
