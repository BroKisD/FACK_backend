package com.app.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthCookies {

    public static final String REFRESH_COOKIE_NAME = "refresh_token";

    private final boolean cookieSecure;
    private final String sameSite;
    private final String cookiePath;

    public AuthCookies(
            @Value("${security.auth.cookie-secure:${COOKIE_SECURE:false}}") boolean cookieSecure,
            @Value("${security.auth.cookie-samesite:Lax}") String sameSite,
            @Value("${security.auth.cookie-path:/api/auth}") String cookiePath) {
        this.cookieSecure = cookieSecure;
        this.sameSite = sameSite;
        this.cookiePath = cookiePath;
    }

    public void setRefreshCookie(HttpServletResponse response, String refreshToken, Duration ttl) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(sameSite)
                .path(cookiePath)
                .maxAge(ttl)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(sameSite)
                .path(cookiePath)
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
