package dev.codegen.api.service;

import dev.codegen.api.entity.User;
import dev.codegen.api.repository.UserRepository;
import dev.codegen.api.security.CustomUserDetails;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // The username identifier in our system is the user's email
        User user =
                userRepository
                        .findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Bad credentials"));

        return new CustomUserDetails(user);
    }

    public User getReferenceById(UUID id) {
        return userRepository.getReferenceById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
