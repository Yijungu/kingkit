package com.kingkit.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
    "com.kingkit.auth_service",
    "com.kingkit.lib_security" // ⬅️ 추가
})
@ConfigurationPropertiesScan
@EnableFeignClients(basePackages = "com.kingkit.auth_service.feign")
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
