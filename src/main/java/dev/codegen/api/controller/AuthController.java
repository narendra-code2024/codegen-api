package dev.codegen.api.controller;

import dev.codegen.api.dto.auth.AuthResponse;
import dev.codegen.api.dto.auth.LoginRequest;
import dev.codegen.api.dto.auth.SignupRequest;
import dev.codegen.api.dto.auth.TokenRefreshRequest;
import dev.codegen.api.dto.auth.UserResponse;
import dev.codegen.api.exception.InvalidTokenException;
import dev.codegen.api.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${security.jwt.refresh-expiration}")
    private Duration refreshExpiration;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    private static final String REFRESH_TOKEN_COOKIE_PATH = "/api/auth";

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest data) {
        UserResponse response = this.authService.signup(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest data,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            HttpServletResponse response) {
        AuthResponse authResponse = this.authService.login(data);
        AuthResponse responseBody = prepareTokenResponse(authResponse, clientType, response);
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody(required = false) TokenRefreshRequest data,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType,
            @CookieValue(value = "refresh_token", required = false) String refreshTokenCookie,
            HttpServletResponse response) {
        String refreshToken = extractRefreshToken(data, refreshTokenCookie);
        AuthResponse authResponse = this.authService.refreshToken(refreshToken);
        AuthResponse responseBody = prepareTokenResponse(authResponse, clientType, response);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = this.authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody(required = false) TokenRefreshRequest data,
            @CookieValue(value = "refresh_token", required = false) String refreshTokenCookie,
            HttpServletResponse response) {
        try {
            String refreshToken = extractRefreshToken(data, refreshTokenCookie);
            this.authService.logout(refreshToken);
        } catch (Exception e) {
            // Graceful logout: if the token is already missing or invalid, we still want to
            // clear the client's cookies and local state.
        }
        clearCookies(response);
        return ResponseEntity.noContent().build();
    }

    private void setRefreshTokenCookie(
            HttpServletResponse response, String value, Duration maxAge) {
        ResponseCookie cookie =
                ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, value)
                        .httpOnly(true)
                        .secure(true)
                        .path(REFRESH_TOKEN_COOKIE_PATH)
                        .maxAge(maxAge)
                        .sameSite("Strict")
                        .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearCookies(HttpServletResponse response) {
        setRefreshTokenCookie(response, "", Duration.ZERO);
    }

    private String extractRefreshToken(TokenRefreshRequest data, String refreshTokenCookie) {
        if (refreshTokenCookie != null && !refreshTokenCookie.isBlank()) {
            return refreshTokenCookie;
        }

        if (data != null && data.refreshToken() != null && !data.refreshToken().isBlank()) {
            return data.refreshToken();
        }

        throw new InvalidTokenException("Refresh token is missing");
    }

    private AuthResponse prepareTokenResponse(
            AuthResponse authResponse, String clientType, HttpServletResponse response) {
        if ("web".equalsIgnoreCase(clientType)) {
            setRefreshTokenCookie(response, authResponse.refreshToken(), refreshExpiration);
            return new AuthResponse(authResponse.accessToken(), null);
        }
        return authResponse;
    }
}
