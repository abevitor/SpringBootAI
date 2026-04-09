package com.example.SpringBootAI.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

public class SecurityConfig extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        
        if (uri.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

       
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            String username = JwtUtil.extractUsername(token);

            System.out.println("USER: " + username);

            request.setAttribute("username", username);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido");
            return;
        }

        filterChain.doFilter(request, response);
    }
}