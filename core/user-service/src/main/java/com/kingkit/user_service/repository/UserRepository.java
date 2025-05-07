package com.kingkit.user_service.repository;

import com.kingkit.user_service.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // 이메일로 User 찾기 (로그인, 회원 가입 중복 체크 등에 사용)
    Optional<User> findByEmail(String email);

    // 닉네임으로 User 찾기 (추후 닉네임 중복 검사용)
    Optional<User> findByNickname(String nickname);

    boolean existsByEmail(String email);

}
