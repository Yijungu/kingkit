package com.kingkit.billing_service.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TossWebhookRequestTest {
    @Test
    @DisplayName("CardInfo 생성 확인")
    void cardInfo() {
        TossWebhookRequest.CardInfo info = new TossWebhookRequest.CardInfo("comp", "1234");
        TossWebhookRequest req = new TossWebhookRequest("event", "bkey", "ckey", "oid", "2024", info);
        assertThat(req.cardInfo().company()).isEqualTo("comp");
        assertThat(req.cardInfo().numberMasked()).isEqualTo("1234");
        assertThat(req.eventType()).isEqualTo("event");
    }
}
