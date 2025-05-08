package com.kingkit.lib_test_support.testsupport.util;

import feign.FeignException;
import feign.Request;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FeignExceptionFactory {

    public static FeignException notFound(String uri) {
        return new FeignException.NotFound(
                "404 Not Found",
                Request.create(Request.HttpMethod.GET, uri, Map.of(), null, StandardCharsets.UTF_8, null),
                null, null
        );
    }

    public static FeignException internalServerError(String uri) {
        return new FeignException.InternalServerError(
                "500 Internal Server Error",
                Request.create(Request.HttpMethod.GET, uri, Map.of(), null, StandardCharsets.UTF_8, null),
                null, null
        );
    }
}
