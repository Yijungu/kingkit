package com.kingkit.billing_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * Toss Webhook 수신용 DTO.
 * SUBSCRIPTION_REGISTERED 이벤트 발생 시 Toss에서 전달됨.
 */
public record TossWebhookRequest(
    @NotBlank String eventType,     // 예: "SUBSCRIPTION_REGISTERED"
    @NotBlank String billingKey,    // PG에서 발급한 결제키
    @NotBlank String customerKey,   // user-1001 형식의 내부 유저 식별자
    @NotBlank String orderId,       // prepareBilling 단계에서 생성된 주문 ID
    @NotBlank String approvedAt,    // 승인 시각 (ISO-8601 포맷)
    CardInfo cardInfo               // 카드 정보 (선택)
) {

    /**
     * 카드 정보 내 중첩 DTO
     */
    public record CardInfo(
        String company,             // 카드사 (예: 국민카드)
        String numberMasked         // 마스킹된 카드번호
    ) {}
}
