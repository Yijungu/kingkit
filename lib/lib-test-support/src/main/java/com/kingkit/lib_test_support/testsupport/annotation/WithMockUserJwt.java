package com.kingkit.lib_test_support.testsupport.annotation;

import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@WithSecurityContext(factory = WithMockUserJwtSecurityContextFactory.class)
public @interface WithMockUserJwt {
    String email() default "test@example.com";
    String role() default "ROLE_USER";
}
