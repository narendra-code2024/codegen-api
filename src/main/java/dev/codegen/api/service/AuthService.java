package dev.codegen.api.service;

import dev.codegen.api.dto.auth.RegisterRequest;
import dev.codegen.api.dto.auth.UserResponse;
import dev.codegen.api.entity.User;
import dev.codegen.api.mapper.UserMapper;
import dev.codegen.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    public UserResponse register(RegisterRequest req) {
        User user = userMapper.toEntity(req);
        // TODO: Implement password hashing when Spring Security is added
        userRepository.save(user);
        return userMapper.toResponse(user);
    }
}
