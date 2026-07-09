package dev.codegen.api.controller;

import dev.codegen.api.dto.auth.AuthResponse;
import dev.codegen.api.dto.auth.LoginRequest;
import dev.codegen.api.dto.auth.SignupRequest;
import dev.codegen.api.dto.auth.TokenRefreshRequest;
import dev.codegen.api.dto.auth.UserResponse;
import dev.codegen.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(data));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest data) {
        return ResponseEntity.ok(authService.login(data));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody TokenRefreshRequest data) {
        return ResponseEntity.ok(authService.refreshToken(data.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody TokenRefreshRequest data) {
        authService.logout(data.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
