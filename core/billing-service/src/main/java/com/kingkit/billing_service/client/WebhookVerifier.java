package com.kingkit.billing_service.client;

public interface WebhookVerifier {

    /**
     * 수신된 Webhook 요청이 유효한지 검증
     * @param payload 원본 요청 본문 (JSON)
     * @param signature 헤더에 포함된 서명 값
     * @return 유효한 경우 true, 위조된 경우 false
     */
    boolean verify(String payload, String signature);
}
