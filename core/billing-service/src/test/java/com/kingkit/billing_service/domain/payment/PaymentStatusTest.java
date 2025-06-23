package com.kingkit.billing_service.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentStatusTest {

    @Nested
    @DisplayName("from() mapping")
    class From {
        @Test
        void mapsKnownValues() {
            assertThat(PaymentStatus.from("SUCCESS")).isEqualTo(PaymentStatus.SUCCESS);
            assertThat(PaymentStatus.from("FAILED")).isEqualTo(PaymentStatus.FAILED);
        }

        @Test
        void unknownValueReturnsUnknown() {
            assertThat(PaymentStatus.from("whoknows")).isEqualTo(PaymentStatus.UNKNOWN);
        }
    }

    @Test
    void predicatesWorkAsExpected() {
        assertThat(PaymentStatus.SUCCESS.isSuccess()).isTrue();
        assertThat(PaymentStatus.FAILED.isFailure()).isTrue();
        assertThat(PaymentStatus.CANCELED.isFailure()).isTrue();
        assertThat(PaymentStatus.IN_PROGRESS.isPending()).isTrue();
        assertThat(PaymentStatus.UNKNOWN.isSuccess()).isFalse();
    }
}
