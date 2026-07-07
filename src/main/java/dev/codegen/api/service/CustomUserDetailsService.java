package dev.codegen.api.service;

import dev.codegen.api.config.CustomUserDetails;
import dev.codegen.api.entity.User;
import dev.codegen.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

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
}
