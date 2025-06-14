package com.udea.gpx.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiDocController Tests")
class ApiDocControllerTest {

    @InjectMocks
    private ApiDocController apiDocController;

    @BeforeEach
    void setUp() {
        // No se necesita configuración especial
    }

    // ========== GET API INFO TESTS ==========

    @Test
    @DisplayName("getApiInfo - Debe retornar información completa de la API")
    void getApiInfo_shouldReturnCompleteApiInfo() {
        // When
        ResponseEntity<Map<String, Object>> response = apiDocController.getApiInfo();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> info = response.getBody();
        assertThat(info.get("name")).isEqualTo("gpx Racing API");
        assertThat(info.get("version")).isEqualTo("1.0.0");
        assertThat(info.get("description")).isEqualTo("API para gestión de eventos de carreras gpx");
        assertThat(info.get("status")).isEqualTo("active");
        assertThat(info.get("environment")).isEqualTo("development");
        assertThat(info.get("documentation")).isEqualTo("/swagger-ui.html");
        assertThat(info.get("openapi")).isEqualTo("/api-docs");

        @SuppressWarnings("unchecked")
        Map<String, String> contact = (Map<String, String>) info.get("contact");
        assertThat(contact).isNotNull();
        assertThat(contact.get("team")).isEqualTo("Equipo gpx Racing");
        assertThat(contact.get("email")).isEqualTo("gpx@udea.edu.co");
    }

    // ========== GET ENDPOINTS TESTS ==========

    @Test
    @DisplayName("getEndpoints - Debe retornar todos los grupos de endpoints")
    void getEndpoints_shouldReturnAllEndpointGroups() {
        // When
        ResponseEntity<Map<String, Object>> response = apiDocController.getEndpoints();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> endpoints = response.getBody();
        assertThat(endpoints).containsKeys("authentication", "users", "events", "vehicles", "stages", "results");
    }

    @Test
    @DisplayName("getEndpoints - Debe incluir endpoints de autenticación")
    void getEndpoints_shouldIncludeAuthenticationEndpoints() {
        // When
        ResponseEntity<Map<String, Object>> response = apiDocController.getEndpoints();

        // Then
        Map<String, Object> endpoints = response.getBody();
        @SuppressWarnings("unchecked")
        List<String> authEndpoints = (List<String>) endpoints.get("authentication");

        assertThat(authEndpoints).isNotNull();
        assertThat(authEndpoints).hasSize(4);
        assertThat(authEndpoints).contains("POST /api/users/login - Iniciar sesión");
        assertThat(authEndpoints).contains("POST /api/users/simple-register - Registro de usuario");
        assertThat(authEndpoints).contains("POST /api/auth/refresh - Refrescar token");
        assertThat(authEndpoints).contains("POST /api/auth/logout - Cerrar sesión");
    }

    @Test
    @DisplayName("getEndpoints - Debe incluir endpoints de usuarios")
    void getEndpoints_shouldIncludeUserEndpoints() {
        // When
        ResponseEntity<Map<String, Object>> response = apiDocController.getEndpoints();

        // Then
        Map<String, Object> endpoints = response.getBody();
        @SuppressWarnings("unchecked")
        List<String> userEndpoints = (List<String>) endpoints.get("users");

        assertThat(userEndpoints).isNotNull();
        assertThat(userEndpoints).hasSize(6);
        assertThat(userEndpoints).contains(
                "GET /api/users/{id} - Obtener usuario",
                "PUT /api/users/{id}/profile - Actualizar perfil (solo datos)",
                "PUT /api/users/{id}/picture - Actualizar foto de perfil (URL)",
                "PUT /api/users/{id}/insurance - Actualizar seguro (URL)",
                "DELETE /api/users/{id}/insurance - Eliminar seguro",
                "GET /api/users/paginated - Listar usuarios");
    }

    @Test
    @DisplayName("getEndpoints - Debe incluir endpoints de eventos")
    void getEndpoints_shouldIncludeEventEndpoints() {
        // When
        ResponseEntity<Map<String, Object>> response = apiDocController.getEndpoints();

        // Then
        Map<String, Object> endpoints = response.getBody();
        @SuppressWarnings("unchecked")
        List<String> eventEndpoints = (List<String>) endpoints.get("events");

        assertThat(eventEndpoints).isNotNull();
        assertThat(eventEndpoints).hasSize(8);
        assertThat(eventEndpoints).contains(
                "GET /api/events - Listar eventos",
                "POST /api/events - Crear evento",
                "GET /api/events/{id} - Obtener evento",
                "PUT /api/events/{id} - Actualizar evento",
                "PUT /api/events/{id}/picture - Actualizar imagen evento (URL)",
                "DELETE /api/events/{id}/picture - Eliminar imagen evento",
                "GET /api/events/current - Eventos actuales",
                "GET /api/events/past - Eventos pasados");
    }

