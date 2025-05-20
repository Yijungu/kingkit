package com.kingkit.billing_service.domain.payment.repository;

import com.kingkit.billing_service.domain.payment.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    boolean existsByOrderId(String orderId);
    Optional<PaymentHistory> findByOrderId(String orderId);
}
