package com.kingkit.billing_service.domain.subscription.repository;

import com.kingkit.billing_service.domain.subscription.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByPlanCode(String planCode);
}
