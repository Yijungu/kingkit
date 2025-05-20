package com.kingkit.billing_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 결제 실패 재시도 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor // ✅ 추가
public class RetryPaymentRequest {

    @NotNull(message = "구독 ID는 필수입니다.")
    private Long subscriptionId;

    @NotBlank(message = "orderId는 필수입니다.")
    private String orderId;

    @NotNull(message = "결제 금액은 필수입니다.")
    @Min(value = 100, message = "결제 금액은 100원 이상이어야 합니다.")
    private Long amount;
}
