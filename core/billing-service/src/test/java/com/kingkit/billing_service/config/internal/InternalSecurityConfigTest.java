package com.kingkit.billing_service.config.internal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class InternalSecurityConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(InternalSecurityConfig.class);

    @Test
    void beanNotCreatedWithoutProperty() {
        contextRunner.run(ctx -> assertThat(ctx.containsBean("internalApiFilterChain")).isFalse());
    }

    @Test
    void beanCreatedWhenPropertyPresent() {
        contextRunner
                .withPropertyValues("internal.api-keys[0]=key123")
                .run(ctx -> assertThat(ctx.containsBean("internalApiFilterChain")).isTrue());
    }
}
