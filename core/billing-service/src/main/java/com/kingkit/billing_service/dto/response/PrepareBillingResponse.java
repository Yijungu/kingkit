package com.kingkit.billing_service.dto.response;

public record PrepareBillingResponse(

    String checkoutUrl,   // Toss Checkout 페이지 URL
    String orderId,       // 주문 고유 ID
    String customerKey    // 고객 키 (예: user-1001)

) {}
