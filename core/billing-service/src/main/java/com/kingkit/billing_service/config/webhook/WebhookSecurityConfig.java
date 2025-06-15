package com.kingkit.billing_service.config.webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class WebhookSecurityConfig {

    private final WebhookSignatureFilter webhookSignatureFilter;

    private static final AntPathRequestMatcher WEBHOOK_PATH = new AntPathRequestMatcher("/webhook/**");

    @Bean
    @Order(1) // External 보다 우선순위 높게
    public SecurityFilterChain webhookFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(WEBHOOK_PATH)
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .sessionManagement(session -> session.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            // ✅ Toss Webhook 시그니처 필터 등록
            .addFilterBefore(webhookSignatureFilter, org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class);

        return http.build();
    }
}
