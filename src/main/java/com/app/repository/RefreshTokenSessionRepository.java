package com.app.repository;

import com.app.entity.RefreshTokenSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenSessionRepository extends JpaRepository<RefreshTokenSession, String> {
    Optional<RefreshTokenSession> findByIdAndUserIdAndRevokedAtIsNull(String id, String userId);
    Optional<RefreshTokenSession> findByUserIdAndTokenHashAndRevokedAtIsNull(String userId, String tokenHash);
}
