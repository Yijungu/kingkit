package com.kingkit.lib_test_support.testsupport.annotation;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockUserJwtSecurityContextFactory implements WithSecurityContextFactory<WithMockUserJwt> {

    @Override
    public SecurityContext createSecurityContext(WithMockUserJwt annotation) {
        String email = annotation.email();
        String role = annotation.role();

        var auth = new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
