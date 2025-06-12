package com.udea.gpx.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OpenApiConfig Tests")
class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
        // Configurar valores por defecto
        ReflectionTestUtils.setField(openApiConfig, "apiVersion", "1.0.0");
        ReflectionTestUtils.setField(openApiConfig, "apiTitle", "gpx Racing API");
        ReflectionTestUtils.setField(openApiConfig, "apiDescription",
                "API para el sistema de gestión de eventos de carreras gpx");
        ReflectionTestUtils.setField(openApiConfig, "serverPort", "8080");
    }

    @Test
    @DisplayName("customOpenAPI debe crear configuración OpenAPI completa")
    void testCustomOpenAPI() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getServers());
        assertNotNull(openAPI.getTags());
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getSecurity());
        assertFalse(openAPI.getSecurity().isEmpty());
    }

    @Test
    @DisplayName("Info debe contener metadatos correctos de la API")
    void testApiInfo() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        Info info = openAPI.getInfo();

        assertEquals("gpx Racing API", info.getTitle());
        assertEquals("1.0.0", info.getVersion());
        assertTrue(info.getDescription().contains("API para el sistema de gestión de eventos de carreras gpx"));

        // Verificar que la descripción contiene las secciones esperadas
        assertTrue(info.getDescription().contains("Funcionalidades principales"));
        assertTrue(info.getDescription().contains("Gestión de Usuarios"));
        assertTrue(info.getDescription().contains("Gestión de Eventos"));
        assertTrue(info.getDescription().contains("Autenticación"));
    }

    @Test
    @DisplayName("Contact debe estar configurado correctamente")
    void testContactInfo() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        Contact contact = openAPI.getInfo().getContact();

        assertNotNull(contact);
        assertEquals("Equipo gpx Racing", contact.getName());
        assertEquals("gpx@udea.edu.co", contact.getEmail());
        assertEquals("https://github.com/udea/gpx-racing", contact.getUrl());
    }

    @Test
    @DisplayName("License debe estar configurada correctamente")
    void testLicenseInfo() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        License license = openAPI.getInfo().getLicense();

        assertNotNull(license);
        assertEquals("MIT License", license.getName());
        assertEquals("https://opensource.org/licenses/MIT", license.getUrl());
    }

    @Test
    @DisplayName("Servers debe incluir entornos de desarrollo, staging y producción")
    void testServerList() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        List<Server> servers = openAPI.getServers();

        assertNotNull(servers);
        assertEquals(3, servers.size());

        // Servidor de desarrollo
        Server devServer = servers.get(0);
        assertEquals("http://localhost:8080", devServer.getUrl());
        assertEquals("Servidor de desarrollo local", devServer.getDescription());

        // Servidor de producción
        Server prodServer = servers.get(1);
        assertEquals("https://gpx-api.render.com", prodServer.getUrl());
        assertEquals("Servidor de producción (Render)", prodServer.getDescription());

        // Servidor de staging
        Server stagingServer = servers.get(2);
        assertEquals("https://staging-gpx-api.render.com", stagingServer.getUrl());
        assertEquals("Servidor de staging", stagingServer.getDescription());
    }

    @Test
    @DisplayName("Tags debe incluir todas las categorías de endpoints")
    void testApiTags() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        List<Tag> tags = openAPI.getTags();

        assertNotNull(tags);
        assertEquals(10, tags.size());

        // Verificar que existen los tags principales
        assertTrue(tags.stream().anyMatch(tag -> "Autenticación".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "Usuarios".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "Eventos".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "Categorías".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "Vehículos".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "Etapas".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "Resultados".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "Archivos".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "OAuth2".equals(tag.getName())));
        assertTrue(tags.stream().anyMatch(tag -> "Administración".equals(tag.getName())));
    }

    @Test
    @DisplayName("Tags debe incluir descripciones detalladas")
    void testTagDescriptions() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        List<Tag> tags = openAPI.getTags();

        Tag authTag = tags.stream()
                .filter(tag -> "Autenticación".equals(tag.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(authTag);
        assertEquals("Endpoints para login, registro y gestión de tokens JWT", authTag.getDescription());

        Tag usersTag = tags.stream()
                .filter(tag -> "Usuarios".equals(tag.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(usersTag);
        assertEquals("Gestión de perfiles de usuario, datos personales y roles", usersTag.getDescription());
    }

    @Test
    @DisplayName("Security components debe configurar Bearer Authentication")
    void testSecurityComponents() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        Components components = openAPI.getComponents();

        assertNotNull(components);
        assertNotNull(components.getSecuritySchemes());
        assertTrue(components.getSecuritySchemes().containsKey("Bearer Authentication"));

        SecurityScheme bearerScheme = components.getSecuritySchemes().get("Bearer Authentication");
        assertEquals(SecurityScheme.Type.HTTP, bearerScheme.getType());
        assertEquals("bearer", bearerScheme.getScheme());
        assertEquals("JWT", bearerScheme.getBearerFormat());
        assertTrue(bearerScheme.getDescription().contains("Token JWT"));
        assertTrue(bearerScheme.getDescription().contains("Bearer {token}"));
    }

    @Test
    @DisplayName("Security requirement debe incluir Bearer Authentication")
    void testSecurityRequirement() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        List<SecurityRequirement> securityRequirements = openAPI.getSecurity();

        assertNotNull(securityRequirements);
        assertFalse(securityRequirements.isEmpty());

        SecurityRequirement requirement = securityRequirements.get(0);
        assertTrue(requirement.containsKey("Bearer Authentication"));
    }

    @Test
    @DisplayName("Configuración debe funcionar con valores personalizados")
    void testCustomValues() {
        // Configurar valores personalizados
        ReflectionTestUtils.setField(openApiConfig, "apiVersion", "2.0.0");
        ReflectionTestUtils.setField(openApiConfig, "apiTitle", "Custom API Title");
        ReflectionTestUtils.setField(openApiConfig, "apiDescription", "Custom API Description");
        ReflectionTestUtils.setField(openApiConfig, "serverPort", "9090");

        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertEquals("Custom API Title", openAPI.getInfo().getTitle());
        assertEquals("2.0.0", openAPI.getInfo().getVersion());
        assertTrue(openAPI.getInfo().getDescription().contains("Custom API Description"));

        // Verificar que el puerto se actualiza en el servidor de desarrollo
        Server devServer = openAPI.getServers().get(0);
        assertEquals("http://localhost:9090", devServer.getUrl());
    }

    @Test
    @DisplayName("Clase debe estar anotada como Configuration")
    void testConfigurationAnnotation() {
        assertTrue(OpenApiConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
    }

    @Test
    @DisplayName("customOpenAPI debe estar anotado como Bean")
    void testBeanAnnotation() throws NoSuchMethodException {
        assertTrue(OpenApiConfig.class
                .getMethod("customOpenAPI")
                .isAnnotationPresent(org.springframework.context.annotation.Bean.class));
    }

    @Test
    @DisplayName("Descripción de API debe incluir información de autenticación")
    void testApiDescriptionContainsAuthInfo() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        String description = openAPI.getInfo().getDescription();

        assertTrue(description.contains("Autenticación"));
        assertTrue(description.contains("JWT"));
        assertTrue(description.contains("OAuth2 Google"));
        assertTrue(description.contains("Authorization: Bearer {token}"));
        assertTrue(description.contains("/api/users/login"));
    }

    @Test
    @DisplayName("Descripción de API debe incluir funcionalidades principales")
    void testApiDescriptionContainsMainFeatures() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        String description = openAPI.getInfo().getDescription();

        assertTrue(description.contains("Gestión de Usuarios"));
        assertTrue(description.contains("Gestión de Eventos"));
        assertTrue(description.contains("Gestión de Vehículos"));
        assertTrue(description.contains("Resultados de Etapas"));
        assertTrue(description.contains("Archivos"));
    }

    @Test
    @DisplayName("Servidores deben tener URLs válidas")
    void testServerUrlsAreValid() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        List<Server> servers = openAPI.getServers();

        for (Server server : servers) {
            String url = server.getUrl();
            assertNotNull(url);
            assertFalse(url.trim().isEmpty());
            assertTrue(url.startsWith("http://") || url.startsWith("https://"));
        }
    }

    @Test
    @DisplayName("Todos los tags deben tener nombre y descripción")
    void testAllTagsHaveNameAndDescription() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        List<Tag> tags = openAPI.getTags();

        for (Tag tag : tags) {
            assertNotNull(tag.getName());
            assertFalse(tag.getName().trim().isEmpty());
            assertNotNull(tag.getDescription());
            assertFalse(tag.getDescription().trim().isEmpty());
        }
    }
}