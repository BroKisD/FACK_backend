package com.app.controller;

import com.app.dto.AuthRequest;
import com.app.dto.AuthResponse;
import com.app.entity.User;
import com.app.security.AuthService;
import com.app.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        User user = authService.authenticate(request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(user);
        AuthResponse response = new AuthResponse(token, "Bearer", user.getId(), user.getRole());
        return ResponseEntity.ok(response);
    }
}
