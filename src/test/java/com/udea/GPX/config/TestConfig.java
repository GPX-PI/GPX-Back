package com.udea.gpx.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.udea.gpx.JwtUtil;
import com.udea.gpx.service.OAuth2Service;
import com.udea.gpx.service.UserService;
import com.udea.gpx.util.AuthUtils;

import javax.sql.DataSource;

/**
 * 🎯 CONFIGURACIÓN ÚNICA DE TESTS
 * 
 * Esta es la ÚNICA configuración que necesitamos para todos los tests.
 * Simple, funcional y sin sobre-ingeniería.
 * 
 * ✅ H2 en memoria (sin Docker)
 * ✅ Mocks necesarios para tests unitarios
 * ✅ Configuración dinámica automática
 */
@TestConfiguration
public class TestConfig {

  // =================== DATASOURCE H2 ===================

  /**
   * DataSource H2 único para todos los tests
   * Reemplaza Docker/PostgreSQL automáticamente
   */
  @Bean
  @Primary
  @Profile({ "test", "test-no-docker" })
  public DataSource h2DataSource() {
    return DataSourceBuilder.create()
        .driverClassName("org.h2.Driver")
        .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL")
        .username("sa")
        .password("password")
        .build();
  }

  // =================== CONFIGURACIÓN AUTOMÁTICA ===================

  /**
   * Configuración automática para todos los tests
   * Se aplica sin necesidad de archivos .properties adicionales
   */
  @DynamicPropertySource
  static void configureTestProperties(DynamicPropertyRegistry registry) {
    // H2 Database
    registry.add("spring.datasource.url",
        () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL");
    registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
    registry.add("spring.datasource.username", () -> "sa");
    registry.add("spring.datasource.password", () -> "password");

    // JPA/Hibernate
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.H2Dialect");
    registry.add("spring.jpa.show-sql", () -> "false");

    // JWT para tests
    registry.add("jwt.secret", () -> "UW5LaVBhRjhYdkhOdjJWM3c0WkRyOEJ6S2ZiNFJ6V3BhNElHUzlqQk1VaVExTDJOSzM3VndOWnI=");

    // Deshabilitar funciones innecesarias en tests
    registry.add("testcontainers.enabled", () -> "false");
    registry.add("spring.test.database.replace", () -> "none");
    registry.add("spring.docker.compose.enabled", () -> "false");
  }

  // =================== CONFIGURACIÓN COMPLETA PARA TESTS ===================

  /**
   * Configuración adicional para tests cuando falta application.properties
   */
  @DynamicPropertySource
  static void configureAdditionalProperties(DynamicPropertyRegistry registry) {
    // JWT
    registry.add("jwt.secret", () -> "test-secret-key-for-junit-tests-only-not-for-production-use");
    registry.add("jwt.expiration-seconds", () -> "3600");
    registry.add("jwt.refresh-expiration-seconds", () -> "604800");
    registry.add("jwt.max-concurrent-sessions", () -> "5");
    registry.add("jwt.allow-refresh-token-rotation", () -> "true");
    registry.add("jwt.enable-blacklist", () -> "true");
    registry.add("jwt.session-timeout-seconds", () -> "7200");
    registry.add("jwt.max-session-duration-seconds", () -> "28800");

    // OAuth2
    registry.add("spring.security.oauth2.client.registration.google.client-id", () -> "test-client-id");
    registry.add("spring.security.oauth2.client.registration.google.client-secret", () -> "test-client-secret");
    registry.add("spring.security.oauth2.client.registration.google.scope", () -> "openid,profile,email");
    registry.add("app.oauth2.frontend-redirect-url", () -> "http://localhost:3000/");

    // CORS
    registry.add("cors.allowed-origins", () -> "http://localhost:3000");

    // GPX Event Limits
    registry.add("gpx.event.limits.default-max-vehicles", () -> "100");
    registry.add("gpx.event.limits.max-days-for-registration", () -> "365");
    registry.add("gpx.event.limits.min-days-before-start", () -> "1");
    registry.add("gpx.event.limits.enforce-capacity-limits", () -> "true");

    // Server port
    registry.add("server.port", () -> "8080");

    // Multipart
    registry.add("spring.servlet.multipart.max-file-size", () -> "10MB");
    registry.add("spring.servlet.multipart.max-request-size", () -> "10MB");
  }

  // =================== MOCKS PARA TESTS UNITARIOS ===================

  @Bean
  @Primary
  public JwtUtil jwtUtil() {
    return Mockito.mock(JwtUtil.class);
  }

  @Bean
  @Primary
  public OAuth2Service oAuth2Service() {
    return Mockito.mock(OAuth2Service.class);
  }

  @Bean
  @Primary
  public UserService userService() {
    return Mockito.mock(UserService.class);
  }

  @Bean
  @Primary
  public AuthUtils authUtils() {
    return Mockito.mock(AuthUtils.class);
  }

  @Bean
  @Primary
  public ClientRegistrationRepository clientRegistrationRepository() {
    return Mockito.mock(ClientRegistrationRepository.class);
  }

  @Bean
  @Primary
  public OAuth2AuthorizedClientRepository authorizedClientRepository() {
    return Mockito.mock(OAuth2AuthorizedClientRepository.class);
  }
}