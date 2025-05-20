package com.kingkit.billing_service.integration.common;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

/**
 * ✅ 모든 통합 테스트의 공통 설정 클래스
 * - @SpringBootTest: 전체 스프링 컨텍스트 로딩
 * - @AutoConfigureMockMvc: MockMvc 자동 주입
 * - @Transactional: 테스트 간 격리
 * - @ActiveProfiles("test"): 테스트 전용 설정 사용
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.yml")
@Transactional
public abstract class IntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @BeforeEach
    void setUpCommon() {
        // 필요 시 공통 DB 초기화 코드 삽입 가능
    }
}