package dev.codegen.api.service;

import dev.codegen.api.dto.auth.AuthResponse;
import dev.codegen.api.dto.auth.LoginRequest;
import dev.codegen.api.dto.auth.SignupRequest;
import dev.codegen.api.dto.auth.UserResponse;
import dev.codegen.api.entity.User;
import dev.codegen.api.exception.DuplicateResourceException;
import dev.codegen.api.exception.ResourceNotFoundException;
import dev.codegen.api.mapper.UserMapper;
import dev.codegen.api.repository.UserRepository;
import dev.codegen.api.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;

    private final AuthUtil authUtil;

    public UserResponse signup(SignupRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new DuplicateResourceException("Email is already registered");
        }

        User user = userMapper.toEntity(req);
        user.setPassword(passwordEncoder.encode(req.password()));
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));

        User user =
                userRepository
                        .findByEmail(req.email())
                        .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        String refreshToken = refreshTokenService.createRefreshToken(user);
        String accessToken = authUtil.generateAccessToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(String token) {
        RefreshTokenService.TokenRotation rotation = refreshTokenService.rotateToken(token);
        String accessToken = authUtil.generateAccessToken(rotation.user());
        return new AuthResponse(accessToken, rotation.newRefreshToken());
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    public UserResponse getCurrentUser() {
        User user =
                userRepository
                        .findById(authUtil.getCurrentUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }
}
