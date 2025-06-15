package com.kingkit.billing_service.util;

import java.time.*;

public final class DateRange {

    private DateRange() {}

    public static LocalDateTime startOf(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime endOf(LocalDate date) {
        return date.atTime(LocalTime.MAX);      // 23:59:59.999999999
    }
}
