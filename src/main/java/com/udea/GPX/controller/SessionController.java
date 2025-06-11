package com.udea.gpx.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.udea.gpx.service.TokenService;

import java.util.List;
import java.util.Map;

/**
 * Controlador para gestión avanzada de sesiones JWT
 */
@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Session Management", description = "Gestión avanzada de sesiones de usuario")
@SecurityRequirement(name = "bearerAuth")
public class SessionController {

  private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

  private final TokenService tokenService;

  public SessionController(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @GetMapping("/active")
  @Operation(summary = "Obtener sesiones activas", description = "Lista todas las sesiones activas del usuario autenticado")
  @ApiResponse(responseCode = "200", description = "Lista de sesiones activas obtenida exitosamente")
  public ResponseEntity<List<SessionInfo>> getActiveSessions(Authentication authentication) {
    logger.debug("🔍 SessionController.getActiveSessions - Usuario: {}", authentication.getName());

    Long userId = Long.parseLong(authentication.getName());
    List<TokenService.SessionInfo> sessions = tokenService.getActiveSessions(userId);

    // Convertir a DTO para respuesta
    List<SessionInfo> sessionDTOs = sessions.stream()
        .map(this::convertToDTO)
        .toList();

    logger.info("✅ SessionController.getActiveSessions - {} sesiones activas para usuario {}",
        sessionDTOs.size(), userId);

    return ResponseEntity.ok(sessionDTOs);
  }

  @DeleteMapping("/{sessionId}")
  @Operation(summary = "Invalidar sesión específica", description = "Invalida una sesión específica por su ID")
  @ApiResponse(responseCode = "200", description = "Sesión invalidada exitosamente")
  @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
  public ResponseEntity<Map<String, String>> invalidateSession(
      @Parameter(description = "ID de la sesión a invalidar") @PathVariable String sessionId,
      Authentication authentication) {

    logger.debug("🔍 SessionController.invalidateSession - Sesión: {} por usuario: {}",
        sessionId, authentication.getName());

    // Verificar que la sesión pertenece al usuario autenticado
    Long userId = Long.parseLong(authentication.getName());
    List<TokenService.SessionInfo> userSessions = tokenService.getActiveSessions(userId);

    boolean sessionExists = userSessions.stream()
        .anyMatch(session -> session.getSessionId().equals(sessionId));

    if (!sessionExists) {
      return ResponseEntity.notFound().build();
    }

    tokenService.invalidateSession(sessionId);

    logger.info("✅ SessionController.invalidateSession - Sesión {} invalidada por usuario {}",
        sessionId, userId);

    return ResponseEntity.ok(Map.of(
        "message", "Sesión invalidada exitosamente",
        "sessionId", sessionId));
  }

  @DeleteMapping("/all")
  @Operation(summary = "Invalidar todas las sesiones", description = "Invalida todas las sesiones activas del usuario autenticado")
  @ApiResponse(responseCode = "200", description = "Todas las sesiones invalidadas exitosamente")
  public ResponseEntity<Map<String, String>> invalidateAllSessions(Authentication authentication) {
    logger.debug("🔍 SessionController.invalidateAllSessions - Usuario: {}", authentication.getName());

    Long userId = Long.parseLong(authentication.getName());
    tokenService.invalidateAllUserTokens(userId);

    logger.info("✅ SessionController.invalidateAllSessions - Todas las sesiones invalidadas para usuario {}", userId);

    return ResponseEntity.ok(Map.of(
        "message", "Todas las sesiones han sido invalidadas exitosamente"));
  }

  @GetMapping("/info")
  @Operation(summary = "Información de sesión actual", description = "Obtiene información detallada de la sesión actual")
  @ApiResponse(responseCode = "200", description = "Información de sesión obtenida exitosamente")
  public ResponseEntity<Map<String, Object>> getCurrentSessionInfo(Authentication authentication) {
    logger.debug("🔍 SessionController.getCurrentSessionInfo - Usuario: {}", authentication.getName());

    Long userId = Long.parseLong(authentication.getName());
    List<TokenService.SessionInfo> sessions = tokenService.getActiveSessions(userId);

    Map<String, Object> info = Map.of(
        "userId", userId,
        "activeSessions", sessions.size(),
        "maxConcurrentSessions", 5,
        "lastActivity", sessions.isEmpty() ? null : sessions.get(0).getLastActivity());

    logger.info("✅ SessionController.getCurrentSessionInfo - Info obtenida para usuario {}", userId);

    return ResponseEntity.ok(info);
  }

  // Método auxiliar para convertir a DTO
  private SessionInfo convertToDTO(TokenService.SessionInfo session) {
    return new SessionInfo(
        session.getSessionId(),
        session.getCreatedAt(),
        session.getLastActivity(),
        session.getUserAgent(),
        session.getIpAddress());
  }

  // DTO para respuesta de sesión (sin información sensible)
  public static class SessionInfo {
    private final String sessionId;
    private final java.time.Instant createdAt;
    private final java.time.Instant lastActivity;
    private final String userAgent;
    private final String ipAddress;

    public SessionInfo(String sessionId, java.time.Instant createdAt, java.time.Instant lastActivity,
        String userAgent, String ipAddress) {
      this.sessionId = sessionId;
      this.createdAt = createdAt;
      this.lastActivity = lastActivity;
      this.userAgent = userAgent;
      this.ipAddress = ipAddress;
    }

    public String getSessionId() {
      return sessionId;
    }

    public java.time.Instant getCreatedAt() {
      return createdAt;
    }

    public java.time.Instant getLastActivity() {
      return lastActivity;
    }

    public String getUserAgent() {
      return userAgent;
    }

    public String getIpAddress() {
      return ipAddress;
    }
  }
}