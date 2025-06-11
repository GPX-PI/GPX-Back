package com.udea.gpx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Configuración de headers de seguridad HTTP para protección adicional
 */
@Configuration
public class SecurityHeadersConfig {

  @Bean
  public OncePerRequestFilter securityHeadersFilter() {
    return new SecurityHeadersFilter();
  }

  /**
   * Filtro para agregar headers de seguridad HTTP
   */
  public static class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {

      // Strict Transport Security (HSTS) - Forzar HTTPS
      response.setHeader("Strict-Transport-Security",
          "max-age=31536000; includeSubDomains; preload");

      // Content Security Policy - Prevenir XSS
      response.setHeader("Content-Security-Policy",
          "default-src 'self'; " +
              "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://accounts.google.com https://apis.google.com; " +
              "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
              "font-src 'self' https://fonts.gstatic.com; " +
              "img-src 'self' data: https:; " +
              "connect-src 'self' https://accounts.google.com https://www.googleapis.com; " +
              "frame-src 'self' https://accounts.google.com; " +
              "form-action 'self'; " +
              "upgrade-insecure-requests");

      // X-Content-Type-Options - Prevenir MIME type sniffing
      response.setHeader("X-Content-Type-Options", "nosniff");

      // X-Frame-Options - Prevenir clickjacking
      response.setHeader("X-Frame-Options", "DENY");

      // X-XSS-Protection - Activar protección XSS del browser
      response.setHeader("X-XSS-Protection", "1; mode=block");

      // Referrer Policy - Controlar información de referer
      response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

      // Feature Policy / Permissions Policy - Controlar APIs del browser
      response.setHeader("Permissions-Policy",
          "accelerometer=(), " +
              "camera=(), " +
              "geolocation=(), " +
              "gyroscope=(), " +
              "magnetometer=(), " +
              "microphone=(), " +
              "payment=(), " +
              "usb=()");

      // Cross-Origin-Embedder-Policy
      response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");

      // Cross-Origin-Opener-Policy
      response.setHeader("Cross-Origin-Opener-Policy", "same-origin");

      // Cross-Origin-Resource-Policy
      response.setHeader("Cross-Origin-Resource-Policy", "same-origin");

      // Cache Control para endpoints sensibles
      String requestURI = request.getRequestURI();
      if (requestURI.contains("/api/users/login") ||
          requestURI.contains("/api/users/profile") ||
          requestURI.contains("/oauth2/") ||
          requestURI.contains("/actuator/")) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
      }

      // Server header removal (se hace en configuración del servidor)
      response.setHeader("Server", "");

      filterChain.doFilter(request, response);
    }
  }
}