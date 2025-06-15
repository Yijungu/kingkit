package com.kingkit.billing_service.client;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
public abstract class AbstractWebhookVerifier implements WebhookVerifier {

    protected abstract String secretKey(); // 서브클래스에서 비밀키 제공

    @Override
    public boolean verify(String payload, String signature) {
        try {
            String computedSignature = calculateSignature(payload, secretKey());
            return computedSignature.equals(signature);
        } catch (Exception e) {
            log.error("❌ Webhook 서명 검증 실패", e);
            return false;
        }
    }

    protected String calculateSignature(String payload, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(payload.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }
}
