package com.kingkit.lib_test_support.testsupport.util;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.http.MediaType;

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

    public static MockHttpServletRequestBuilder postJson(String url, Object body, ObjectMapper objectMapper) throws Exception {
        return MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
    }

    public static MockHttpServletRequestBuilder getJson(String url, ObjectMapper objectMapper) {
        return MockMvcRequestBuilders
                .get(url)
                .accept(MediaType.APPLICATION_JSON);
    }
}
