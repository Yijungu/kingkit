package com.kingkit.auth_service.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;


/**
 * application.yml / application-*.yml 의 jwt.* 값을
 * 생성자를 통해 불변(immutable)으로 바인딩하는 record 클래스
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        /** Base64-encoded 256~512 bit secret */
        String secret,

        /** 액세스 토큰 유효시간 (ms) */
        long accessTokenValidity,

        /** 리프레시 토큰 유효시간 (ms) */
        long refreshTokenValidity
) {
    /**
     * Spring Boot 3.x에서 record 바인딩을 활성화하려면
     * 반드시 @ConstructorBinding—or 메타 포함—이 필요합니다.
     */
    @ConstructorBinding
    public JwtProperties {}
}
