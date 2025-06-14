package com.kingkit.billing_service.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 사용자 결제 수단 등록 요청 DTO
 * 
 * ⚠️ userId는 JWT 인증에서 별도로 추출하여 서비스 메서드에 전달
 */
public record PrepareBillingRequest(

    @NotBlank(message = "planId는 필수입니다.")
    String planId,

    @NotBlank(message = "successUrl은 필수입니다.")
    String successUrl,

    @NotBlank(message = "failUrl은 필수입니다.")
    String failUrl

) {}
