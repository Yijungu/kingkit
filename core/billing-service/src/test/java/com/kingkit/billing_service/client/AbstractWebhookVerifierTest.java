package com.kingkit.billing_service.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractWebhookVerifierTest {

    @Test
    @DisplayName("verify 성공")
    void verify_ok() throws Exception {
        TestVerifier verifier = new TestVerifier();
        String payload = "hello";
        String sig = verifier.calculateSignature(payload, "secret");
        assertThat(verifier.verify(payload, sig)).isTrue();
    }

    @Test
    @DisplayName("verify 실패와 예외 처리")
    void verify_failAndException() {
        TestVerifier verifier = new TestVerifier();
        assertThat(verifier.verify("hello", "wrong")).isFalse();
        FaultyVerifier faulty = new FaultyVerifier();
        assertThat(faulty.verify("x", "sig")).isFalse();
    }

    private static class TestVerifier extends AbstractWebhookVerifier {
        @Override protected String secretKey() { return "secret"; }
    }

    private static class FaultyVerifier extends TestVerifier {
        @Override
        protected String calculateSignature(String payload, String key) throws Exception {
            throw new Exception("oops");
        }
    }
}
