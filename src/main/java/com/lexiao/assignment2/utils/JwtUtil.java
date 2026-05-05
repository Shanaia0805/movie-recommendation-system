package com.lexiao.assignment2.utils;
import com.lexiao.assignment2.entities.Jwt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static Key key;
    private static Jwt jwt;

    @Autowired
    public void JWTUtil(Jwt jwt) {
        JwtUtil.jwt = jwt;
    }

    public JwtUtil(Jwt jwt) {
        JwtUtil.jwt = jwt;
    }

    @PostConstruct
    public void init() {
        // Note: HS256 requires a secret of at least 256 bits (32 bytes). Ensure the configured secret is long enough.
        JwtUtil.key = Keys.hmacShaKeyFor(jwt.getSecret().getBytes());
    }

    @SuppressWarnings("deprecation")
    public String generateToken(String subject) {
        long now = System.currentTimeMillis();
        long expirationMs = jwt.getExpirationMs();
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @SuppressWarnings("deprecation")
    public boolean validateToken(String token) {
        try {
            @SuppressWarnings("unused")
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;  // Return true if valid
        } catch (JwtException e) {
            return false;  // Return false if invalid or expired
        }
    }
}
