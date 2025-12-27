package com.example.hms.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private byte[] getSigningKeyBytes() {
        return jwtSecret.getBytes(StandardCharsets.UTF_8);
    }

    // Token oluşturma
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(getSigningKeyBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // Token'dan email al
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(getSigningKeyBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Token'dan role al
    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(getSigningKeyBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // Token geçerliliğini kontrol et
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(getSigningKeyBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // log ekleyebilirsin
            // System.out.println("Invalid JWT: " + e.getMessage());
            logger.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }
}
