package dev.codegen.api.service;

import dev.codegen.api.entity.RefreshToken;
import dev.codegen.api.entity.User;
import dev.codegen.api.exception.InvalidTokenException;
import dev.codegen.api.repository.RefreshTokenRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${security.jwt.refresh-expiration}")
    private Duration refreshExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    public record TokenRotation(User user, String newRefreshToken) {}

    @Transactional
    public String createRefreshToken(User user) {
        // Delete any existing refresh tokens for the user to enforce single session
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plus(refreshExpiration));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    @Transactional
    public TokenRotation rotateToken(String tokenString) {
        RefreshToken token =
                refreshTokenRepository
                        .findByToken(tokenString)
                        .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        verifyExpiration(token);

        User user = token.getUser();
        String newRefreshToken = createRefreshToken(user);
        return new TokenRotation(user, newRefreshToken);
    }

    @Transactional
    public void revokeToken(String tokenString) {
        if (tokenString != null && !tokenString.isBlank()) {
            refreshTokenRepository
                    .findByToken(tokenString)
                    .ifPresent(token -> refreshTokenRepository.deleteByUser(token.getUser()));
        }
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new InvalidTokenException("Refresh token was expired. Please log in again.");
        }
        return token;
    }
}
