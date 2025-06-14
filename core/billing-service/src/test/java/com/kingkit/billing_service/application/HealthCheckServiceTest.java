package com.kingkit.billing_service.application;

import com.kingkit.billing_service.application.usecase.HealthCheckService;
import com.kingkit.billing_service.client.toss.TossClient;
import com.kingkit.billing_service.dto.response.PgHealthResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HealthCheckServiceTest {

    private HealthCheckService healthCheckService;
    private TossClient tossClient;

    @BeforeEach
    void setUp() {
        tossClient = mock(TossClient.class);
        healthCheckService = new HealthCheckService(tossClient);
    }

    @Test
    @DisplayName("✅ Toss 연결이 정상일 경우 isAvailable=true")
    void tossHealthSuccess() {
        // given
        when(tossClient.testConnection(null)).thenReturn(HttpStatus.OK);

        // when
        PgHealthResponse result = healthCheckService.checkPgHealth();

        // then
        assertThat(result.isAvailable()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("Toss 연동 정상");
    }

    @Test
    @DisplayName("❌ Toss SecretKey 오류 발생 시 isAvailable=false")
    void tossUnauthorized() {
        // given
        String errorMessage = "401 Unauthorized";
        when(tossClient.testConnection(null)).thenThrow(new RuntimeException(errorMessage));

        // when & then
        assertUnavailable(errorMessage);
    }

    @Test
    @DisplayName("❌ Toss 서버 응답 불가 시 isAvailable=false")
    void tossTimeout() {
        // given
        String errorMessage = "502 Gateway Timeout";
        when(tossClient.testConnection(null)).thenThrow(new RuntimeException(errorMessage));

        // when & then
        assertUnavailable(errorMessage);
    }

    // ✅ 실패 응답 검증 공통화
    private void assertUnavailable(String errorMessage) {
        PgHealthResponse result = healthCheckService.checkPgHealth();

        assertThat(result.isAvailable()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
        assertThat(result.getMessage()).contains(errorMessage);
    }
}
