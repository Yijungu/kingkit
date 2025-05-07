package com.kingkit.auth_service.repository;

import com.kingkit.auth_service.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByEmail(String email);

    void deleteByEmail(String email);
}
