package com.kingkit.billing_service.domain.subscription.repository;

import com.kingkit.billing_service.domain.payment.PaymentMethod;
import com.kingkit.billing_service.domain.subscription.Subscription;
import com.kingkit.billing_service.domain.subscription.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);
    List<Subscription> findAllByUserIdInAndNextBillingAtAndStatus(List<Long> userIds, LocalDateTime billingAt, SubscriptionStatus status);
    List<Subscription> findAllByNextBillingAtAndStatus(LocalDateTime billingAt, SubscriptionStatus status);
    List<Subscription> findAllByNextBillingAt(LocalDate targetDate);
    List<Subscription> findByUserIdInAndNextBillingAt(List<Long> userIds, LocalDate nextBillingDate);
    List<Subscription> findByUserIdInAndNextBillingAtBetween(
            Collection<Long> userIds,
            LocalDateTime start,
            LocalDateTime end);

    List<Subscription> findByNextBillingAtBetween(
            LocalDateTime start,
            LocalDateTime end);
    Optional<Subscription> findByUserIdAndPaymentMethod(Long userId, PaymentMethod method);

}
