package com.kingkit.billing_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PrepareBillingRequest(

    @NotNull(message = "userId는 필수입니다.")
    Long userId,

    @NotBlank(message = "planId는 필수입니다.")
    String planId,

    @NotBlank(message = "successUrl은 필수입니다.")
    String successUrl,

    @NotBlank(message = "failUrl은 필수입니다.")
    String failUrl

) {}
