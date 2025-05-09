package com.kingkit.auth_service.domain;

import java.util.Objects;

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
    private String email;
    private String token;
    private String role;

    public void update(String newToken) {
        this.token = newToken;
    }

    public boolean isTokenMatch(String providedToken) {
        return Objects.equals(this.token, providedToken);
    }
}

