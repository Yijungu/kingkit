package com.kingkit.billing_service.config.webhook;

import com.kingkit.billing_service.util.TossSignatureVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class WebhookSignatureFilter extends OncePerRequestFilter {

    private final TossSignatureVerifier tossSignatureVerifier;

    public WebhookSignatureFilter(TossSignatureVerifier tossSignatureVerifier) {
        this.tossSignatureVerifier = tossSignatureVerifier;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // ✅ Toss Webhook URL만 필터 적용
        return !request.getRequestURI().equals("/webhook/toss");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);

        // ✅ 여기서 read 하면 안 됨!! 아래의 StreamUtils 사용 X
        // 대신 filterChain 진행 후 읽어야 안전

        String signature = request.getHeader("Toss-Signature");

        // ✅ FilterChain 호출 전에 read 하지 않도록 함
        filterChain.doFilter(cachingRequest, response);

        // ✅ filterChain 이후에 Raw Body 안전하게 읽기
        String rawBody = new String(cachingRequest.getContentAsByteArray(), StandardCharsets.UTF_8);

        boolean verified = tossSignatureVerifier.verify(rawBody, signature);
        log.info("Signature verified: {}, signature={}", verified, signature);
        
        if (!verified) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Invalid Toss Signature");
        }
    }
}
