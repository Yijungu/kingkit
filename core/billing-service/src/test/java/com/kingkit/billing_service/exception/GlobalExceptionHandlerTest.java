package com.kingkit.billing_service.exception;

import com.kingkit.billing_service.exception.core.*;
import com.kingkit.billing_service.exception.domain.billing.InvalidBillingKeyException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final HttpServletRequest req = new MockHttpServletRequest("GET", "/test");

    @Test
    void handleBusinessReturnsProperErrorCode() {
        BusinessException ex = new InvalidBillingKeyException("bad");
        var response = handler.handleBusiness(ex, req);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.BILLING_KEY_NOT_FOUND.getCode());
    }

    @Test
    void handleUnknownReturnsInternalServerError() {
        var response = handler.handleUnknown(new RuntimeException("oops"), req);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getCode());
    }
}
