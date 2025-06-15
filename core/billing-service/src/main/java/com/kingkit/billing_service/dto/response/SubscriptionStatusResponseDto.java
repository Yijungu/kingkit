package com.kingkit.billing_service.dto.response;

import com.kingkit.billing_service.domain.payment.PaymentMethod;
import com.kingkit.billing_service.domain.subscription.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 현재 로그인한 사용자의 구독 상태 정보를 나타내는 DTO.
 * ACTIVE 상태의 구독이 존재할 경우 상세 정보를 포함.
 */
@Getter
@Builder
@AllArgsConstructor
public class SubscriptionStatusResponseDto {
    private String planName;
    private LocalDateTime subscribedAt;
    private LocalDateTime nextBillingAt;
    private boolean isActive;
    private CardInfo cardInfo;

    /**
     * ACTIVE 구독이 존재할 경우의 응답 생성
     */
    public static SubscriptionStatusResponseDto from(Subscription subscription) {
        return SubscriptionStatusResponseDto.builder()
                .planName(subscription.getPlan().getName())
                .subscribedAt(subscription.getStartedAt())
                .nextBillingAt(subscription.getNextBillingAt())
                .isActive(true)
                .cardInfo(CardInfo.from(subscription.getPaymentMethod()))
                .build();
    }

    /**
     * ACTIVE 구독이 존재하지 않을 경우의 응답 생성
     */
    public static SubscriptionStatusResponseDto inactive() {
        return SubscriptionStatusResponseDto.builder()
                .isActive(false)
                .build();
    }

    /**
     * 테스트나 샘플용 응답을 쉽게 만들 수 있도록 하는 팩토리
     */
    public static SubscriptionStatusResponseDto sample(
            boolean isActive,
            String planName,
            String cardCompany,
            String cardNumberMasked
    ) {
        return SubscriptionStatusResponseDto.builder()
                .isActive(isActive)
                .planName(planName)
                .cardInfo(
                        CardInfo.builder()
                                .cardCompany(cardCompany)
                                .cardNumberMasked(cardNumberMasked)
                                .build()
                )
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CardInfo {
        private String cardCompany;
        private String cardNumberMasked;

        public static CardInfo from(PaymentMethod paymentMethod) {
            return CardInfo.builder()
                    .cardCompany(paymentMethod.getCardCompany())
                    .cardNumberMasked(paymentMethod.getCardNumberMasked())
                    .build();
        }
    }
}
