package dev.codegen.api.service;

import dev.codegen.api.dto.auth.LoginRequest;
import dev.codegen.api.dto.auth.AuthResponse;
import dev.codegen.api.dto.auth.SignupRequest;
import dev.codegen.api.dto.auth.UserResponse;
import dev.codegen.api.entity.User;
import dev.codegen.api.mapper.UserMapper;
import dev.codegen.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserResponse signup(SignupRequest req) {
        User user = userMapper.toEntity(req);
        user.setPassword(passwordEncoder.encode(req.password()));
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(java.util.Collections.emptyList())
                .build();

        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token);
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return userMapper.toResponse(user);
    }
}
