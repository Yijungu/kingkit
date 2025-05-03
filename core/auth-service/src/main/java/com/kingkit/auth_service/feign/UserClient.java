package com.kingkit.auth_service.feign;

import com.kingkit.auth_service.feign.dto.UserDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${user-service.url}")  // yml로 주소 설정
public interface UserClient {

    @GetMapping("/users/internal/email")
    UserDto getUserByEmail(@RequestParam("email") String email);
}
