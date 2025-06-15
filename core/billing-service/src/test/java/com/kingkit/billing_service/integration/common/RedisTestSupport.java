package com.kingkit.billing_service.integration.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Redis 테스트 지원 유틸리티
 * Redis에 테스트 데이터를 넣거나, TTL 확인 및 삭제에 사용
 */
@Component
public class RedisTestSupport {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * Redis에 값을 직접 저장
     */
    public void save(String key, String value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * 값 조회
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 키 존재 여부
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 키 삭제
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * TTL (Time-To-Live) 조회
     */
    public Long getTtl(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * Redis 초기화 (전체 삭제)
     * 실제 테스트 DB일 때만 사용하는 것을 권장
     */
    public void flushAll() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }
}
