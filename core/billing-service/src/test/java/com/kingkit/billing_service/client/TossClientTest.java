package com.kingkit.billing_service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;

import com.kingkit.billing_service.client.toss.TossClient;

import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TossClient – WebClient stub 기반 단위 테스트 (완전 리팩터링 버전)
 */
@SuppressWarnings({"rawtypes", "unchecked"})
class TossClientTest {

    /* ===== 공통 Mock  ===== */
    private WebClient webClient;
    private TossClient tossClient;

    /* ===== POST chain ===== */
    private WebClient.RequestBodyUriSpec   postBody;
    private WebClient.RequestBodySpec      postSpec;
    private WebClient.RequestHeadersSpec   postHdr;

    /* ===== GET chain  ===== */
    private WebClient.RequestHeadersUriSpec getUri;
    private WebClient.RequestHeadersSpec    getHdr;

    /* ===== DELETE chain === */
    private WebClient.RequestHeadersUriSpec delUri;
    private WebClient.RequestHeadersSpec    delHdr;

    /* ===== 공통 ResponseSpec */
    private WebClient.ResponseSpec response;

    /* -------------------------------------------------- */
    @BeforeEach
    void setUp() {
        /* 1. mock 생성 */
        webClient = mock(WebClient.class);

        postBody  = mock(WebClient.RequestBodyUriSpec.class);
        postSpec  = mock(WebClient.RequestBodySpec.class);
        postHdr   = mock(WebClient.RequestHeadersSpec.class);

        getUri    = mock(WebClient.RequestHeadersUriSpec.class);
        getHdr    = mock(WebClient.RequestHeadersSpec.class);

        delUri    = mock(WebClient.RequestHeadersUriSpec.class);
        delHdr    = mock(WebClient.RequestHeadersSpec.class);

        response  = mock(WebClient.ResponseSpec.class);

        /* 2. 실제 객체 생성 & WebClient 교체 */
        tossClient = TossClient.testClient("https://api.test-toss.com", "test-sk");
        ReflectionTestUtils.setField(tossClient, "client", webClient);

        /* 3. vararg 인자를 사용하는 uri() 오버로드에 대한 lenient 설정 – 공통화 */
        lenient().when(postBody.uri(anyString())).thenReturn(postBody);
        lenient().when(postBody.uri(anyString(), any(Object[].class))).thenReturn(postBody);
        lenient().when(postSpec.retrieve()).thenReturn(response);
        lenient().when(postBody.bodyValue(any())).thenReturn((RequestHeadersSpec) postSpec);

        lenient().when(getUri.uri(anyString())).thenReturn(getHdr);
        lenient().when(getUri.uri(anyString(), any(Object[].class))).thenReturn(getHdr);
        lenient().when(getHdr.retrieve()).thenReturn(response);

        lenient().when(delUri.uri(anyString())).thenReturn(delHdr);
        lenient().when(delUri.uri(anyString(), any(Object[].class))).thenReturn(delHdr);
        lenient().when(delHdr.retrieve()).thenReturn(response);
    }

    /* ================================================== */
    /* checkoutUrl                                        */
    /* ================================================== */
    @Test
    @DisplayName("✅ requestCheckoutUrl – checkoutUrl 반환")
    void requestCheckoutUrl_success() {
        when(webClient.post()).thenReturn(postBody);
        when(response.bodyToMono(Map.class))
                .thenReturn(Mono.just(Map.of("checkoutUrl", "https://test-checkout")));

        String url = tossClient.requestCheckoutUrl(
                "https://ok", "https://fail", "user-1", "order-1", 1_000);

        assertThat(url).isEqualTo("https://test-checkout");
    }

    /* ================================================== */
    /* executeBilling                                     */
    /* ================================================== */
    @Test
    @DisplayName("✅ executeBilling – paymentKey 반환")
    void executeBilling_success() {
        mockExecuteChain(Mono.just(Map.of("paymentKey", "pay-123")));
        assertThat(tossClient.executeBilling("billing-key", "order-1", 10_900))
                .isEqualTo("pay-123");
    }

    @Test
    @DisplayName("❌ executeBilling 실패 시 null 반환")
    void executeBilling_fail() {
        mockExecuteChain(Mono.error(new RuntimeException("Toss 실패")));
        assertThat(tossClient.executeBilling("billing-key", "order-1", 10_900))
                .isNull();
    }

    private void mockExecuteChain(Mono<Map> bodyMono) {
        when(webClient.post()).thenReturn(postBody);
        when(response.bodyToMono(Map.class)).thenReturn(bodyMono);
    }

    /* ================================================== */
    /* deleteBillingKey                                   */
    /* ================================================== */
    @Test
    @DisplayName("✅ deleteBillingKey – Toss 404 는 무시")
    void deleteBillingKey_ignore404() {
        mockDeleteChain(Mono.error(new WebClientResponseException(
                404, "Not Found", null, null, null)));

        assertThatCode(() -> tossClient.deleteBillingKey("missing"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("❌ deleteBillingKey – 5xx 시 예외 전파")
    void deleteBillingKey_serverError() {
        mockDeleteChain(Mono.error(new WebClientResponseException(
                500, "Internal Error", null, null, null)));

        assertThatThrownBy(() -> tossClient.deleteBillingKey("error"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("삭제 실패");
    }

    private void mockDeleteChain(Mono<ResponseEntity<Void>> mono) {
        when(webClient.delete()).thenReturn(delUri);
        when(response.toBodilessEntity()).thenReturn(mono);
    }

    /* ================================================== */
    /* testConnection                                     */
    /* ================================================== */
    @Test
    @DisplayName("✅ testConnection – 404 ⇒ 연결 OK")
    void testConnection_notFound() {
        mockHealthChain(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
        assertThat(tossClient.testConnection(null)).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("❌ testConnection – 401 도 그대로 반환")
    void testConnection_unauthorized() {
        mockHealthChain(Mono.error(new WebClientResponseException(
                401, "Unauthorized", null, null, null)));
        assertThat(tossClient.testConnection(null)).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private void mockHealthChain(Mono<ResponseEntity<Void>> mono) {
        when(webClient.get()).thenReturn(getUri);
        when(response.toBodilessEntity()).thenReturn(mono);
    }
}
