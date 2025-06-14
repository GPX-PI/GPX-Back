package com.udea.gpx.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración avanzada de JWT con refresh tokens y gestión de sesiones
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

  private String secret = "tu-clave-secreta-super-larga-y-segura-para-jwt-tokens";
  private long expirationSeconds = 36000; // 10 horas por defecto
  private long refreshExpirationSeconds = 604800; // 7 días por defecto
  private int maxConcurrentSessions = 5; // Máximo de sesiones concurrentes
  private boolean allowRefreshTokenRotation = true; // Rotar refresh tokens
  private boolean enableBlacklist = true; // Habilitar blacklist de tokens
  private long sessionTimeoutSeconds = 7200; // 2 horas de inactividad por defecto
  private long maxSessionDurationSeconds = 28800; // 8 horas máximo por defecto

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public long getExpirationSeconds() {
    return expirationSeconds;
  }

  public void setExpirationSeconds(long expirationSeconds) {
    this.expirationSeconds = expirationSeconds;
  }

  public long getRefreshExpirationSeconds() {
    return refreshExpirationSeconds;
  }

  public void setRefreshExpirationSeconds(long refreshExpirationSeconds) {
    this.refreshExpirationSeconds = refreshExpirationSeconds;
  }

  public int getMaxConcurrentSessions() {
    return maxConcurrentSessions;
  }

  public void setMaxConcurrentSessions(int maxConcurrentSessions) {
    this.maxConcurrentSessions = maxConcurrentSessions;
  }

  public boolean isAllowRefreshTokenRotation() {
    return allowRefreshTokenRotation;
  }

  public void setAllowRefreshTokenRotation(boolean allowRefreshTokenRotation) {
    this.allowRefreshTokenRotation = allowRefreshTokenRotation;
  }

  public boolean isEnableBlacklist() {
    return enableBlacklist;
  }

  public void setEnableBlacklist(boolean enableBlacklist) {
    this.enableBlacklist = enableBlacklist;
  }

  public long getSessionTimeoutSeconds() {
    return sessionTimeoutSeconds;
  }

  public void setSessionTimeoutSeconds(long sessionTimeoutSeconds) {
    this.sessionTimeoutSeconds = sessionTimeoutSeconds;
  }

  public long getMaxSessionDurationSeconds() {
    return maxSessionDurationSeconds;
  }

  public void setMaxSessionDurationSeconds(long maxSessionDurationSeconds) {
    this.maxSessionDurationSeconds = maxSessionDurationSeconds;
  }
}