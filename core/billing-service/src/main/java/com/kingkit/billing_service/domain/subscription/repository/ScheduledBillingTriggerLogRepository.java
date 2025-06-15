package com.kingkit.billing_service.domain.subscription.repository;

import com.kingkit.billing_service.domain.subscription.ScheduledBillingTriggerLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledBillingTriggerLogRepository extends JpaRepository<ScheduledBillingTriggerLog, Long> {
}
