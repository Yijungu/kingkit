package com.kingkit.billing_service.infrastructure.cache;

import com.kingkit.billing_service.application.port.out.RedisPlanCachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * ✅ Redis 기반 주문-요금제 캐시 어댑터
 * - 주문 생성 시 orderId ➜ planCode 를 TTL과 함께 Redis에 저장
 * - Webhook 등에서 해당 planCode를 조회해 구독 생성에 사용
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisPlanCacheAdapter implements RedisPlanCachePort {

    private static final String KEY_PREFIX = "billing:order:";

    private final StringRedisTemplate redis;

    /**
     * TTL 설정 (초 단위)
     * - 기본: 5분 (300초)
     * - yml 설정: app.redis.order-id-ttl
     */
    @Value("${app.redis.order-id-ttl:300}")
    private long ttlSeconds;

    /**
     * ⏳ Redis에 planCode 저장
     */
    @Override
    public void store(String orderId, String planCode) {
        String key = buildKey(orderId);
        try {
            redis.opsForValue().set(key, planCode, Duration.ofSeconds(ttlSeconds));
            log.debug("✅ Redis 저장 완료. key={}, planCode={}, ttl={}s", key, planCode, ttlSeconds);
        } catch (Exception e) {
            log.error("🚨 Redis 저장 실패. orderId={}, planCode={}", orderId, planCode, e);
            throw new IllegalStateException("Redis 저장 실패", e);
        }
    }

    /**
     * 🔍 Redis에서 planCode 조회
     */
    @Override
    public String findPlanId(String orderId) {
        String key = buildKey(orderId);
        try {
            String planCode = redis.opsForValue().get(key);
            log.debug("🔍 Redis 조회. key={}, planCode={}", key, planCode);
            return planCode;
        } catch (Exception e) {
            log.error("🚨 Redis 조회 실패. orderId={}", orderId, e);
            return null;
        }
    }

    /**
     * 🧹 Redis에서 키 삭제
     */
    @Override
    public void evict(String orderId) {
        String key = buildKey(orderId);
        try {
            redis.delete(key);
            log.debug("🧹 Redis 삭제 완료. key={}", key);
        } catch (Exception e) {
            log.warn("⚠️ Redis 삭제 실패. key={}", key, e);
        }
    }

    private String buildKey(String orderId) {
        return KEY_PREFIX + orderId;
    }
}
