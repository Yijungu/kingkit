package com.kingkit.billing_service.integration.common;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;


import com.kingkit.billing_service.support.config.JpaAuditingTestConfig;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

/**
 * ✅ 모든 통합 테스트의 공통 설정 클래스
 * - @SpringBootTest: 전체 스프링 컨텍스트 로딩
 * - @AutoConfigureMockMvc: MockMvc 자동 주입
 * - @Transactional: 테스트 간 격리
 * - @ActiveProfiles("test"): 테스트 전용 설정 사용
 */

@AutoConfigureWireMock(port = 0)          
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.yml")
@Transactional
@Import({JpaAuditingTestConfig.class})
public abstract class IntegrationTestSupport {
    
    // ① Redis 컨테이너
    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
                    .withExposedPorts(6379)
                    .waitingFor(  // 포트만 열리는 것이 아니라,
                        Wait.forLogMessage(".*Ready to accept connections.*\\n", 1)  // 진짜 준비될 때까지
                    )
                    .withReuse(true);
    @BeforeEach
    void checkRedis() {
        System.out.println("🔍 Redis host: " + REDIS.getHost());
        System.out.println("🔍 Redis port: " + REDIS.getMappedPort(6379));
    }
    // ② host/port 동적 주입
    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);          // ← prefix 수정
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @BeforeEach
    void setUpCommon() {
        // 필요 시 공통 DB 초기화 코드 삽입 가능
    }
}