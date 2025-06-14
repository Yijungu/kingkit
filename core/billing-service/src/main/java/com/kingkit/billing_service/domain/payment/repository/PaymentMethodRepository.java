package com.kingkit.billing_service.domain.payment.repository;

import com.kingkit.billing_service.domain.payment.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    // ✅ 사용자 + billingKey로 결제 수단 조회
    Optional<PaymentMethod> findByUserIdAndBillingKey(Long userId, String billingKey);

    // ✅ 해당 사용자 모든 결제 수단 조회
    List<PaymentMethod> findAllByUserId(Long userId);

    // ✅ 활성 상태의 결제 수단 조회
    Optional<PaymentMethod> findByUserIdAndIsActiveTrue(Long userId);

    // ✅ billingKey 기준 단일 결제 수단 조회 (Webhook에서 유용)
    Optional<PaymentMethod> findByBillingKey(String billingKey);

    // ✅ 사용자의 모든 결제 수단 비활성화 (JPQL 일괄 처리)
    @Modifying(clearAutomatically = true)
    @Query("UPDATE PaymentMethod pm SET pm.isActive = false WHERE pm.userId = :userId")
    void deactivateAllByUserId(@Param("userId") Long userId);
}
