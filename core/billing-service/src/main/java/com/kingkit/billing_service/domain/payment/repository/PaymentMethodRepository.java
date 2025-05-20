package com.kingkit.billing_service.domain.payment.repository;

import com.kingkit.billing_service.domain.payment.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByUserIdAndBillingKey(Long userId, String billingKey);
}
