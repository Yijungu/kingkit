package com.kingkit.lib_test_support.testsupport.assertion;

import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ErrorAssert {

    /**
     * 지정된 에러 코드와 함께 400 BAD_REQUEST를 기대하는 검증
     */
    public static ResultMatcher badRequestWithCode(String expectedCode) {
        return result -> {
            status().isBadRequest().match(result);
            jsonPath("$.code").value(expectedCode).match(result);
        };
    }

    // 만약 다른 에러 유형도 추가하고 싶다면 아래처럼 확장 가능
    public static ResultMatcher unauthorizedWithCode(String expectedCode) {
        return result -> {
            status().isUnauthorized().match(result);
            jsonPath("$.code").value(expectedCode).match(result);
        };
    }
}
