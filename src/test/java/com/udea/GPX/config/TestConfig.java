package com.udea.GPX.config;

import com.udea.GPX.JwtUtil;
import com.udea.GPX.service.OAuth2Service;
import com.udea.GPX.service.UserService;
import com.udea.GPX.util.AuthUtils;
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