package com.kingkit.billing_service.api.internal;

import com.kingkit.billing_service.application.usecase.BillingService;
import com.kingkit.billing_service.application.usecase.BillingTriggerService;
import com.kingkit.billing_service.application.usecase.HealthCheckService;
import com.kingkit.billing_service.dto.request.AdminBillingTriggerRequest;
import com.kingkit.billing_service.dto.request.ManualBillingRequestDto;
import com.kingkit.billing_service.dto.response.AdminBillingTriggerResponse;
import com.kingkit.billing_service.dto.response.BillingKeyDeleteResponse;
import com.kingkit.billing_service.dto.response.ManualBillingResponseDto;
import com.kingkit.billing_service.dto.response.PgHealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
@Tag(name = "InternalBillingController", description = "내부 관리자 및 시스템용 결제 API")
public class InternalBillingController {

    private final BillingService billingService;
    private final BillingTriggerService billingTriggerService;
    private final HealthCheckService healthCheckService;

    @PostMapping("/billing/trigger")
    @Operation(summary = "정기 결제 트리거", description = "날짜 및 유저 ID 기반으로 정기 결제를 실행합니다.")
    public ResponseEntity<AdminBillingTriggerResponse> triggerBilling(
            @Valid @RequestBody AdminBillingTriggerRequest request
    ) {
        AdminBillingTriggerResponse response = billingTriggerService.triggerScheduledBilling(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/billing/execute")
    @Operation(summary = "수동 결제 실행", description = "관리자 또는 시스템에서 특정 유저에 대해 수동 결제를 실행합니다.")
    public ResponseEntity<ManualBillingResponseDto> executeManualBilling(
            @Valid @RequestBody ManualBillingRequestDto request
    ) {
        ManualBillingResponseDto response = billingService.executeManualBilling(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/billing/key")
    @Operation(summary = "BillingKey 삭제", description = "유저 ID 기반으로 BillingKey를 삭제 요청합니다.")
    public ResponseEntity<BillingKeyDeleteResponse> deleteBillingKey(
            @RequestParam Long userId
    ) {
        BillingKeyDeleteResponse response = billingService.deleteBillingKey(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pg/health")
    @Operation(summary = "PG 상태 확인", description = "PG사(Toss)와의 연결 상태 및 응답 속도를 점검합니다.")
    public ResponseEntity<PgHealthResponse> checkPgHealth(
            @RequestParam(value = "billingKey", required = false) String billingKey
    ) {
        PgHealthResponse response = healthCheckService.checkPgHealth(billingKey);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

} 
