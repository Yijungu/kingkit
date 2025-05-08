package com.kingkit.lib_test_support.testsupport.util;

import com.kingkit.lib_security.jwt.JwtTokenProvider;
import com.kingkit.lib_security.jwt.JwtProperties;

public class JwtTestUtil {

    public static JwtTokenProvider createTokenProviderForTests() {
        JwtProperties props = new JwtProperties(
            "test-jwt-secret-key-that-is-long-enough-for-hmac", // secret
            3600L,  // access token: 1시간
            86400L  // refresh token: 1일
        );
        return new JwtTokenProvider(props);
    }
}
