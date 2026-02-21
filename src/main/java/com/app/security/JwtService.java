package com.app.security;

import com.app.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final String accessSecret;
    private final String refreshSecret;
    private final String accessTtlRaw;
    private final String refreshTtlRaw;

    private Duration accessTtl;
    private Duration refreshTtl;

    public JwtService(
            @Value("${security.jwt.access-secret:${JWT_ACCESS_SECRET:${security.jwt.secret}}}") String accessSecret,
            @Value("${security.jwt.refresh-secret:${JWT_REFRESH_SECRET:${security.jwt.secret}}}") String refreshSecret,
            @Value("${security.jwt.access-ttl:${ACCESS_TTL:15m}}") String accessTtlRaw,
            @Value("${security.jwt.refresh-ttl:${REFRESH_TTL:7d}}") String refreshTtlRaw) {
        this.accessSecret = accessSecret;
        this.refreshSecret = refreshSecret;
        this.accessTtlRaw = accessTtlRaw;
        this.refreshTtlRaw = refreshTtlRaw;
    }

    @PostConstruct
    public void validateConfig() {
        if (accessSecret == null || accessSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT access secret must be at least 32 characters");
        }
        if (refreshSecret == null || refreshSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT refresh secret must be at least 32 characters");
        }
        this.accessTtl = parseTtl(accessTtlRaw);
        this.refreshTtl = parseTtl(refreshTtlRaw);
    }

    public String signAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(accessTtl);

        return Jwts.builder()
                .setSubject(user.getId())
                .claim("email", user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(getSigningKey(accessSecret), SignatureAlgorithm.HS256)
                .compact();
    }

    public String signRefreshToken(String userId, String sessionId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(refreshTtl);

        return Jwts.builder()
                .setSubject(userId)
                .setId(sessionId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(getSigningKey(refreshSecret), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims verifyAccessToken(String token) {
        return parseClaims(token, accessSecret);
    }

    public Claims verifyRefreshToken(String token) {
        return parseClaims(token, refreshSecret);
    }

    public String extractUserIdFromAccessToken(String token) {
        return verifyAccessToken(token).getSubject();
    }

    public boolean isAccessTokenValid(String token) {
        try {
            verifyAccessToken(token);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public Instant getRefreshTokenExpiryInstant() {
        return Instant.now().plus(refreshTtl);
    }

    public Duration getRefreshTtl() {
        return refreshTtl;
    }

    private Claims parseClaims(String token, String secret) {
        return Jwts.parser()
                .verifyWith(getSigningKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Duration parseTtl(String raw) {
        if (raw == null || raw.isBlank()) return Duration.ofMinutes(15);
        String value = raw.trim().toLowerCase();
        if (value.endsWith("ms")) return Duration.ofMillis(Long.parseLong(value.substring(0, value.length() - 2)));
        if (value.endsWith("s")) return Duration.ofSeconds(Long.parseLong(value.substring(0, value.length() - 1)));
        if (value.endsWith("m")) return Duration.ofMinutes(Long.parseLong(value.substring(0, value.length() - 1)));
        if (value.endsWith("h")) return Duration.ofHours(Long.parseLong(value.substring(0, value.length() - 1)));
        if (value.endsWith("d")) return Duration.ofDays(Long.parseLong(value.substring(0, value.length() - 1)));
        return Duration.ofSeconds(Long.parseLong(value));
    }
}
