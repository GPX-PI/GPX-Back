package com.udea.GPX.integration;

import com.udea.GPX.config.TestConfig;
import com.udea.GPX.util.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles("test")
@DisplayName("🚀 Simple Integration Test")
class SimpleIntegrationTest {

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

      System.out.println("✅ Base de datos: " + metaData.getDatabaseProductName());
      System.out.println("✅ URL: " + metaData.getURL());
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
    System.out.println("⚡ Test ejecutado en: " + duration + "ms");
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

    System.out.println("🏗️ TestDataBuilder creó objetos correctamente");
  }

  @Test
  @DisplayName("🔐 Security Context helpers funcionan")
  void securityContextHelpersShouldWork() {
    // Given & When
    TestDataBuilder.setupAdminSecurityContext();

    // Then
    // Este test simplemente verifica que no hay errores
    // La funcionalidad real se probará en tests específicos

    // Cleanup
    TestDataBuilder.clearSecurityContext();

    System.out.println("🔐 Security Context helpers funcionan");
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
      assertThat(connection.isValid(1)).isTrue();

      // Verificar que podemos crear datos de test
      var user = TestDataBuilder.buildNormalUser();
      assertThat(user).isNotNull();

      System.out.println("🎯 ¡Configuración simplificada y funcional!");
    }
  }
}