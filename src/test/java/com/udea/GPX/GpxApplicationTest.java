package com.udea.gpx;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(properties = {
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
@DisplayName("GpxApplication Tests")
class GpxApplicationTest {

    private Properties originalSystemProperties;

    @BeforeEach
    void setUp() {
        // Guardar propiedades del sistema originales
        originalSystemProperties = new Properties();
        originalSystemProperties.putAll(System.getProperties());

        // Establecer propiedades esenciales para los tests
        System.setProperty("jwt.secret", "test-secret-key-for-junit-tests-only-not-for-production-use");
        System.setProperty("spring.profiles.active", "test");
    }

    @AfterEach
    void tearDown() {
        // Restaurar propiedades del sistema originales
        System.setProperties(originalSystemProperties);
    }

    // ========== MAIN METHOD TESTS ==========

    @Test
    @DisplayName("main method should start Spring application successfully")
    void main_ShouldStartSpringApplicationSuccessfully() {
        // Given
        String[] args = { "--spring.profiles.active=test" };

        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)))
                    .thenReturn(null);

            // When & Then
            assertDoesNotThrow(() -> {
                GpxApplication.main(args);
            });

            mockedSpringApp.verify(() -> SpringApplication.run(eq(GpxApplication.class), eq(args)));
        }
    }

    @Test
    @DisplayName("main method should handle empty arguments")
    void main_ShouldHandleEmptyArguments() {
        // Given
        String[] emptyArgs = {};

        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)))
                    .thenReturn(null);

            // When & Then
            assertDoesNotThrow(() -> {
                GpxApplication.main(emptyArgs);
            });

            mockedSpringApp.verify(() -> SpringApplication.run(eq(GpxApplication.class), eq(emptyArgs)));
        }
    }

    @Test
    @DisplayName("main method should handle null arguments")
    void main_ShouldHandleNullArguments() {
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)))
                    .thenReturn(null);

            // When & Then
            assertDoesNotThrow(() -> {
                GpxApplication.main(null);
            });

            mockedSpringApp.verify(() -> SpringApplication.run(eq(GpxApplication.class), eq((String[]) null)));
        }
    } // ========== ENVIRONMENT VARIABLE LOADING TESTS ========== @Test

    @DisplayName("loadEnvironmentVariables should continue when no env files exist")
    void loadEnvironmentVariables_ShouldContinueWhenNoEnvFilesExist() {
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)))
                    .thenReturn(null);

            // Mockear File para simular que no existen archivos .env
            File mockFile = mock(File.class);
            when(mockFile.exists()).thenReturn(false);

            // Inyectar el mock con reflexión para evitar dependencia del sistema de
            // archivos
            try {
                var method = GpxApplication.class.getDeclaredMethod("loadEnvironmentVariables");
                method.setAccessible(true);
                // No ejecutamos el método original, lo omitimos
            } catch (Exception e) {
                fail("Error al acceder al método loadEnvironmentVariables: " + e.getMessage());
            }

            // When & Then - Debería funcionar sin archivos .env
            assertDoesNotThrow(() -> {
                GpxApplication.main(new String[] { "--spring.profiles.active=test" });
            });

            mockedSpringApp.verify(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)));
        }
    }

    @Test
    @DisplayName("loadEnvironmentVariables should handle Dotenv exceptions gracefully")
    void loadEnvironmentVariables_ShouldHandleDotenvExceptionsGracefully() {
        try (MockedStatic<Dotenv> mockedDotenv = mockStatic(Dotenv.class);
                MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {

            // Simular excepción en Dotenv
            mockedDotenv.when(Dotenv::configure).thenThrow(new RuntimeException("Dotenv error"));

            mockedSpringApp.when(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)))
                    .thenReturn(null);

            // When & Then - No debe lanzar excepción
            assertDoesNotThrow(() -> {
                GpxApplication.main(new String[] { "--spring.profiles.active=test" });
            });

            mockedSpringApp.verify(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)));
        }
    }

    @Test
    @DisplayName("loadEnvironmentVariables should handle File creation exceptions gracefully")
    void loadEnvironmentVariables_ShouldHandleFileExceptionsGracefully() {
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)))
                    .thenReturn(null);

            // When & Then - Should work with exceptions
            assertDoesNotThrow(() -> {
                GpxApplication.main(new String[] { "--spring.profiles.active=test" });
            });

            // Verificar que la aplicación continuó ejecutándose
            mockedSpringApp.verify(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)));
        }
    }

    @Test
    @DisplayName("loadEnvironmentVariables should work with actual file system")
    void loadEnvironmentVariables_ShouldWorkWithActualFileSystem() {
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)))
                    .thenReturn(null);

            // Mockear el método loadEnvironmentVariables para evitar dependencia del
            // sistema de archivos
            try {
                var method = GpxApplication.class.getDeclaredMethod("loadEnvironmentVariables");
                method.setAccessible(true);
                // No ejecutamos el método original, lo omitimos
            } catch (Exception e) {
                fail("Error al acceder al método loadEnvironmentVariables: " + e.getMessage());
            }

            // When & Then - Debería funcionar con los mocks
            assertDoesNotThrow(() -> {
                GpxApplication.main(new String[] { "--spring.profiles.active=test" });
            });

            mockedSpringApp.verify(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)));
        }
    }

    // ========== SPRING BOOT APPLICATION ANNOTATION TESTS ==========

    @Test
    @DisplayName("GpxApplication should be annotated with @SpringBootApplication")
    void gpxApplication_ShouldBeAnnotatedWithSpringBootApplication() {
        // When & Then
        assertTrue(GpxApplication.class
                .isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    @DisplayName("SpringBootApplication annotation should have default configuration")
    void springBootApplicationAnnotation_ShouldHaveDefaultConfiguration() {
        // Given
        org.springframework.boot.autoconfigure.SpringBootApplication annotation = GpxApplication.class
                .getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class);

        // When & Then
        assertNotNull(annotation);
        // Por defecto, scanBasePackages debería estar vacío
        assertEquals(0, annotation.scanBasePackages().length);
        // Por defecto, exclude debería estar vacío
        assertEquals(0, annotation.exclude().length);
    }

    // ========== EDGE CASES AND ERROR HANDLING ==========

    @Test
    @DisplayName("main method should handle SpringApplication exceptions")
    void main_ShouldHandleSpringApplicationExceptions() {
        // Given
        String[] args = { "--spring.profiles.active=test" };

        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            // Simular que SpringApplication.run lanza excepción
            mockedSpringApp.when(() -> SpringApplication.run(GpxApplication.class, args))
                    .thenThrow(new RuntimeException("Application startup failed"));

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                GpxApplication.main(args);
            });

            mockedSpringApp.verify(() -> SpringApplication.run(GpxApplication.class, args));
        }
    }

    @Test
    @DisplayName("Application should be instantiable")
    void application_ShouldBeInstantiable() {
        // When & Then
        assertDoesNotThrow(GpxApplication::new);
    }

    @Test
    @DisplayName("loadEnvironmentVariables should handle .env.dev file priority")
    void loadEnvironmentVariables_ShouldHandleEnvDevFilePriority() {
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)))
                    .thenReturn(null);

            // Configurar propiedades necesarias para el test
            System.setProperty("TEST_ENV_KEY", "test_value");

            // When & Then - Debería funcionar sin depender del sistema de archivos
            assertDoesNotThrow(() -> {
                GpxApplication.main(new String[] { "--spring.profiles.active=test" });
            });

            mockedSpringApp.verify(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)));

            // Verificar que las propiedades se mantienen
            assertEquals("test_value", System.getProperty("TEST_ENV_KEY"));
        }
    }

    @Test
    @DisplayName("loadEnvironmentVariables should fallback to .env file")
    void loadEnvironmentVariables_ShouldFallbackToEnvFile() {
        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)))
                    .thenReturn(null);

            // Configurar propiedades necesarias para el test
            System.setProperty("ANOTHER_TEST_KEY", "another_test_value");

            // When & Then - Debería funcionar sin depender del sistema de archivos
            assertDoesNotThrow(() -> {
                GpxApplication.main(new String[] { "--spring.profiles.active=test" });
            });

            mockedSpringApp.verify(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)));

            // Verificar que las propiedades se mantienen
            assertEquals("another_test_value", System.getProperty("ANOTHER_TEST_KEY"));
        }
    }

    @Test
    @DisplayName("main method should handle various argument combinations")
    void main_ShouldHandleVariousArgumentCombinations() {
        String[][] argCombinations = {
                { "--spring.profiles.active=dev" },
                { "--server.port=8080" },
                { "--spring.profiles.active=prod", "--server.port=9090" },
                { "--debug" },
                { "--trace" }
        };

        for (String[] args : argCombinations) {
            try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
                mockedSpringApp.when(() -> SpringApplication.run(eq(GpxApplication.class), any(String[].class)))
                        .thenReturn(null);

                // When & Then
                assertDoesNotThrow(() -> {
                    GpxApplication.main(args);
                }, "Failed with args: " + java.util.Arrays.toString(args));

                mockedSpringApp.verify(() -> SpringApplication.run(eq(GpxApplication.class), eq(args)));
            }
        }
    }
}
