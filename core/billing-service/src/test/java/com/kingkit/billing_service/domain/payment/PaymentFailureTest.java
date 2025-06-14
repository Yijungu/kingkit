package com.kingkit.billing_service.domain.payment;

import com.kingkit.billing_service.domain.subscription.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class PaymentFailureTest {

    private PaymentFailure failure;
    private Subscription dummySubscription;

    @BeforeEach
    void setUp() {
        // Dummy Subscription: 실제 로직에선 Mock or TestEntity 사용
        dummySubscription = Subscription.builder()
                .id(1L)
                .userId(100L)
                .plan(null)
                .paymentMethod(null)
                .startedAt(LocalDateTime.now().minusDays(10))
                .nextBillingAt(LocalDateTime.now().plusDays(1))
                .status(null)
                .build();

        failure = PaymentFailure.builder()
                .subscription(dummySubscription)
                .failedAt(LocalDateTime.now())
                .reason("카드 한도 초과")
                .retryCount(0)
                .retryScheduledAt(LocalDateTime.now())
                .resolved(false)
                .build();
    }

    @Test
    @DisplayName("markResolved() 호출 시 resolved 필드가 true로 바뀐다")
    void markResolved_shouldSetResolvedTrue() {
        // when
        failure.markResolved();

        // then
        assertThat(failure.isResolved()).isTrue();
    }

    @Test
    @DisplayName("scheduleNextRetry() 호출 시 retryCount 증가 및 기본 1시간 뒤로 스케줄링된다")
    void scheduleNextRetry_shouldIncreaseCountAndSetNextHour() {
        // given
        LocalDateTime before = failure.getRetryScheduledAt();

        // when
        failure.scheduleNextRetry();

        // then
        assertThat(failure.getRetryCount()).isEqualTo(1);
        assertThat(failure.getRetryScheduledAt()).isAfter(before.plusMinutes(59));
        assertThat(failure.getRetryScheduledAt()).isBefore(before.plusHours(2));
    }

    @Test
    @DisplayName("scheduleNextRetry(Duration) 호출 시 지정한 간격만큼 스케줄링된다")
    void scheduleNextRetry_customInterval_shouldApplyCorrectly() {
        // given
        Duration interval = Duration.ofMinutes(30);
        LocalDateTime before = LocalDateTime.now();

        // when
        failure.scheduleNextRetry(interval);

        // then
        assertThat(failure.getRetryCount()).isEqualTo(1);
        assertThat(failure.getRetryScheduledAt()).isAfter(before.plusMinutes(29));
        assertThat(failure.getRetryScheduledAt()).isBefore(before.plusMinutes(31));
    }

    @Test
    @DisplayName("scheduleNextRetry(null) 호출 시 IllegalArgumentException 예외 발생")
    void scheduleNextRetry_null_shouldThrowException() {
        assertThatThrownBy(() -> failure.scheduleNextRetry(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재시도 간격(interval)은 null일 수 없습니다.");
    }
}
