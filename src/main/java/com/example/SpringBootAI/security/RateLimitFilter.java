package com.example.SpringBootAI.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        return Bucket.builder()
                     .addLimit(limit)
                     .build();
    }

       private Bucket getBucket(String key) {
        return buckets.computeIfAbsent(key, k -> createBucket());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) 
                                    throws ServletException, IOException {
                                        String uri = request.getRequestURI();

                                        if(!uri.startsWith("/api/")) {
                                            filterChain.doFilter(request, response);
                                            return;
                                        }

                String username = (String) request.getAttribute("username");
                String key = username != null ? username: request.getRemoteAddr();

                Bucket bucket = getBucket(key);

                if(bucket.tryConsume(1)){
                    
                    response.addHeader("X-RateLimit-Remaining",
                    String.valueOf(bucket.getAvailableTokens()));

                    filterChain.doFilter(request, response);


                           
                }else {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\\\"error\\\": \\\"Muitas requisições. Tente novamente em instantes.\\\", \\\"status\\\": 429}");
                }
                                    }
    
}
