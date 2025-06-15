package com.kingkit.billing_service.api.external;

import com.kingkit.billing_service.application.usecase.BillingHistoryService;
import com.kingkit.billing_service.application.usecase.BillingService;
import com.kingkit.billing_service.application.usecase.RetryService;
import com.kingkit.billing_service.dto.request.PrepareBillingRequest;
import com.kingkit.billing_service.dto.request.RetryPaymentRequest;
import com.kingkit.billing_service.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "결제 · 구독 API", description = "결제 수단 등록, 구독 상태, 결제 재시도, 이력 등 외부 사용자 API")
public class ExternalBillingController {

    private final BillingService billingService;
    private final BillingHistoryService billingHistoryService;
    private final RetryService retryService;

    @Operation(
            summary = "결제 수단 등록",
            description = "Toss 결제 Checkout URL을 생성하여 반환합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 수단 등록 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 요금제 ID", content = @Content),
                    @ApiResponse(responseCode = "502", description = "PG사 응답 실패", content = @Content)
            }
    )
    @PostMapping("/billing/prepare")
    public ResponseEntity<PrepareBillingResponse> prepareBilling(
            @Valid @RequestBody PrepareBillingRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        PrepareBillingResponse response = billingService.prepareBilling(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "구독 상태 조회",
        description = "현재 활성화된 구독 상태 및 요금제, 결제 수단 마스킹 정보를 조회합니다.",
        responses = {
                @ApiResponse(responseCode = "200", description = "구독 상태 조회 성공")
        }
    )
    @GetMapping("/subscription")
    public ResponseEntity<SubscriptionStatusResponseDto> getSubscriptionStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        SubscriptionStatusResponseDto response = billingService.getSubscriptionStatus(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "구독 해지",
            description = "활성화된 구독을 해지하고 결제 수단을 비활성화 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "구독 해지 성공"),
                    @ApiResponse(responseCode = "400", description = "구독 없음 / 이미 해지된 구독", content = @Content),
                    @ApiResponse(responseCode = "502", description = "PG사 BillingKey 폐기 실패", content = @Content)
            }
    )
    @DeleteMapping("/subscription")
    public ResponseEntity<SubscriptionCancelResponseDto> cancelSubscription(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        SubscriptionCancelResponseDto response = billingService.cancelSubscription(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "결제 재시도",
            description = "결제 실패 건에 대해 수동 재시도를 요청합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 재시도 성공 or 실패 처리"),
                    @ApiResponse(responseCode = "400", description = "이미 resolved된 실패건", content = @Content)
            }
    )
    @PostMapping("/billing/retry")
    public ResponseEntity<RetryPaymentResponse> retryPayment(
            @Valid @RequestBody RetryPaymentRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        RetryPaymentResponse response = retryService.retryFailedPayment(request, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "결제 이력 조회 (페이징)",
            description = "현재 유저의 결제 이력을 페이징하여 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 이력 조회 성공"),
                    @ApiResponse(responseCode = "401", description = "비인증 요청", content = @Content),
                    @ApiResponse(responseCode = "403", description = "잘못된 접근", content = @Content)
            }
    )
    @GetMapping("/billing/history")
    public ResponseEntity<Page<PaymentHistoryResponse>> getBillingHistory(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Parameter(description = "페이징 정보 (page, size, sort)") Pageable pageable
    ) {
        Page<PaymentHistoryResponse> page = billingHistoryService.getUserBillingHistory(userId, pageable);
        return ResponseEntity.ok(page);
    }
}
