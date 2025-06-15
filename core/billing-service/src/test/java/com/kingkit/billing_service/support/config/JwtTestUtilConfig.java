package com.kingkit.billing_service.support.config;

import com.kingkit.lib_security.jwt.JwtTokenProvider;
import com.kingkit.lib_test_support.testsupport.util.JwtTestTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JwtTestUtilConfig {

    @Bean
    public JwtTestTokenProvider jwtTestTokenProvider(JwtTokenProvider jwtTokenProvider) {
        return new JwtTestTokenProvider(jwtTokenProvider);
    }
}
