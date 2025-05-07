package com.kingkit.lib_security.apikey;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableConfigurationProperties(ApiKeyProperties.class)
@RequiredArgsConstructor
public class ApiKeyFilterAutoConfig {

    private final ApiKeyProperties props;

    /** internal.api-keys 가 설정돼 있을 때만 필터 활성화 */
    @Bean
    @Order(1)
    @ConditionalOnProperty(name = "internal.api-keys")
    public SecurityFilterChain internalApiFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/internal/users/**")              // 기본 패턴
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .addFilterBefore(new InternalApiKeyFilter(props),
                             UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
