package com.cykj.psychology.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    private final byte[] secret;
    private final long expireMillis;

    public JwtUtils(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expire-millis:86400000}") long expireMillis) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("security.jwt.secret must contain at least 32 characters");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expireMillis = expireMillis;
    }

    public String generateToken(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireMillis))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        Object value = claims.get("userId");
        if (!(value instanceof Number)) {
            throw new IllegalArgumentException("Token does not contain a valid userId");
        }
        return ((Number) value).longValue();
    }

    public String getRole(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
