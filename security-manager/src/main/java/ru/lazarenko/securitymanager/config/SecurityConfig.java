package ru.lazarenko.securitymanager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.lazarenko.securitymanager.security.AuthFilter;
import ru.lazarenko.securitymanager.security.JwtAccessTokenVerifier;
import ru.lazarenko.securitymanager.security.JwtRefreshTokenVerifier;
import ru.lazarenko.securitymanager.security.JwtService;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    public static final Set<String> whiteListUrls = Set.of("/login", "/api/users/reg", "/api/users/details", "/api/refresh-token", "/api/tokens/validate", "/api/tokens/generate");

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(6);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthenticationManager authManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));

        http
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/login", "/api/users/reg", "/api/refresh-token", "/api/users/details", "/api/tokens/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new AuthFilter(authManager, jwtService, objectMapper))
                .addFilterAfter(new JwtAccessTokenVerifier(jwtService), AuthFilter.class)
                .addFilterBefore(new JwtRefreshTokenVerifier(jwtService, objectMapper), AuthFilter.class)
                .formLogin()
                .disable();

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfig) throws Exception {
        return authenticationConfig.getAuthenticationManager();
    }

}
