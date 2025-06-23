package com.kingkit.billing_service.config.webhook;

import com.kingkit.billing_service.support.fixture.composite.WebhookTestFixture;
import com.kingkit.billing_service.util.TossSignatureVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class WebhookSignatureFilterTest {

    private WebhookSignatureFilter filter;

    @BeforeEach
    void setUp() throws Exception {
        TossSignatureVerifier verifier = new TossSignatureVerifier();
        Field f = TossSignatureVerifier.class.getDeclaredField("secretKey");
        f.setAccessible(true);
        f.set(verifier, WebhookTestFixture.TOSS_SECRET_KEY);
        filter = new WebhookSignatureFilter(verifier);
    }

    @Test
    @DisplayName("shouldNotFilter returns false for webhook path")
    void shouldFilterWebhookPath() {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/webhook/toss");
        assertThat(filter.shouldNotFilter(req)).isFalse();
    }

    @Test
    @DisplayName("shouldNotFilter returns true for other paths")
    void shouldNotFilterOtherPath() {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/health");
        assertThat(filter.shouldNotFilter(req)).isTrue();
    }

    @Test
    @DisplayName("invalid signature results in 401")
    void invalidSignature_responds401() throws Exception {
        String body = "{\"ok\":true}";
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/webhook/toss");
        req.setContent(body.getBytes(StandardCharsets.UTF_8));
        req.addHeader("Toss-Signature", "wrong");

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = (request, response) -> request.getInputStream().readAllBytes();

        filter.doFilter(req, res, chain);

        assertThat(res.getStatus()).isEqualTo(401);
        assertThat(res.getContentAsString()).contains("Invalid Toss Signature");
    }

    @Test
    @DisplayName("valid signature allows request")
    void validSignature_passes() throws Exception {
        String body = "{\"ok\":true}";
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/webhook/toss");
        req.setContent(body.getBytes(StandardCharsets.UTF_8));
        req.addHeader("Toss-Signature", WebhookTestFixture.validSignature(body));

        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = (request, response) -> request.getInputStream().readAllBytes();

        filter.doFilter(req, res, chain);

        assertThat(res.getStatus()).isNotEqualTo(401);
    }
}
