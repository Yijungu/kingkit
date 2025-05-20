package com.kingkit.lib_test_support.testsupport.util;

import com.kingkit.lib_security.jwt.JwtTokenProvider;

/**
 * 테스트용 JWT 토큰 발급 유틸리티
 *
 * <p>공유 테스트 라이브러리에서 사용되며, 테스트 목적의 JWT 토큰을 생성합니다.
 * 실제 서비스 코드에서는 사용하지 않습니다.</p>
 */
public final class JwtTestTokenProvider {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTestTokenProvider(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 일반 유저용 JWT 생성 (ROLE_USER)
     */
    public String generateUserToken(Long userId) {
        return jwtTokenProvider.createAccessToken("user-" + userId, "ROLE_USER");
    }

    /**
     * 관리자용 JWT 생성 (ROLE_ADMIN)
     */
    public String generateAdminToken(Long userId) {
        return jwtTokenProvider.createAccessToken("admin-" + userId, "ROLE_ADMIN");
    }

    /**
     * 커스텀 Role 기반 JWT 생성
     */
    public String generateToken(Long userId, String role) {
        return jwtTokenProvider.createAccessToken("user-" + userId, role);
    }
}
