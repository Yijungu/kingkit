package com.kingkit.gateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.config.import=optional:configserver:",
    "spring.cloud.config.enabled=false",
    "spring.cloud.compatibility-verifier.enabled=false"
})
@Disabled("Requires external services")
class GatewayServiceApplicationTests {

        @Test
        void contextLoads() {
        }

}
