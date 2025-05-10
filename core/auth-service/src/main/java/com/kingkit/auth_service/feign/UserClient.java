package com.kingkit.auth_service.feign;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kingkit.lib_dto.UserDto;

@FeignClient(name = "user-service", url = "${user-service.url}")  // yml로 주소 설정
public interface UserClient {

    @GetMapping("/internal/users/email")
    UserDto getUserByEmail(@RequestParam("email") String email);

    @GetMapping("/internal/users/exists")
    Boolean existsByEmail(@RequestParam("email") String email);

    @PostMapping("/internal/users/oauth")
    UserDto createOAuthUser(@RequestParam("email") String email,
                            @RequestParam("nickname") String nickname,
                            @RequestParam("provider") String provider);
}
