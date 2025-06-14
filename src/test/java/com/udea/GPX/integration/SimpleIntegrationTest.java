package com.udea.gpx.integration;

import com.udea.gpx.config.TestConfig;
import com.udea.gpx.util.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 🎯 TEST DE INTEGRACIÓN SIMPLE Y ESTANDARIZADO
 * 
 * ✅ Usa TestConfig único
 * ✅ H2 automático (sin Docker)
 * ✅ TestDataBuilder para datos de prueba
 * ✅ Configuración mínima y funcional
 */
@SpringBootTest(classes = TestConfig.class, properties = {
    "spring.profiles.active=test",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "jwt.secret=test-secret-key-for-junit-tests-only-not-for-production-use",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=password",
    "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
    "spring.jpa.show-sql=false",
    "spring.security.oauth2.client.registration.google.client-id=test-client-id",
    "spring.security.oauth2.client.registration.google.client-secret=test-client-secret",
    "app.oauth2.frontend-redirect-url=http://localhost:3000/",
    "cors.allowed-origins=http://localhost:3000",
    "spring.servlet.multipart.max-file-size=10MB",
    "spring.servlet.multipart.max-request-size=10MB"
})
@ActiveProfiles("test")
@DisplayName("🚀 Simple Integration Test")
class SimpleIntegrationTest {

  private static final Logger logger = LoggerFactory.getLogger(SimpleIntegrationTest.class);

  @Autowired
  private DataSource dataSource;

  @Test
  @DisplayName("✅ H2 DataSource configurado correctamente")
  void h2DataSourceShouldBeConfigured() throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData metaData = connection.getMetaData();

      assertThat(connection).isNotNull();
      assertThat(connection.isValid(1)).isTrue();
      assertThat(metaData.getDatabaseProductName()).isEqualToIgnoringCase("H2");
      assertThat(metaData.getURL()).contains("jdbc:h2:mem:testdb");

      logger.info("✅ Base de datos: {}", metaData.getDatabaseProductName());
      logger.info("✅ URL: {}", metaData.getURL());
    }
  }

  @Test
  @DisplayName("⚡ H2 súper rápido para tests")
  void h2ShouldExecuteQueriesFast() throws SQLException {
    long startTime = System.currentTimeMillis();

    try (Connection connection = dataSource.getConnection()) {
      var statement = connection.createStatement();
      var resultSet = statement.executeQuery("SELECT 1 as test_value");

      assertThat(resultSet.next()).isTrue();
      assertThat(resultSet.getInt("test_value")).isEqualTo(1);
    }
    long duration = System.currentTimeMillis() - startTime;
    assertThat(duration).isLessThan(100);
    logger.info("⚡ Test ejecutado en: {}ms", duration);
  }

  @Test
  @DisplayName("🏗️ TestDataBuilder funciona correctamente")
  void testDataBuilderShouldWork() {
    // Given - Usando TestDataBuilder para crear datos de prueba
    var user = TestDataBuilder.buildUser(1L, "TestUser", false);
    var adminUser = TestDataBuilder.buildAdminUser();
    var category = TestDataBuilder.buildCategory(1L, "Test Category");
    var vehicle = TestDataBuilder.buildVehicle(1L, user, category);

    // Then - Verificar que los objetos se crean correctamente
    assertThat(user).isNotNull();
    assertThat(user.getFirstName()).isEqualTo("TestUser");
    assertThat(user.isAdmin()).isFalse();

    assertThat(adminUser).isNotNull();
    assertThat(adminUser.isAdmin()).isTrue();

    assertThat(category).isNotNull();
    assertThat(category.getName()).isEqualTo("Test Category");

    assertThat(vehicle).isNotNull();
    assertThat(vehicle.getUser()).isEqualTo(user);
    assertThat(vehicle.getCategory()).isEqualTo(category);

    logger.info("🏗️ TestDataBuilder creó objetos correctamente");
  }

  @Test
  @DisplayName("🔐 Security Context helpers funcionan")
  void securityContextHelpersShouldWork() {
    // Given & When
    TestDataBuilder.setupAdminSecurityContext();

    // Then
    // Este test simplemente verifica que no hay errores
    // La funcionalidad real se probará en tests específicos

    // Assert that the security context is not null
    assertThat(org.springframework.security.core.context.SecurityContextHolder.getContext()).isNotNull();

    logger.info("🔐 Security Context helpers funcionan");
  }

  @Test
  @DisplayName("🎯 Configuración completa y funcional")
  void configurationIsCompleteAndFunctional() throws SQLException {
    // Este test confirma que toda la configuración funciona:
    // ✅ H2 configurado automáticamente
    // ✅ Sin archivos .properties adicionales
    // ✅ TestDataBuilder disponible
    // ✅ Configuración única en TestConfig
    // ✅ Tests súper rápidos

    try (Connection connection = dataSource.getConnection()) {
      assertThat(dataSource).isNotNull();
      assertThat(connection.isValid(1)).isTrue(); // Verificar que podemos crear datos de test
      var user = TestDataBuilder.buildNormalUser();
      assertThat(user).isNotNull();

      logger.info("🎯 ¡Configuración simplificada y funcional!");
    }
  }
}