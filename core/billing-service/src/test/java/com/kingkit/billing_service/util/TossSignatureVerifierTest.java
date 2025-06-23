package com.kingkit.billing_service.util;

import com.kingkit.billing_service.support.fixture.composite.WebhookTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class TossSignatureVerifierTest {

    private TossSignatureVerifier verifier;

    @BeforeEach
    void setUp() throws Exception {
        verifier = new TossSignatureVerifier();
        Field f = TossSignatureVerifier.class.getDeclaredField("secretKey");
        f.setAccessible(true);
        f.set(verifier, WebhookTestFixture.TOSS_SECRET_KEY);
    }

    @Test
    @DisplayName("verify() returns true for valid signature")
    void verify_validSignature_returnsTrue() {
        String body = "{\"hello\":\"world\"}";
        String sig = WebhookTestFixture.validSignature(body);

        assertThat(verifier.verify(body, sig)).isTrue();
    }

    @Test
    @DisplayName("verify() returns false for invalid signature")
    void verify_invalidSignature_returnsFalse() {
        String body = "{\"hello\":\"world\"}";

        assertThat(verifier.verify(body, "wrong")).isFalse();
    }

    @Test
    @DisplayName("verify() returns false when secretKey is null")
    void verify_nullSecretKey_returnsFalse() throws Exception {
        Field f = TossSignatureVerifier.class.getDeclaredField("secretKey");
        f.setAccessible(true);
        f.set(verifier, null);

        assertThat(verifier.verify("body", "sig")).isFalse();
    }
}
