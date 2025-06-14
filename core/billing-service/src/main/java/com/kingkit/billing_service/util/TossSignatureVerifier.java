package com.kingkit.billing_service.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossSignatureVerifier {

    @Value("${toss.secret-key}")
    private String secretKey;

    public boolean verify(String body, String tossSignature) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(keySpec);

            byte[] hash = sha256_HMAC.doFinal(body.getBytes(StandardCharsets.UTF_8));
            String calculated = Base64.getEncoder().encodeToString(hash);

            return calculated.equals(tossSignature);
        } catch (Exception e) {
            return false;
        }
    }
}
