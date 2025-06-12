package com.udea.gpx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.udea.gpx.service.TokenService;
import com.udea.gpx.service.TokenService.TokenPair;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Controlador para operaciones avanzadas de tokens JWT
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Gestión avanzada de tokens JWT")
public class AuthTokenController {

  private static final Logger logger = LoggerFactory.getLogger(AuthTokenController.class);

  // Constantes para respuestas
  private static final String KEY_MESSAGE = "message";
  private static final String KEY_ERROR = "error";

  private final TokenService tokenService;

  public AuthTokenController(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  /**
   * Refresca un access token usando un refresh token
   */
  @PostMapping("/refresh")
  @Operation(summary = "Refrescar token de acceso", description = "Genera un nuevo access token usando un refresh token válido")
  @ApiResponse(responseCode = "200", description = "Token refrescado exitosamente", content = @Content(schema = @Schema(example = "{\"accessToken\":\"new-jwt-token\",\"refreshToken\":\"new-refresh-token\",\""
      + KEY_MESSAGE + "\":\"Token refrescado exitosamente\"}")))
  @ApiResponse(responseCode = "400", description = "Refresh token inválido o expirado")
  @ApiResponse(responseCode = "500", description = "Error interno del servidor")
  public ResponseEntity<Map<String, Object>> refreshToken(
      @Parameter(description = "Refresh token", required = true, schema = @Schema(example = "{\"refreshToken\":\"refresh-token-uuid-12345\"}")) @RequestBody Map<String, String> request) {
    try {
      String refreshToken = request.get("refreshToken");

      if (refreshToken == null || refreshToken.trim().isEmpty()) {
        return ResponseEntity.badRequest()
            .body(Map.of(KEY_ERROR, "Refresh token requerido"));
      }

      logger.debug("🔍 AuthTokenController.refreshToken - Procesando refresh token");

      TokenPair newTokenPair = tokenService.refreshAccessToken(refreshToken);
      return ResponseEntity.ok(Map.of(
          "accessToken", newTokenPair.getAccessToken(),
          "refreshToken", newTokenPair.getRefreshToken(),
          KEY_MESSAGE, "Token refrescado exitosamente"));

    } catch (IllegalArgumentException e) {
      logger.warn("⚠️ AuthTokenController.refreshToken - Error: {}", e.getMessage());
      return ResponseEntity.badRequest()
          .body(Map.of(KEY_ERROR, e.getMessage()));
    } catch (Exception e) {
      logger.error("❌ AuthTokenController.refreshToken - Error inesperado: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError()
          .body(Map.of(KEY_ERROR, "Error interno del servidor"));
    }
  }

  /**
   * Logout que invalida el token actual
   */
  @PostMapping("/logout")
  @Operation(summary = "Cerrar sesión", description = "Invalida el token JWT actual añadiéndolo a la blacklist")
  @SecurityRequirement(name = "Bearer Authentication")
  @ApiResponse(responseCode = "200", description = "Logout exitoso", content = @Content(schema = @Schema(example = "{\""
      + KEY_MESSAGE + "\":\"Logout exitoso\"}")))
  @ApiResponse(responseCode = "400", description = "Token no encontrado")
  @ApiResponse(responseCode = "500", description = "Error durante logout")
  public ResponseEntity<Map<String, Object>> logout(
      @Parameter(description = "Request con Authorization header", hidden = true) HttpServletRequest request) {
    try {
      String authHeader = request.getHeader("Authorization");

      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        tokenService.invalidateToken(token);

        logger.info("✅ AuthTokenController.logout - Token invalidado exitosamente");
        return ResponseEntity.ok(Map.of(KEY_MESSAGE, "Logout exitoso"));
      }

      return ResponseEntity.badRequest()
          .body(Map.of(KEY_ERROR, "Token no encontrado"));

    } catch (Exception e) {
      logger.error("❌ AuthTokenController.logout - Error: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError()
          .body(Map.of(KEY_ERROR, "Error durante logout"));
    }
  }

  /**
   * Invalida todos los tokens de un usuario (logout global)
   */
  @PostMapping("/logout-all")
  public ResponseEntity<Map<String, Object>> logoutAll(@RequestBody Map<String, Object> request) {
    try {
      Object userIdObj = request.get("userId");

      if (userIdObj == null) {
        return ResponseEntity.badRequest()
            .body(Map.of(KEY_ERROR, "ID de usuario requerido"));
      }

      Long userId = Long.valueOf(userIdObj.toString());
      tokenService.invalidateAllUserTokens(userId);

      logger.info("✅ AuthTokenController.logoutAll - Todos los tokens invalidados para usuario {}", userId);
      return ResponseEntity.ok(Map.of(KEY_MESSAGE, "Todas las sesiones cerradas exitosamente"));

    } catch (NumberFormatException e) {
      return ResponseEntity.badRequest()
          .body(Map.of(KEY_ERROR, "ID de usuario inválido"));
    } catch (Exception e) {
      logger.error("❌ AuthTokenController.logoutAll - Error: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError()
          .body(Map.of(KEY_ERROR, "Error durante logout global"));
    }
  }

  /**
   * Endpoint para verificar el estado de un token
   */
  @PostMapping("/verify")
  public ResponseEntity<Map<String, Object>> verifyToken(@RequestBody Map<String, String> request) {
    try {
      String token = request.get("token");

      if (token == null || token.trim().isEmpty()) {
        return ResponseEntity.badRequest()
            .body(Map.of(KEY_ERROR, "Token requerido"));
      }

      boolean isBlacklisted = tokenService.isTokenBlacklisted(token);

      return ResponseEntity.ok(Map.of(
          "valid", !isBlacklisted,
          "blacklisted", isBlacklisted));

    } catch (Exception e) {
      logger.error("❌ AuthTokenController.verifyToken - Error: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError()
          .body(Map.of(KEY_ERROR, "Error verificando token"));
    }
  }

  /**
   * Endpoint administrativo para limpiar tokens expirados
   */
  @PostMapping("/admin/cleanup")
  public ResponseEntity<Map<String, Object>> cleanupTokens() {
    try {
      tokenService.cleanupExpiredTokens();

      logger.info("✅ AuthTokenController.cleanupTokens - Limpieza de tokens completada");
      return ResponseEntity.ok(Map.of(KEY_MESSAGE, "Limpieza de tokens completada"));

    } catch (Exception e) {
      logger.error("❌ AuthTokenController.cleanupTokens - Error: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError()
          .body(Map.of(KEY_ERROR, "Error durante limpieza"));
    }
  }
}