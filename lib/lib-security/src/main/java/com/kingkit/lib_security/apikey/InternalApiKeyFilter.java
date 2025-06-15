package com.kingkit.lib_security.apikey;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private final Set<String> apiKeys;
    private final List<String> allowedIps;
    private final Predicate<HttpServletRequest> uriMatcher;

    private static final String HEADER_NAME = "X-Internal-API-Key";
    private static final String LOOPBACK_V6 = "0:0:0:0:0:0:0:1";

    /** 기본 Matcher: /internal/users/** */
    public InternalApiKeyFilter(ApiKeyProperties props) {
        this(props.apiKeys(),
             props.allowedIps(),
             req -> req.getRequestURI().startsWith("/internal"));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !uriMatcher.test(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {

        String requestIp = normalizeIp(req.getRemoteAddr());   // ★
        String key       = req.getHeader(HEADER_NAME);

        /* 1) 키/아이피 검증 -------------------------------------------------- */
        if (apiKeys == null || apiKeys.isEmpty()) {
            log.error("❌ API-Key 미설정. IP={}", requestIp);
            res.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal API-Key not configured");
            return;
        }
        if (key == null || !apiKeys.contains(key)) {
            log.warn("❌ 잘못된 키. IP={}, 받은 키={}, 허용 키 목록={}", requestIp, mask(key), apiKeys);
            res.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid Internal API-Key");
            return;
        }
        
        if (allowedIps != null && !allowedIps.isEmpty() && !allowedIps.contains(requestIp)) {
            log.warn("❌ 허용되지 않은 IP. IP={}", requestIp);
            res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied from IP: " + requestIp);
            return;
        }

        /* 2) 검증 성공 → SecurityContext 에 인증 토큰 생성 --------------------- */
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                    "internal-call",                       // principal
                    null,                                 // credentials
                    List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
            );
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(req, res);
    }

    /* ------------------------------------------------------------- */

    private String normalizeIp(String ip) {
        return LOOPBACK_V6.equals(ip) ? "::1" : ip;            // IPv6 loop-back 보정
    }

    private String mask(String key) {
        if (key == null || key.length() < 4) return "null";
        return key.substring(0, 4) + "****";
    }
}
