package com.example.erp.config;

import com.example.erp.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    // Authentication Manager
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    // Security Filter Chain
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            // Không dùng CSRF vì sử dụng JWT
            .csrf(csrf -> csrf.disable())

            // JWT không sử dụng session
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // Phân quyền API
            .authorizeHttpRequests(auth -> auth

                // =========================
                // PUBLIC
                // =========================

                .requestMatchers("/api/auth/**")
                .permitAll()

                .requestMatchers("/uploads/**")
                .permitAll()

                .requestMatchers(
                    "/api/products/**",
                    "/api/categories/**"
                )
                .permitAll()


                // =========================
                // ADMIN
                // =========================

                .requestMatchers(
                    "/api/users/**",
                    "/api/admin/**"
                )
                .hasRole("ADMIN")


                // =========================
                // CART
                // =========================

                .requestMatchers("/api/cart/**")
                .hasAnyRole(
                    "ADMIN",
                    "MANAGER",
                    "SALES",
                    "USER"
                )


                // =========================
                // ORDERS
                // =========================

                .requestMatchers("/api/orders/**")
                .hasAnyRole(
                    "ADMIN",
                    "MANAGER",
                    "SALES",
                    "USER"
                )


                // =========================
                // INVOICES
                // =========================

                .requestMatchers("/api/invoices/**")
                .hasAnyRole(
                    "ADMIN",
                    "MANAGER",
                    "SALES",
                    "USER"
                )


                // =========================
                // CUSTOMERS
                // =========================

                .requestMatchers("/api/customers/**")
                .hasAnyRole(
                    "ADMIN",
                    "MANAGER",
                    "SALES"
                )


                // =========================
                // WAREHOUSE
                // =========================

                .requestMatchers(
                    "/api/inventory/**",
                    "/api/suppliers/**"
                )
                .hasAnyRole(
                    "ADMIN",
                    "MANAGER",
                    "WAREHOUSE"
                )
             // =========================
             // APPROVAL
             // =========================

             .requestMatchers(
                 "/api/request-approves/**",
                 "/api/approvals/**",
                 "/api/approval-lines/**"
             )
             .hasAnyRole(
                 "ADMIN",
                 "MANAGER",
                 "SALES",
                 "USER"
             )

                // =========================
                // CÒN LẠI
                // =========================

                .anyRequest()
                .authenticated()
            )

            // JWT Filter
            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}