package com.kingkit.billing_service.domain.payment.repository;

import com.kingkit.billing_service.domain.payment.PaymentFailure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentFailureRepository extends JpaRepository<PaymentFailure, Long> {
    List<PaymentFailure> findBySubscriptionIdAndResolvedFalse(Long subscriptionId);
}
