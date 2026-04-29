package com.revpay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Activates @PreAuthorize GLOBALLY
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Completely Public Endpoints (Auth (Partial) + Static Resources)
                        .requestMatchers("/auth/login", "/auth/register", "/auth/forgot-password/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                        // 2. The HTML Page Routes - STRICTLY RESTRICTED TO 'GET' ONLY
                        .requestMatchers(HttpMethod.GET, "/", "/login", "/register", "/dashboard",
                                "/transactions", "/invoices", "/loans",
                                "/notifications", "/profile", "/requests",
                                "/payment-methods", "/analytics").permitAll()

                        // 3. EVERYTHING ELSE (Actual API data endpoints, POSTs, PUTs, etc.) requires a valid JWT
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}