package com.example.SpringBootAI.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // Injeta o JwtFilter que já criamos
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // sem sessão, pois usamos JWT
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()  // rotas públicas
                .anyRequest().authenticated()             // todo o resto exige autenticação
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // registra o JwtFilter

        return http.build();
    }
}