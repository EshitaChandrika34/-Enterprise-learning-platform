package com.enterpriselearning.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    private final JwtAuthenticationFilter authenticationFilter;

    // ============================================================
    // PASSWORD ENCODER
    // ============================================================

    @Bean
    public static PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // ============================================================
    // AUTHENTICATION MANAGER
    // ============================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    // ============================================================
    // SECURITY FILTER CHAIN
    // ============================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

                // ------------------------------------------------
                // CORS
                // ------------------------------------------------
                .cors(
                        Customizer.withDefaults()
                )

                // ------------------------------------------------
                // CSRF
                // ------------------------------------------------
                .csrf(
                        AbstractHttpConfigurer::disable
                )

                // ------------------------------------------------
                // FRAME OPTIONS
                // ------------------------------------------------
                .headers(
                        headers ->
                                headers.frameOptions(
                                        HeadersConfigurer
                                                .FrameOptionsConfig
                                                ::disable
                                )
                )

                // ------------------------------------------------
                // EXCEPTION HANDLING
                // ------------------------------------------------
                .exceptionHandling(
                        exception ->
                                exception.authenticationEntryPoint(
                                        authenticationEntryPoint
                                )
                )

                // ------------------------------------------------
                // JWT = STATELESS
                // ------------------------------------------------
                .sessionManagement(
                        session ->
                                session.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS
                                )
                )

                // ------------------------------------------------
                // AUTHORIZATION
                // ------------------------------------------------
                .authorizeHttpRequests(
                        authorize -> authorize

                                // ==================================
                                // AUTHENTICATION
                                // ==================================

                                .requestMatchers(
                                        "/api/auth/**"
                                )
                                .permitAll()

                                // ==================================
                                // H2 CONSOLE
                                // ==================================

                                .requestMatchers(
                                        "/h2-console/**"
                                )
                                .permitAll()

                                // ==================================
                                // PUBLIC SKILL GET REQUESTS
                                // ==================================

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/skills/**"
                                )
                                .permitAll()

                                // ==================================
                                // PUBLIC COURSE GET REQUESTS
                                // ==================================

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/learning/courses/**"
                                )
                                .permitAll()

                                // ==================================
                                // PUBLIC CAREER PATHS
                                // ==================================

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/career/paths/**"
                                )
                                .permitAll()

                                // ==================================
                                // PUBLIC JOB GET REQUESTS
                                // ==================================

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/jobs/**"
                                )
                                .permitAll()

                                // ==================================
                                // ORGANIZATION ANALYTICS
                                //
                                // ADMIN + HR ONLY
                                // ==================================

                                .requestMatchers(
                                        "/api/analytics/**"
                                )
                                .hasAnyRole(
                                        "ADMIN",
                                        "HR"
                                )

                                // ==================================
                                // EVERYTHING ELSE
                                //
                                // Valid JWT required
                                // ==================================

                                .anyRequest()
                                .authenticated()
                );

        // ============================================================
        // JWT FILTER
        // ============================================================

        http.addFilterBefore(
                authenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}