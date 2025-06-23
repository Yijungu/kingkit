package com.kingkit.billing_service.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class DateRangeTest {

    @Test
    void startOf_returnsStartOfDay() {
        LocalDate date = LocalDate.of(2025, 6, 1);
        assertThat(DateRange.startOf(date)).isEqualTo(date.atStartOfDay());
    }

    @Test
    void endOf_returnsEndOfDay() {
        LocalDate date = LocalDate.of(2025, 6, 1);
        assertThat(DateRange.endOf(date)).isEqualTo(date.atTime(LocalTime.MAX));
    }
}
