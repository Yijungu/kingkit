package com.kingkit.billing_service.client.toss;

import com.kingkit.billing_service.client.AbstractWebhookVerifier;
import com.kingkit.billing_service.config.toss.TossProperties;

import org.springframework.stereotype.Component;

@Component
public class TossWebhookVerifier extends AbstractWebhookVerifier {

    private final TossProperties properties;

    public TossWebhookVerifier(TossProperties properties) {
        this.properties = properties;
    }

    @Override
    protected String secretKey() {
        return properties.secretKey(); // TossProperties에서 가져옴
    }
}
