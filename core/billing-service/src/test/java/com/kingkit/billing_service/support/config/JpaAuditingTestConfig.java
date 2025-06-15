// src/test/java/com/kingkit/billing_service/support/config/JpaAuditingTestConfig.java
package com.kingkit.billing_service.support.config;

import java.util.Optional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@TestConfiguration
@EnableJpaAuditing
public class JpaAuditingTestConfig {

    /** 필요하면 현재 로그인 유저 id 반환 */
    @Bean
    public AuditorAware<Long> auditorAware() {
        return () -> Optional.empty();
    }
}
