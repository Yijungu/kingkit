package com.kingkit.billing_service.domain.payment.repository;

import com.kingkit.billing_service.domain.payment.PaymentHistory;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    boolean existsByOrderId(String orderId);
    Optional<PaymentHistory> findByOrderId(String orderId);
    Page<PaymentHistory> findBySubscription_UserId(Long userId, Pageable pageable);
}
