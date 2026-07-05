package dev.codegen.api.config;

import dev.codegen.api.filter.JwtAuthenticationFilter;
import dev.codegen.api.handler.SecurityExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final SecurityExceptionHandler securityExceptionHandler;

    @Bean
    @SuppressWarnings("java:S4502") // API is stateless and uses Bearer tokens
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(
                auth ->
                        auth.requestMatchers(
                                        "/api/auth/signup",
                                        "/api/auth/login",
                                        "/api/auth/refresh",
                                        "/api/auth/logout")
                                .permitAll()
                                .anyRequest()
                                .authenticated());

        http.exceptionHandling(
                ex ->
                        ex.authenticationEntryPoint(securityExceptionHandler)
                                .accessDeniedHandler(securityExceptionHandler));

        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authenticationProvider(authenticationProvider);
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
