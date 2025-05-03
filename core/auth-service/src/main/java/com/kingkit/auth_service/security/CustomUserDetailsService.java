package com.kingkit.auth_service.security;

import com.kingkit.auth_service.feign.UserClient;
import com.kingkit.auth_service.feign.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            UserDto user = userClient.getUserByEmail(email);
            return new UserPrincipal(user);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found: " + email);
        }
    }
}
