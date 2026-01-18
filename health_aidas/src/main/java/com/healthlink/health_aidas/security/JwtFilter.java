package com.healthlink.health_aidas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Header se 'Authorization' nikalo
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Debug: Console mein dekho request aa rahi hai ya nahi (Mac Terminal)
        System.out.println("DEBUG: Request URI: " + request.getRequestURI());

        // 2. Check karo ki header "Bearer " se start ho raha hai
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // "Bearer " ke baad wala token

            try {
                if (jwtUtil.validateToken(token)) {
                    username = jwtUtil.extractUsername(token);
                    System.out.println("DEBUG: Token Validated for User: " + username);
                } else {
                    System.out.println("DEBUG: Token Validation FAILED!");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Error validating token: " + e.getMessage());
            }
        } else {
            // Agar chat API call hai aur header nahi mila
            if (request.getRequestURI().contains("/api/ai/")) {
                System.out.println("DEBUG: No Authorization Header found for AI API call!");
            }
        }

        // 3. User ko authenticate karo
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}