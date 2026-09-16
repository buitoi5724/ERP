package com.example.erp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        System.out.println("===== JWT FILTER =====");
        System.out.println("METHOD: " + request.getMethod());
        System.out.println("PATH: " + path);

        // ==============================
        // PUBLIC AUTH API
        // ==============================

        if (path.startsWith("/api/auth/")) {

            System.out.println("PUBLIC AUTH API");

            filterChain.doFilter(request, response);
            return;
        }

        // ==============================
        // LẤY AUTHORIZATION HEADER
        // ==============================

        String authHeader = request.getHeader("Authorization");

        System.out.println("Authorization: " + authHeader);

        // Không có JWT
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            System.out.println("❌ KHÔNG CÓ JWT");

            filterChain.doFilter(request, response);
            return;
        }

        try {

            // ==============================
            // LẤY JWT
            // ==============================

            String jwt = authHeader.substring(7);

            System.out.println("✅ Có JWT");

            // ==============================
            // LẤY USERNAME
            // ==============================

            String username =
                    jwtService.extractUsername(jwt);

            System.out.println("USERNAME: " + username);

            // ==============================
            // KIỂM TRA AUTHENTICATION
            // ==============================

            if (username != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);

                // ==============================
                // DEBUG ROLE
                // ==============================

                System.out.println(
                        "AUTHORITIES: "
                                + userDetails.getAuthorities()
                );

                // ==============================
                // KIỂM TRA TOKEN
                // ==============================

                boolean valid =
                        jwtService.isTokenValid(jwt);

                System.out.println(
                        "TOKEN VALID: " + valid
                );

                if (valid) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);

                    System.out.println(
                            "✅ AUTHENTICATION SUCCESS"
                    );

                } else {

                    System.out.println(
                            "❌ TOKEN KHÔNG HỢP LỆ"
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "❌ JWT ERROR: "
                            + e.getMessage()
            );
        }

        System.out.println("======================");

        // Cho request tiếp tục
        filterChain.doFilter(request, response);
    }
}