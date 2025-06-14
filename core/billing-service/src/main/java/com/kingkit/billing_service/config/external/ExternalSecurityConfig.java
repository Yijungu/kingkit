package com.kingkit.billing_service.config.external;

import com.kingkit.lib_security.jwt.JwtAuthenticationEntryPoint;
import com.kingkit.lib_security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

@Configuration
@RequiredArgsConstructor
public class ExternalSecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final AntPathRequestMatcher BILLING_PATH = new AntPathRequestMatcher("/billing/**");
    private static final AntPathRequestMatcher SUBSCRIPTION_PATH = new AntPathRequestMatcher("/subscription");
    private static final OrRequestMatcher EXTERNAL_PATHS = new OrRequestMatcher(BILLING_PATH, SUBSCRIPTION_PATH);

    @Bean
    @Order(3)
    public SecurityFilterChain externalFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(EXTERNAL_PATHS)

            // 1. 보안 설정
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // 2. 요청 권한
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/users",
                    "/users/email",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // 3. JWT 필터, 예외 처리
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
