package com.example.SpringBootAI.service;

import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.example.SpringBootAI.security.JwtUtil;

public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);
            String username = JwtUtil.extractUsername(token);

            request.setAttribute("username", username);
        }

        filterChain.doFilter(request, response);
    }
}
