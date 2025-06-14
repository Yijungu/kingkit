package com.kingkit.billing_service.domain.payment.repository;

import com.kingkit.billing_service.domain.payment.PaymentFailure;
import com.kingkit.billing_service.domain.subscription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentFailureRepository extends JpaRepository<PaymentFailure, Long> {

    // ✅ 구독 ID 기준으로 아직 해결되지 않은 실패 이력들
    List<PaymentFailure> findBySubscriptionIdAndResolvedFalse(Long subscriptionId);

    // ✅ 가장 최근 실패 이력 1건
    Optional<PaymentFailure> findTopBySubscriptionOrderByFailedAtDesc(Subscription subscription);

    // ✅ 자동 재시도용: 아직 해결되지 않았고, 재시도 예약 시간이 현재 이전인 것들
    List<PaymentFailure> findByResolvedFalseAndRetryScheduledAtBefore(java.time.LocalDateTime now);
}
