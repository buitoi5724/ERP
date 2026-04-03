package com.example.erp.security;

import java.security.Key;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // ⚠️ Key phải >= 32 bytes cho HS256
    private static final String SECRET =
            "ERP_SECRET_KEY_12345678901234567890";

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ================== LOGIN ==================
    // Sinh JWT khi login
    public String generateToken(Authentication authentication) {

        return Jwts.builder()
                .setSubject(authentication.getName()) // username
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 86400000) // 1 ngày
                )
                .signWith(getSignKey())
                .compact();
    }

    // ================== JWT FILTER ==================
    // Lấy username từ token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Kiểm tra token còn hạn không
    public boolean isTokenValid(String token) {
        try {
            return extractAllClaims(token)
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // ================== INTERNAL ==================
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
