package com.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_token_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenSession {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "token_hash", length = 128, nullable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
