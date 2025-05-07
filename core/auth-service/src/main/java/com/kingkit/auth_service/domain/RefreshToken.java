package com.kingkit.auth_service.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    private String email; // 유저 식별자 (이메일)

    @Column(nullable = false, length = 1000)
    private String token;

    public void update(String newToken) {
        this.token = newToken;
    }
}
