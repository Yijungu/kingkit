package com.kingkit.billing_service.application.port.out;

// application.port.out.RedisPlanCachePort
public interface RedisPlanCachePort {
    void store(String orderId, String planCode);
    String findPlanId(String orderId);
    void evict(String orderId); // ✅ delete → evict 로 통일
}
