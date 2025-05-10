package com.kingkit.lib_test_support.testsupport.jwt;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;

public class JwtTestUtils {

    public static final String DEFAULT_VALID_TOKEN = "valid.token.here";
    public static final String DEFAULT_INVALID_TOKEN = "invalid.token";

    public static MockHttpServletRequest requestWithToken(String uri, String token) {
        var request = new MockHttpServletRequest("GET", uri);
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }

    public static MockHttpServletRequest requestWithoutToken(String uri) {
        return new MockHttpServletRequest("GET", uri);
    }

    public static MockHttpServletResponse response() {
        return new MockHttpServletResponse();
    }

    public static MockFilterChain chain() {
        return new MockFilterChain();
    }
}
