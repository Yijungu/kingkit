package com.kingkit.lib_security.apikey;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class TestableInternalApiKeyFilter extends InternalApiKeyFilter {

    public TestableInternalApiKeyFilter(Set<String> apiKeys, List<String> allowedIps, Predicate<HttpServletRequest> uriMatcher) {
        super(apiKeys, allowedIps, uriMatcher);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        super.doFilterInternal(req, res, chain);
    }
}
