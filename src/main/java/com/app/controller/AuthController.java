package com.app.controller;

import com.app.dto.AuthRequest;
import com.app.dto.AuthResponse;
import com.app.dto.UserDTO;
import com.app.entity.User;
import com.app.security.AuthService;
import com.app.security.AuthCookies;
import com.app.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthCookies authCookies;

    public AuthController(AuthService authService, JwtService jwtService, AuthCookies authCookies) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.authCookies = authCookies;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        AuthService.AuthResult result = authService.login(
                request.getEmail(),
                request.getPassword(),
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent")
        );

        User user = result.user();
        authCookies.setRefreshCookie(httpResponse, result.refreshToken(), jwtService.getRefreshTtl());

        AuthResponse response = new AuthResponse(
                result.accessToken(),
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String refreshToken = readRefreshCookie(httpRequest).orElse(null);
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        final AuthService.AuthResult result;
        try {
            result = authService.refresh(
                    refreshToken,
                    httpRequest.getRemoteAddr(),
                    httpRequest.getHeader("User-Agent")
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = result.user();
        authCookies.setRefreshCookie(httpResponse, result.refreshToken(), jwtService.getRefreshTtl());

        AuthResponse response = new AuthResponse(
                result.accessToken(),
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        readRefreshCookie(httpRequest).ifPresent(authService::logout);
        authCookies.clearRefreshCookie(httpResponse);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = jwtService.extractUserIdFromAccessToken(authHeader.substring(7));
        User user = authService.getUserById(userId);

        UserDTO dto = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                null,
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
        return ResponseEntity.ok(dto);
    }

    private Optional<String> readRefreshCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return Optional.empty();

        return Arrays.stream(cookies)
                .filter(cookie -> AuthCookies.REFRESH_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