    // ========== GET EXAMPLES TESTS ==========

    @Test
    @DisplayName("getExamples - Debe retornar ejemplos de uso")
    void getExamples_shouldReturnUsageExamples() {
        // When
        ResponseEntity<Map<String, Object>> response = apiDocController.getExamples();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> examples = response.getBody();
        assertThat(examples).containsKeys("login", "createEvent");
    }

    @Test
    @DisplayName("getExamples - Debe incluir ejemplo de login")
    void getExamples_shouldIncludeLoginExample() {
        // When
        ResponseEntity<Map<String, Object>> response = apiDocController.getExamples();

        // Then
        Map<String, Object> examples = response.getBody();
        @SuppressWarnings("unchecked")
        Map<String, Object> loginExample = (Map<String, Object>) examples.get("login");

        assertThat(loginExample).isNotNull();
        assertThat(loginExample).containsKeys("request", "response");

        @SuppressWarnings("unchecked")
        Map<String, Object> request = (Map<String, Object>) loginExample.get("request");
        assertThat(request.get("method")).isEqualTo("POST");
        assertThat(request.get("url")).isEqualTo("/api/users/login");

        @SuppressWarnings("unchecked")
        Map<String, Object> requestBody = (Map<String, Object>) request.get("body");
        assertThat(requestBody.get("email")).isEqualTo("usuario@email.com");
        assertThat(requestBody.get("password")).isEqualTo("password123");
    }

    @Test
    @DisplayName("getExamples - Debe incluir ejemplo de crear evento")
    void getExamples_shouldIncludeCreateEventExample() {
        // When
        ResponseEntity<Map<String, Object>> response = apiDocController.getExamples();

        // Then
        Map<String, Object> examples = response.getBody();
        @SuppressWarnings("unchecked")
        Map<String, Object> createEventExample = (Map<String, Object>) examples.get("createEvent");

        assertThat(createEventExample).isNotNull();
        assertThat(createEventExample).containsKeys("request", "response");

        @SuppressWarnings("unchecked")
        Map<String, Object> request = (Map<String, Object>) createEventExample.get("request");
        assertThat(request.get("method")).isEqualTo("POST");
        assertThat(request.get("url")).isEqualTo("/api/events");

        @SuppressWarnings("unchecked")
        Map<String, Object> headers = (Map<String, Object>) request.get("headers");
        assertThat(headers.get("Authorization")).isEqualTo("Bearer {token}");

        @SuppressWarnings("unchecked")
        Map<String, Object> requestBody = (Map<String, Object>) request.get("body");
        assertThat(requestBody.get("name")).isEqualTo("Copa gpx 2024");
        assertThat(requestBody.get("location")).isEqualTo("Medellín, Colombia");
    }

    // ========== GET API STATUS TESTS ==========

    @Test
    @DisplayName("getApiStatus - Debe retornar estado completo de la API")
    void getApiStatus_shouldReturnCompleteApiStatus() {
        // When
        ResponseEntity<Map<String, Object>> response = apiDocController.getApiStatus();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> status = response.getBody();
        assertThat(status.get("api")).isEqualTo("online");
        assertThat(status.get("database")).isEqualTo("connected");
        assertThat(status.get("authentication")).isEqualTo("active");
        assertThat(status.get("uptime")).isEqualTo("Available");
        assertThat(status.get("timestamp")).isNotNull();
        assertThat(status.get("timestamp")).isInstanceOf(Long.class);
    }

    @Test
    @DisplayName("getApiStatus - Debe incluir estado de todos los servicios")
    void getApiStatus_shouldIncludeAllServicesStatus() {
        // When
        ResponseEntity<Map<String, Object>> response = apiDocController.getApiStatus();

        // Then
        Map<String, Object> status = response.getBody();
        @SuppressWarnings("unchecked")
        Map<String, String> services = (Map<String, String>) status.get("services");

        assertThat(services).isNotNull();
        assertThat(services).containsKeys(
                "user-service",
                "event-service",
                "vehicle-service",
                "stage-service",
                "url-validation");

        // Todos los servicios deben estar activos
        services.values().forEach(serviceStatus -> assertThat(serviceStatus).isEqualTo("active"));
    }

    @Test
    @DisplayName("getApiStatus - Timestamp debe ser tiempo actual")
    void getApiStatus_timestampShouldBeCurrentTime() {
        // Given
        long beforeCall = System.currentTimeMillis();

        // When
        ResponseEntity<Map<String, Object>> response = apiDocController.getApiStatus();

        // Then
        long afterCall = System.currentTimeMillis();
        Map<String, Object> status = response.getBody();
        long timestamp = (Long) status.get("timestamp");

        assertThat(timestamp).isBetween(beforeCall, afterCall);
    }
}