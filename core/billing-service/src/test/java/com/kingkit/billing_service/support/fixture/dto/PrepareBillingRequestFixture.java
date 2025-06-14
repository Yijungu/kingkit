package com.kingkit.billing_service.support.fixture.dto;

import com.kingkit.billing_service.dto.request.PrepareBillingRequest;

public class PrepareBillingRequestFixture {

    public static PrepareBillingRequest of(String planId, String successUrl, String failUrl) {
        return new PrepareBillingRequest(planId, successUrl, failUrl);
    }

    public static PrepareBillingRequest defaultRequest() {
        return of("basic-monthly", "https://success.com", "https://fail.com");
    }
}
