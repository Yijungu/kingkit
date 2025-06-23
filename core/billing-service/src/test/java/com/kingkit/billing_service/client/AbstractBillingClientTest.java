package com.kingkit.billing_service.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import com.kingkit.billing_service.client.dto.PaymentCommand;
import com.kingkit.billing_service.client.dto.PaymentResult;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"rawtypes","unchecked"})
class AbstractBillingClientTest {
    private WebClient webClient;
    private TestBillingClient client;
    private WebClient.RequestHeadersUriSpec delUri;
    private WebClient.RequestHeadersSpec delSpec;
    private WebClient.RequestHeadersUriSpec getUri;
    private WebClient.RequestHeadersSpec getSpec;
    private ResponseSpec response;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        delUri = mock(RequestHeadersUriSpec.class);
        delSpec = mock(RequestHeadersSpec.class);
        getUri = mock(RequestHeadersUriSpec.class);
        getSpec = mock(RequestHeadersSpec.class);
        response = mock(ResponseSpec.class);

        client = new TestBillingClient(webClient);

        lenient().when(delUri.uri(anyString())).thenReturn((RequestHeadersSpec) delSpec);
        lenient().when(delUri.uri(anyString(), any(Object[].class))).thenReturn((RequestHeadersSpec) delSpec);
        lenient().when(delSpec.retrieve()).thenReturn(response);

        lenient().when(getUri.uri(anyString())).thenReturn((RequestHeadersSpec) getSpec);
        lenient().when(getSpec.retrieve()).thenReturn(response);
    }

    @Test
    @DisplayName("deleteBillingKey 성공")
    void deleteBillingKey_ok() {
        when(webClient.delete()).thenReturn(delUri);
        when(response.toBodilessEntity()).thenReturn(Mono.empty());
        assertThatCode(() -> client.deleteBillingKey("bkey")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteBillingKey 404 무시")
    void deleteBillingKey_404() {
        when(webClient.delete()).thenReturn(delUri);
        when(response.toBodilessEntity()).thenReturn(Mono.error(new WebClientResponseException(404, "NF", null, null, null)));
        assertThatCode(() -> client.deleteBillingKey("missing")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("deleteBillingKey 5xx 예외")
    void deleteBillingKey_5xx() {
        when(webClient.delete()).thenReturn(delUri);
        when(response.toBodilessEntity()).thenReturn(Mono.error(new WebClientResponseException(500, "Err", null, null, null)));
        assertThatThrownBy(() -> client.deleteBillingKey("err"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("delete failed");
    }

    @Test
    @DisplayName("testConnection 정상")
    void testConnection_ok() {
        when(webClient.get()).thenReturn(getUri);
        when(response.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));
        assertThat(client.testConnection("key")).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("testConnection 예외시 상태반환")
    void testConnection_error() {
        when(webClient.get()).thenReturn(getUri);
        when(response.toBodilessEntity()).thenReturn(Mono.error(new WebClientResponseException(401, "unauth", null, null, null)));
        assertThat(client.testConnection("key")).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private static class TestBillingClient extends AbstractBillingClient {
        TestBillingClient(WebClient client) { super(client); }
        @Override public PaymentResult execute(PaymentCommand c) { return null; }
        @Override public String requestCheckoutUrl(String a,String b,String c2,String d,long e) { return null; }
        @Override protected Mono<String> requestCheckoutUrlAsync(String s1, String s2, String s3, String s4, long a) { return Mono.empty(); }
        @Override protected Mono<String> executeBillingAsync(String b, String o, long a) { return Mono.empty(); }
    }
}
