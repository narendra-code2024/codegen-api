package dev.codegen.api.controller;

import dev.codegen.api.dto.auth.LoginRequest;
import dev.codegen.api.dto.auth.AuthResponse;
import dev.codegen.api.dto.auth.SignupRequest;
import dev.codegen.api.dto.auth.UserResponse;
import dev.codegen.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public UserResponse signup(@Valid @RequestBody SignupRequest data) {
        return this.authService.signup(data);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest data) {
        return this.authService.login(data);
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserDetails userDetails) {
        return this.authService.getCurrentUser(userDetails.getUsername());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }
}
