package com.kingkit.billing_service.scheduler;

import com.kingkit.billing_service.application.usecase.BillingTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingTriggerScheduler {

    private final BillingTriggerService billingTriggerService;

    /**
     * 매일 설정된 시간에 실행되어 당일 결제 예정 구독에 대해 결제 트리거를 수행합니다.
     * 실행 시점은 application.yml의 scheduler.billing.cron 값을 따릅니다.
     */
    @Scheduled(cron = "${scheduler.billing.cron:0 0 3 * * *}")
    public void triggerDaily() {
        LocalDate today = LocalDate.now();
        log.info("[BillingScheduler] 시작 - targetDate={}", today);

        try {
            billingTriggerService.trigger(today, null); // 전체 유저 대상으로 실행
            log.info("[BillingScheduler] 종료 - targetDate={}", today);
        } catch (Exception e) {
            log.error("[BillingScheduler] 예외 발생 - targetDate={}, message={}", today, e.getMessage(), e);
        }
    }
}
