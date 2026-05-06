package com.lexiao.assignment2;

import com.lexiao.assignment2.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    private static final Set<String> ALLOWED_PATHS = Set.of(
            "/v1/healthcheck",
            "/v1/login",
            "/v1/register",
            "/v1/rating",
            "/error"
    );

    @Autowired
    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String requestPath = request.getRequestURI();

        // Allow requests to specific public endpoints
        if (ALLOWED_PATHS.contains(requestPath) || requestPath.startsWith("/v1/movie/")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            respondWithUnauthorized(response, "Missing or invalid Authorization header. Requested URI: " + requestPath);
            return false;
        }

        String token = authHeader.substring(7);
        try {
            jwtUtil.validateToken(token);
        } catch (JwtException ex) {
            respondWithUnauthorized(response, "Invalid or expired token.");
            return false;
        }

        return true;
    }

    private void respondWithUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
