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
import com.kingkit.billing_service.support.fixture.composite.BillingFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class InternalBillingControllerTest {

    @Mock
    private BillingService billingService;

    @Mock
    private HealthCheckService healthCheckService;

    @Mock
    private BillingTriggerService billingTriggerService;

    @InjectMocks
    private InternalBillingController controller;

    public InternalBillingControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("✅ 수동 결제 요청 성공")
    void executeManualBilling_success() {
        ManualBillingRequestDto request = BillingFixture.validManualBillingRequest();
            ManualBillingResponseDto response = ManualBillingResponseDto.success(
            "pay-123", "order-456", LocalDate.now().atStartOfDay());

        when(billingService.executeManualBilling(request)).thenReturn(response);

        ManualBillingResponseDto result = controller.executeManualBilling(request).getBody();;
        assertThat(result.getPaymentKey()).isEqualTo("pay-123");
    }

    @Test
    @DisplayName("✅ PG 상태 체크 응답")
    void checkPgHealth_returnsResponse() {
        PgHealthResponse mockResponse = PgHealthResponse.builder()
            .available(true)
            .statusCode(200) // ✅ 변경: int → HttpStatus
            .message("OK")
            .responseTimeMillis(102L)
            .slow(false)
            .build();

        when(healthCheckService.checkPgHealth(null)).thenReturn(mockResponse);

        PgHealthResponse response = controller.checkPgHealth(null).getBody();

        assertThat(response).isNotNull();
        assertThat(response.isAvailable()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(200); // ✅ 타입 맞춤 검증
        assertThat(response.getMessage()).isEqualTo("OK");
    }


    @Test
    @DisplayName("✅ BillingKey 삭제 요청 성공")
    void deleteBillingKey_success() {
        BillingKeyDeleteResponse response = BillingKeyDeleteResponse.success("billing-key");
        when(billingService.deleteBillingKey(1234L)).thenReturn(response);

        BillingKeyDeleteResponse result = controller.deleteBillingKey(1234L).getBody();;
        assertThat(result.getBillingKey()).isEqualTo("billing-key");
        assertThat(result.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("✅ 정기결제 트리거 실행 성공")
    void triggerBilling_success() {
        AdminBillingTriggerRequest request = new AdminBillingTriggerRequest(LocalDate.now(), List.of(1001L, 1002L));
        AdminBillingTriggerResponse response = new AdminBillingTriggerResponse(2, 0, List.of());

        when(billingTriggerService.triggerScheduledBilling(any())).thenReturn(response);

        AdminBillingTriggerResponse result = controller.triggerBilling(request).getBody();;
        assertThat(result.getSuccessCount()).isEqualTo(2);
        assertThat(result.getFailureCount()).isEqualTo(0);
    }
}
