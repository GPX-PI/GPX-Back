package com.udea.GPX.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Cliente simple para servicios externos con protección Circuit Breaker
 * Enfoque minimalista sin over-engineering
 */
@Service
public class ExternalServiceClient {

  private static final Logger logger = LoggerFactory.getLogger(ExternalServiceClient.class);

  private final RestTemplate restTemplate;

  public ExternalServiceClient() {
    this.restTemplate = new RestTemplate();
  }

  /**
   * Verificar disponibilidad de Google OAuth2 con circuit breaker
   */
  @CircuitBreaker(name = "oauth2", fallbackMethod = "fallbackOAuth2Health")
  public boolean checkGoogleOAuth2Health() {
    logger.debug("🔍 Verificando disponibilidad de Google OAuth2...");

    try {
      // Verificación simple con token inválido para probar conectividad
      String url = "https://www.googleapis.com/oauth2/v2/tokeninfo?access_token=invalid";
      ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

      // Si responde (aunque sea error), el servicio está disponible
      logger.debug("✅ Google OAuth2 responde correctamente");
      return true;

    } catch (RestClientException e) {
      logger.warn("⚠️ Error conectando con Google OAuth2: {}", e.getMessage());
      throw new RuntimeException("Google OAuth2 no disponible", e);
    } catch (Exception e) {
      logger.error("❌ Error inesperado verificando Google OAuth2", e);
      throw new RuntimeException("Error verificando OAuth2", e);
    }
  }

  /**
   * Fallback cuando Google OAuth2 no está disponible
   */
  public boolean fallbackOAuth2Health(Exception ex) {
    logger.warn("🔌 Circuit Breaker activado para OAuth2. Usando fallback. Error: {}", ex.getMessage());
    return false;
  }

  /**
   * Obtener información básica de usuario OAuth2 con protección
   */
  @CircuitBreaker(name = "oauth2", fallbackMethod = "fallbackGetUserInfo")
  public Map<String, Object> getUserInfoFromProvider(String accessToken, String providerUrl) {
    logger.debug("🔍 Obteniendo información de usuario desde provider externo...");

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(accessToken);
      HttpEntity<String> entity = new HttpEntity<>(headers);

      @SuppressWarnings("unchecked")
      ResponseEntity<Map> response = restTemplate.exchange(
          providerUrl, HttpMethod.GET, entity, Map.class);

      logger.debug("✅ Información de usuario obtenida exitosamente");
      return response.getBody();

    } catch (Exception e) {
      logger.warn("⚠️ Error obteniendo información de usuario: {}", e.getMessage());
      throw new RuntimeException("Error obteniendo datos del usuario", e);
    }
  }

  /**
   * Fallback para obtención de información de usuario
   */
  public Map<String, Object> fallbackGetUserInfo(String accessToken, String providerUrl, Exception ex) {
    logger.warn("🔌 Circuit Breaker activado para obtención de usuario. Error: {}", ex.getMessage());

    // Retornar información mínima para no bloquear el proceso
    return Map.of(
        "sub", "unknown",
        "email", "unknown@example.com",
        "name", "Usuario Temporal",
        "fallback", true);
  }

  /**
   * Verificar conectividad básica a Internet
   */
  @CircuitBreaker(name = "oauth2", fallbackMethod = "fallbackInternetCheck")
  public boolean checkInternetConnectivity() {
    logger.debug("🔍 Verificando conectividad a Internet...");

    try {
      ResponseEntity<String> response = restTemplate.getForEntity("https://www.google.com", String.class);
      logger.debug("✅ Conectividad a Internet OK");
      return response.getStatusCode().is2xxSuccessful();

    } catch (Exception e) {
      logger.warn("⚠️ Sin conectividad a Internet: {}", e.getMessage());
      throw new RuntimeException("Sin conectividad", e);
    }
  }

  /**
   * Fallback para verificación de conectividad
   */
  public boolean fallbackInternetCheck(Exception ex) {
    logger.warn("🔌 Circuit Breaker activado para conectividad. Asumiendo offline.");
    return false;
  }
}