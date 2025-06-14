package com.kingkit.billing_service.config.internal;

import com.kingkit.lib_security.apikey.ApiKeyProperties;
import com.kingkit.lib_security.apikey.InternalApiKeyFilter;
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
@RequiredArgsConstructor
@EnableConfigurationProperties(ApiKeyProperties.class)
public class InternalSecurityConfig {

    private final ApiKeyProperties props;

    @Bean
    @Order(2) // ✅ 우선순위 명시
    @ConditionalOnProperty(name = "internal.api-keys[0]")
    public SecurityFilterChain internalApiFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/internal/**")  // 📌 유연한 매칭
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .addFilterBefore(new InternalApiKeyFilter(props),
                             UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    
}
