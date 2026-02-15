package com.app.dto;

public class AuthResponse {
    private String token;
    private String tokenType;
    private String userId;
    private String role;

    public AuthResponse(String token, String tokenType, String userId, String role) {
        this.token = token;
        this.tokenType = tokenType;
        this.userId = userId;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}
