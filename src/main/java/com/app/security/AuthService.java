package com.app.security;

import com.app.entity.User;
import com.app.entity.RefreshTokenSession;
import com.app.repository.RefreshTokenSessionRepository;
import com.app.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenHashUtil tokenHashUtil;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenSessionRepository refreshTokenSessionRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            TokenHashUtil tokenHashUtil) {
        this.userRepository = userRepository;
        this.refreshTokenSessionRepository = refreshTokenSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenHashUtil = tokenHashUtil;
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordMatches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return user;
    }

    public AuthResult login(String email, String password, String ipAddress, String userAgent) {
        User user = authenticate(email, password);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String sessionId = UUID.randomUUID().toString();
        String refreshToken = jwtService.signRefreshToken(user.getId(), sessionId);

        createRefreshSession(user.getId(), sessionId, refreshToken, ipAddress, userAgent, jwtService.getRefreshTokenExpiryInstant());

        String accessToken = jwtService.signAccessToken(user);
        return new AuthResult(accessToken, refreshToken, user);
    }

    public AuthResult refresh(String refreshToken, String ipAddress, String userAgent) {
        var claims = jwtService.verifyRefreshToken(refreshToken);
        String userId = claims.getSubject();
        String sessionId = claims.getId();
        if (userId == null || sessionId == null) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        String tokenHash = tokenHashUtil.hashToken(refreshToken);
        RefreshTokenSession session = refreshTokenSessionRepository
                .findByIdAndUserIdAndRevokedAtIsNull(sessionId, userId)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh session"));

        if (!tokenHash.equals(session.getTokenHash())) {
            throw new BadCredentialsException("Invalid refresh session");
        }

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            session.setRevokedAt(LocalDateTime.now());
            refreshTokenSessionRepository.save(session);
            throw new BadCredentialsException("Refresh session expired");
        }

        session.setRevokedAt(LocalDateTime.now());
        refreshTokenSessionRepository.save(session);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        String newSessionId = UUID.randomUUID().toString();
        String newRefreshToken = jwtService.signRefreshToken(userId, newSessionId);
        createRefreshSession(userId, newSessionId, newRefreshToken, ipAddress, userAgent, jwtService.getRefreshTokenExpiryInstant());

        String newAccessToken = jwtService.signAccessToken(user);
        return new AuthResult(newAccessToken, newRefreshToken, user);
    }

    public void logout(String refreshToken) {
        try {
            var claims = jwtService.verifyRefreshToken(refreshToken);
            String userId = claims.getSubject();
            String sessionId = claims.getId();
            if (userId == null || sessionId == null) return;

            refreshTokenSessionRepository.findByIdAndUserIdAndRevokedAtIsNull(sessionId, userId)
                    .ifPresent(session -> {
                        session.setRevokedAt(LocalDateTime.now());
                        refreshTokenSessionRepository.save(session);
                    });
        } catch (Exception ignored) {
            // Logout should be idempotent and safe even if token is invalid/expired.
        }
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
    }

    private void createRefreshSession(
            String userId,
            String sessionId,
            String refreshToken,
            String ipAddress,
            String userAgent,
            Instant expiresAt) {
        RefreshTokenSession session = new RefreshTokenSession();
        session.setId(sessionId);
        session.setUserId(userId);
        session.setTokenHash(tokenHashUtil.hashToken(refreshToken));
        session.setExpiresAt(LocalDateTime.ofInstant(expiresAt, ZoneOffset.UTC));
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        refreshTokenSessionRepository.save(session);
    }

    private boolean passwordMatches(String rawPassword, String storedHash) {
        if (storedHash == null || storedHash.isBlank()) {
            return false;
        }
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedHash);
        }
        return storedHash.equals(rawPassword);
    }

    public record AuthResult(String accessToken, String refreshToken, User user) {}
}
