package com.kingkit.billing_service.scheduler;

import com.kingkit.billing_service.application.usecase.BillingTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 실제 결제 트리거 작업을 수행하는 Job 클래스.
 * Scheduler 또는 다른 프로세스에서 이 클래스를 호출하여 실행할 수 있습니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledBillingTriggerJob {

    private final BillingTriggerService billingTriggerService;

    /**
     * 당일 대상 구독에 대해 결제 트리거를 수행합니다.
     * 내부적으로 BillingTriggerService.trigger() 호출.
     *
     * @param targetDate 결제를 수행할 대상 날짜
     */
    public void run(LocalDate targetDate) {
        log.info("[BillingJob] 결제 트리거 실행 시작 - targetDate={}", targetDate);

        try {
            billingTriggerService.trigger(targetDate, null);
            log.info("[BillingJob] 결제 트리거 실행 성공 - targetDate={}", targetDate);
        } catch (Exception e) {
            log.error("[BillingJob] 결제 트리거 실행 실패 - targetDate={}, message={}", targetDate, e.getMessage(), e);
        }
    }
}
