package com.kingkit.lib_security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint entryPoint; // ✅ 추가

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            if (tokenProvider.isTokenValid(token)) {
                String userId = tokenProvider.getUserId(token);
                String role = tokenProvider.getRole(token);

                var auth = new UsernamePasswordAuthenticationToken(
                        userId, null, Collections.singleton(() -> role)
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } else {
                // ✅ 유효하지 않은 토큰이면 entryPoint 호출
                entryPoint.commence(request, response,
                        new BadCredentialsException("Invalid JWT token"));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}