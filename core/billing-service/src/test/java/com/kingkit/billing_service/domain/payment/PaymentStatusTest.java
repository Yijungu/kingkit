package com.kingkit.billing_service.domain.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentStatusTest {

    @Test
    @DisplayName("from() 메서드는 대소문자와 무관하게 상태를 매핑한다")
    void from_mapsValueIgnoringCase() {
        assertThat(PaymentStatus.from("success")).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(PaymentStatus.from("FAILED")).isEqualTo(PaymentStatus.FAILED);
        assertThat(PaymentStatus.from("Canceled")).isEqualTo(PaymentStatus.CANCELED);
    }

    @Test
    @DisplayName("from() 메서드는 알 수 없는 값을 입력하면 UNKNOWN을 반환한다")
    void from_unknownValue_returnsUnknown() {
        assertThat(PaymentStatus.from("something")).isEqualTo(PaymentStatus.UNKNOWN);
    }

    @Test
    @DisplayName("상태 편의 메서드들은 올바른 값을 반환한다")
    void helperMethods_workCorrectly() {
        assertThat(PaymentStatus.SUCCESS.isSuccess()).isTrue();
        assertThat(PaymentStatus.SUCCESS.isFailure()).isFalse();
        assertThat(PaymentStatus.SUCCESS.isPending()).isFalse();

        assertThat(PaymentStatus.FAILED.isFailure()).isTrue();
        assertThat(PaymentStatus.CANCELED.isFailure()).isTrue();

        assertThat(PaymentStatus.IN_PROGRESS.isPending()).isTrue();
    }
}
