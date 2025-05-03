package com.kingkit.auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.kingkit.auth_service.feign.UserClient;

@SpringBootTest
class AuthServiceApplicationTests {

	@MockBean
    private UserClient userClient;


	@Test
	void contextLoads() {
	}

}
