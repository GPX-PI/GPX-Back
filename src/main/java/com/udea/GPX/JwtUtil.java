package com.udea.GPX;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "clave_secreta_super_segura_1234567890";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 horas

    public String generateToken(Long userId, boolean isAdmin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("admin", isAdmin);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            System.out.println("🔍 JwtUtil.validateToken - Iniciando validación de token");
            Claims claims = extractAllClaims(token);
            System.out.println("🔍 JwtUtil.validateToken - Claims extraídos exitosamente");

            Date expiration = claims.getExpiration();
            Date now = new Date();
            boolean isValid = expiration.after(now);

            System.out.println("🔍 JwtUtil.validateToken - Expiración: " + expiration);
            System.out.println("🔍 JwtUtil.validateToken - Ahora: " + now);
            System.out.println("🔍 JwtUtil.validateToken - Es válido: " + isValid);

            return isValid;
        } catch (Exception e) {
            System.out.println(
                    "❌ JwtUtil.validateToken - Error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Long extractUserId(String token) {
        return Long.parseLong(extractAllClaims(token).get("userId").toString());
    }

    public boolean extractIsAdmin(String token) {
        return Boolean.parseBoolean(extractAllClaims(token).get("admin").toString());
    }
}