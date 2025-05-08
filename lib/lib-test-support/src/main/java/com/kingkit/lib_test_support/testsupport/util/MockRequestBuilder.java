package com.kingkit.testsupport.util;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class MockRequestBuilder {

    public static MockHttpServletRequestBuilder getWithAuth(String uri, String token) {
        return get(uri)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json");
    }

    public static MockHttpServletRequestBuilder postWithAuth(String uri, String token) {
        return post(uri)
                .header("Authorization", "Bearer " + token)
                .contentType("application/json");
    }

    // 다른 메서드들도 필요에 따라 추가 가능
}
