package com.udea.gpx.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.udea.gpx.exception.ConnectivityException;
import com.udea.gpx.exception.OAuth2ServiceException;

import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Cliente simple para servicios externos con protección Circuit Breaker
 * Enfoque minimalista sin over-engineering
 */
@Service
public class ExternalServiceClient {

  private static final Logger logger = LoggerFactory.getLogger(ExternalServiceClient.class);
  // String constants to eliminate duplicated literals
  private static final String GOOGLE_OAUTH_URL = "https://www.googleapis.com/oauth2/v2/tokeninfo?access_token=invalid";
  private static final String GOOGLE_BASE_URL = "https://www.google.com";
  private static final String OAUTH2_CIRCUIT_BREAKER = "oauth2";
  private static final String OAUTH2_NOT_AVAILABLE_MSG = "Google OAuth2 no disponible: ";
  private static final String CRITICAL_ERROR_MSG = "Error crítico";
  private static final String USER_INFO_ERROR_MSG = "Error obteniendo datos del usuario desde provider: ";
  private static final String INTERNET_CONNECTIVITY_ERROR_MSG = "Sin conectividad a Internet: ";
  private static final String TEMP_USER_PREFIX = "temp_";
  private static final String UNKNOWN_EMAIL = "unknown@example.com";
  private static final String TEMP_USER_NAME = "Usuario Temporal";
  private static final String PROVIDER_URL_KEY = "provider_url";
  private static final String FALLBACK_KEY = "fallback";
  private static final String SUB_KEY = "sub";
  private static final String EMAIL_KEY = "email";
  private static final String NAME_KEY = "name";

  private final RestTemplate restTemplate;

  public ExternalServiceClient() {
    this.restTemplate = new RestTemplate();
  }

  /**
   * Verificar disponibilidad de Google OAuth2 con circuit breaker
   */
  @CircuitBreaker(name = OAUTH2_CIRCUIT_BREAKER, fallbackMethod = "fallbackOAuth2Health")
  public boolean checkGoogleOAuth2Health() throws OAuth2ServiceException {
    logger.debug("🔍 Verificando disponibilidad de Google OAuth2...");

    try {
      // Verificación simple con token inválido para probar conectividad
      restTemplate.getForEntity(GOOGLE_OAUTH_URL, String.class);

      // Si responde (aunque sea error), el servicio está disponible
      logger.debug("✅ Google OAuth2 responde correctamente");
      return true;
    } catch (RestClientException e) {
      // Rethrow with contextual information - logging handled by upstream handlers
      throw new OAuth2ServiceException(OAUTH2_NOT_AVAILABLE_MSG + e.getMessage(), e);
    } catch (Exception e) {
      // Rethrow with contextual information - logging handled by upstream handlers
      throw new OAuth2ServiceException(CRITICAL_ERROR_MSG + " verificando OAuth2: " + e.getMessage(), e);
    }
  }

  /**
   * Fallback cuando Google OAuth2 no está disponible
   */
  public boolean fallbackOAuth2Health(OAuth2ServiceException ex) {
    logger.warn("🔌 Circuit Breaker activado para OAuth2. Usando fallback. Error: {}", ex.getMessage());
    return false;
  }

  /**
   * Obtener información básica de usuario OAuth2 con protección
   */
  @CircuitBreaker(name = OAUTH2_CIRCUIT_BREAKER, fallbackMethod = "fallbackGetUserInfo")
  public Map<String, Object> getUserInfoFromProvider(String accessToken, String providerUrl)
      throws OAuth2ServiceException {
    logger.debug("🔍 Obteniendo información de usuario desde provider externo...");

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(accessToken);
      HttpEntity<String> entity = new HttpEntity<>(headers);

      ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
          providerUrl, HttpMethod.GET, entity,
          new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
          });

      logger.debug("✅ Información de usuario obtenida exitosamente");
      Map<String, Object> userInfo = response.getBody();
      return userInfo != null ? userInfo : Map.of();
    } catch (RestClientException e) {
      // Rethrow with contextual information - logging handled by upstream handlers
      throw new OAuth2ServiceException(USER_INFO_ERROR_MSG + e.getMessage(), e);
    } catch (Exception e) {
      // Rethrow with contextual information - logging handled by upstream handlers
      throw new OAuth2ServiceException(CRITICAL_ERROR_MSG + " obteniendo datos del usuario: " + e.getMessage(), e);
    }
  }

  /**
   * Fallback para obtención de información de usuario
   */
  public Map<String, Object> fallbackGetUserInfo(String accessToken, String providerUrl, OAuth2ServiceException ex) {
    logger.warn("🔌 Circuit Breaker activado para obtención de usuario. Provider: {}, Error: {}",
        providerUrl, ex.getMessage()); // Retornar información mínima para no bloquear el proceso // Usar accessToken
                                       // para generar un ID único temporal (evitar Math.abs para hashCode)
    String tempId = TEMP_USER_PREFIX + Integer.toUnsignedString(accessToken.hashCode());

    return Map.of(
        SUB_KEY, tempId,
        EMAIL_KEY, UNKNOWN_EMAIL,
        NAME_KEY, TEMP_USER_NAME,
        PROVIDER_URL_KEY, providerUrl,
        FALLBACK_KEY, true);
  }

  /**
   * Verificar conectividad básica a Internet
   */
  @CircuitBreaker(name = OAUTH2_CIRCUIT_BREAKER, fallbackMethod = "fallbackInternetCheck")
  public boolean checkInternetConnectivity() throws ConnectivityException {
    logger.debug("🔍 Verificando conectividad a Internet...");

    try {
      ResponseEntity<String> response = restTemplate.getForEntity(GOOGLE_BASE_URL, String.class);
      logger.debug("✅ Conectividad a Internet OK");
      return response.getStatusCode().is2xxSuccessful();
    } catch (RestClientException e) {
      // Rethrow with contextual information - logging handled by upstream handlers
      throw new ConnectivityException(INTERNET_CONNECTIVITY_ERROR_MSG + e.getMessage(), e);
    } catch (Exception e) {
      // Rethrow with contextual information - logging handled by upstream handlers
      throw new ConnectivityException(CRITICAL_ERROR_MSG + " verificando conectividad: " + e.getMessage(), e);
    }
  }

  /**
   * Fallback para verificación de conectividad
   */
  public boolean fallbackInternetCheck(ConnectivityException ex) {
    logger.warn("🔌 Circuit Breaker activado para conectividad. Asumiendo offline. Error: {}", ex.getMessage());
    return false;
  }
}