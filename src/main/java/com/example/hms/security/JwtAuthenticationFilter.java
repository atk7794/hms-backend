package com.example.hms.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization header’ı al
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        String role = null;

        // "Bearer " ile başlıyorsa token’ı çıkar
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                if (jwtUtil.validateToken(token)) {
                    email = jwtUtil.getEmailFromToken(token);
                    role = jwtUtil.getRoleFromToken(token);
                }
            } catch (Exception ex) {
                // System.out.println("⚠️ Geçersiz JWT: " + ex.getMessage());
                logger.warn("Geçersiz JWT: {}", ex.getMessage());
            }
        }

        // Eğer token geçerliyse ve context boşsa
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Yetkileri oluştur
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
