package com.kingkit.billing_service.util;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class OrderIdGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public String generate() {
        String timestamp = java.time.LocalDateTime.now().format(FORMATTER);
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return "order-" + timestamp + "-" + uuid;
    }
}
