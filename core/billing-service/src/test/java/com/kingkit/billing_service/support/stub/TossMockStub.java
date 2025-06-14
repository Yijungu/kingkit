package com.kingkit.billing_service.support.stub;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.util.List;

public class TossMockStub {

    public static void stub404Response() {
        stubFor(get(urlEqualTo("/v1/billing/test-404"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"message\": \"Billing key not found\"}")));
    }

    public static void stub401Response() {
        stubFor(get(urlEqualTo("/v1/billing/test-401"))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"message\": \"Unauthorized\"}")));
    }

    public static void stubSlowResponse() {
        stubFor(get(urlEqualTo("/v1/billing/test-slow"))
            .willReturn(aResponse()
                .withStatus(200)
                .withFixedDelay(2500)
                .withBody("{\"message\": \"OK\"}")));
    }

    public static void stub200ResponseBilling2001() {
        stubFor(post(urlEqualTo("/v1/billing/billing-2001"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "paymentKey": "stub-pay-123",
                        "orderId": "stub-order-123",
                        "status": "DONE",
                        "paidAt": "2025-06-10T10:00:00"
                    }
                """)));
    }

    public static void stub200ResponseFor(String billingKey) {
        stubFor(post(urlEqualTo("/v1/billing/" + billingKey))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "paymentKey": "stub-pay-%s",
                        "orderId": "stub-order-%s",
                        "status": "DONE",
                        "paidAt": "2025-06-10T10:00:00"
                    }
                """.formatted(billingKey, billingKey))));
    }

    public static void stubFailResponseFor(String billingKey, int statusCode, String message) {
        stubFor(post(urlEqualTo("/v1/billing/" + billingKey))
            .willReturn(aResponse()
                .withStatus(statusCode)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"message\": \"" + message + "\"}")));
    }

    public static void setupDefaultSuccessBillingStubs(List<Long> userIds) {
    for (Long userId : userIds) {
        String billingKey = "billing-" + userId;
        stubFor(post(urlEqualTo("/v1/billing/" + billingKey))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "paymentKey": "stub-pay-%s",
                        "orderId": "stub-order-%s",
                        "status": "DONE",
                        "paidAt": "2025-06-10T10:00:00"
                    }
                """.formatted(userId, userId))));
        }
    }

    public static void setupDefaultSuccessPaymentsStubs(List<Long> userIds) {
    for (Long userId : userIds) {
        String billingKey = "billing-" + userId;
        stubFor(get(urlEqualTo("/v1/payments/" + billingKey))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "paymentKey": "stub-pay-%s",
                        "orderId": "stub-order-%s",
                        "status": "DONE",
                        "paidAt": "2025-06-10T10:00:00"
                    }
                """.formatted(userId, userId))));
        }
    }

        public static void stubDeleteBillingKeySuccess(String billingKey) {
        stubFor(delete(urlEqualTo("/v1/billing/" + billingKey))
            .willReturn(aResponse()
                .withStatus(204))); // Toss에서는 보통 204 No Content 응답
    }


}
