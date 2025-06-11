package com.udea.gpx;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration-seconds:3600}")
    private long EXPIRATION_TIME;

    public String generateToken(Long userId, boolean isAdmin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("admin", isAdmin);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (EXPIRATION_TIME * 1000))) // Convertir segundos a
                                                                                                // milisegundos
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
            logger.debug("🔍 JwtUtil.validateToken - Iniciando validación de token");
            Claims claims = extractAllClaims(token);
            logger.debug("🔍 JwtUtil.validateToken - Claims extraídos exitosamente");

            Date expiration = claims.getExpiration();
            Date now = new Date();
            boolean isValid = expiration.after(now);

            logger.debug("🔍 JwtUtil.validateToken - Expiración: {}, Ahora: {}, Es válido: {}",
                    expiration, now, isValid);

            return isValid;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.warn("❌ JwtUtil.validateToken - Token expirado: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.security.SecurityException e) {
            logger.warn("❌ JwtUtil.validateToken - Firma inválida: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            logger.warn("❌ JwtUtil.validateToken - Token malformado: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.warn("❌ JwtUtil.validateToken - Error inesperado: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
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