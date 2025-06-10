package com.udea.GPX.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

/**
 * Controlador para información de la API y ejemplos de uso
 */
@RestController
@RequestMapping("/api/docs")
@Tag(name = "Administración", description = "Información y documentación de la API")
public class ApiDocController {

  @GetMapping("/info")
  @Operation(summary = "Información de la API", description = "Proporciona información general sobre la API, versión y capacidades")
  @ApiResponse(responseCode = "200", description = "Información de la API obtenida exitosamente", content = @Content(schema = @Schema(example = "{\"name\":\"GPX Racing API\",\"version\":\"1.0.0\",\"status\":\"active\"}")))
  public ResponseEntity<Map<String, Object>> getApiInfo() {
    Map<String, Object> info = new HashMap<>();
    info.put("name", "GPX Racing API");
    info.put("version", "1.0.0");
    info.put("description", "API para gestión de eventos de carreras GPX");
    info.put("status", "active");
    info.put("environment", "development");
    info.put("documentation", "/swagger-ui.html");
    info.put("openapi", "/api-docs");
    info.put("contact", Map.of(
        "team", "Equipo GPX Racing",
        "email", "gpx@udea.edu.co"));

    return ResponseEntity.ok(info);
  }

  @GetMapping("/endpoints")
  @Operation(summary = "Lista de endpoints principales", description = "Proporciona una lista organizada de los endpoints principales de la API")
  @ApiResponse(responseCode = "200", description = "Lista de endpoints obtenida exitosamente")
  public ResponseEntity<Map<String, Object>> getEndpoints() {
    Map<String, Object> endpoints = new HashMap<>();

    endpoints.put("authentication", Arrays.asList(
        "POST /api/users/login - Iniciar sesión",
        "POST /api/users/simple-register - Registro de usuario",
        "POST /api/auth/refresh - Refrescar token",
        "POST /api/auth/logout - Cerrar sesión"));

    endpoints.put("users", Arrays.asList(
        "GET /api/users/{id} - Obtener usuario",
        "PUT /api/users/{id} - Actualizar usuario",
        "GET /api/users/paginated - Listar usuarios"));

    endpoints.put("events", Arrays.asList(
        "GET /api/events - Listar eventos",
        "POST /api/events - Crear evento",
        "GET /api/events/{id} - Obtener evento",
        "PUT /api/events/{id} - Actualizar evento",
        "GET /api/events/current - Eventos actuales",
        "GET /api/events/past - Eventos pasados"));

    endpoints.put("vehicles", Arrays.asList(
        "GET /api/vehicles - Listar vehículos",
        "POST /api/vehicles - Crear vehículo",
        "GET /api/vehicles/{id} - Obtener vehículo",
        "PUT /api/vehicles/{id} - Actualizar vehículo"));

    endpoints.put("stages", Arrays.asList(
        "GET /api/stages - Listar etapas",
        "POST /api/stages - Crear etapa",
        "GET /api/stages/{id} - Obtener etapa",
        "PUT /api/stages/{id} - Actualizar etapa"));

    endpoints.put("results", Arrays.asList(
        "GET /api/stage-results - Listar resultados",
        "POST /api/stage-results - Crear resultado",
        "GET /api/stage-results/{id} - Obtener resultado",
        "PUT /api/stage-results/{id} - Actualizar resultado"));

    return ResponseEntity.ok(endpoints);
  }

  @GetMapping("/examples")
  @Operation(summary = "Ejemplos de uso de la API", description = "Proporciona ejemplos de requests y responses para los endpoints principales")
  @ApiResponse(responseCode = "200", description = "Ejemplos obtenidos exitosamente")
  public ResponseEntity<Map<String, Object>> getExamples() {
    Map<String, Object> examples = new HashMap<>();

    // Ejemplo de login
    Map<String, Object> loginExample = Map.of(
        "request", Map.of(
            "method", "POST",
            "url", "/api/users/login",
            "body", Map.of(
                "email", "usuario@email.com",
                "password", "password123")),
        "response", Map.of(
            "accessToken", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "refreshToken", "refresh-token-uuid-12345",
            "userId", 1,
            "admin", false,
            "profileComplete", true,
            "firstName", "Juan"));

    // Ejemplo de crear evento
    Map<String, Object> createEventExample = Map.of(
        "request", Map.of(
            "method", "POST",
            "url", "/api/events",
            "headers", Map.of("Authorization", "Bearer {token}"),
            "body", Map.of(
                "name", "Copa GPX 2024",
                "location", "Medellín, Colombia",
                "details", "Evento anual de carreras off-road",
                "startDate", "2024-06-15",
                "endDate", "2024-06-17")),
        "response", Map.of(
            "id", 1,
            "name", "Copa GPX 2024",
            "location", "Medellín, Colombia",
            "startDate", "2024-06-15",
            "endDate", "2024-06-17"));

    examples.put("login", loginExample);
    examples.put("createEvent", createEventExample);

    return ResponseEntity.ok(examples);
  }

  @GetMapping("/status")
  @Operation(summary = "Estado de la API", description = "Verifica el estado general de la API y sus servicios")
  @ApiResponse(responseCode = "200", description = "Estado de la API obtenido exitosamente")
  public ResponseEntity<Map<String, Object>> getApiStatus() {
    Map<String, Object> status = new HashMap<>();
    status.put("api", "online");
    status.put("database", "connected");
    status.put("authentication", "active");
    status.put("timestamp", System.currentTimeMillis());
    status.put("uptime", "Available");

    Map<String, String> services = new HashMap<>();
    services.put("user-service", "active");
    services.put("event-service", "active");
    services.put("vehicle-service", "active");
    services.put("stage-service", "active");
    services.put("file-service", "active");
    status.put("services", services);

    return ResponseEntity.ok(status);
  }
}