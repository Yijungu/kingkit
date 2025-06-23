package com.kingkit.billing_service.application.usecase;

import com.kingkit.billing_service.client.toss.TossPaymentQueryClient;
import com.kingkit.billing_service.domain.payment.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class TossPaymentQueryServiceTest {

    @Mock
    TossPaymentQueryClient client;

    @InjectMocks
    TossPaymentQueryService service;

    @Test
    void mapStatusesProperly() {
        when(client.getPaymentDetail("k1")).thenReturn(Map.of("status", "DONE"));
        when(client.getPaymentDetail("k2")).thenReturn(Map.of("status", "FAILED"));
        when(client.getPaymentDetail("k3")).thenReturn(Map.of("status", "WHAT"));

        assertThat(service.getStatus("k1")).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(service.getStatus("k2")).isEqualTo(PaymentStatus.FAILED);
        assertThat(service.getStatus("k3")).isEqualTo(PaymentStatus.UNKNOWN);
    }

    @Test
    void exceptionResultsInUnknown() {
        doThrow(new RuntimeException("boom")).when(client).getPaymentDetail("oops");
        assertThat(service.getStatus("oops")).isEqualTo(PaymentStatus.UNKNOWN);
    }
}
