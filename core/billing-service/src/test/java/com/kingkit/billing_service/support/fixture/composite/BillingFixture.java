package com.kingkit.billing_service.support.fixture.composite;

import java.time.LocalDate;
import java.util.List;

import com.kingkit.billing_service.dto.request.AdminBillingTriggerRequest;
import com.kingkit.billing_service.dto.request.ManualBillingRequestDto;
import com.kingkit.billing_service.dto.request.PrepareBillingRequest;
import com.kingkit.billing_service.dto.response.BillingKeyDeleteResponse;
import com.kingkit.billing_service.dto.response.PgHealthResponse;
import com.kingkit.billing_service.support.fixture.dto.ManualBillingRequestDtoFixture;
import com.kingkit.billing_service.support.fixture.dto.PrepareBillingRequestFixture;

public class BillingFixture {

    public static PrepareBillingRequest validPrepareRequest() {
        return PrepareBillingRequestFixture.defaultRequest();
    }

    public static ManualBillingRequestDto validManualBillingRequest() {
        return ManualBillingRequestDtoFixture.defaultRequest();
    }

    public static AdminBillingTriggerRequest validTriggerRequest() {
        return new AdminBillingTriggerRequest(LocalDate.now(), List.of(1001L, 1002L));
    }

    public static BillingKeyDeleteResponse deletedKeyResponse() {
        return BillingKeyDeleteResponse.builder()
                .billingKey("billing-key")
                .deleted(true)
                .message("정상 삭제")
                .build();
    }

    public static PgHealthResponse healthyPgResponse() {
        return PgHealthResponse.builder()
                .available(true)
                .statusCode(200) // ✅ 정수 → HttpStatus enum
                .message("OK")
                .responseTimeMillis(102L)
                .slow(false)
                .build();
    }
}

