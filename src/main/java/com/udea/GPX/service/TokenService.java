package com.udea.gpx.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.udea.gpx.JwtUtil;
import com.udea.gpx.config.JwtProperties;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Servicio avanzado de tokens JWT con refresh tokens, blacklist y gestión de
 * sesiones
 */
@Service
public class TokenService {
  private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

  private final JwtUtil jwtUtil;
  private final JwtProperties jwtProperties;

  // Almacenamiento en memoria para tokens - thread-safe
  private final Map<String, RefreshTokenInfo> refreshTokenStore = new ConcurrentHashMap<>();
  private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

  // Gestión de sesiones activas por usuario - thread-safe
  private final Map<Long, Set<SessionInfo>> activeSessions = new ConcurrentHashMap<>();
  private final Map<String, SessionInfo> sessionsByToken = new ConcurrentHashMap<>();

  // Gestión de access tokens por usuario (para tokens sin sesión) - thread-safe
  private final Map<Long, Set<String>> userAccessTokens = new ConcurrentHashMap<>();

  public TokenService(JwtUtil jwtUtil, JwtProperties jwtProperties) {
    this.jwtUtil = jwtUtil;
    this.jwtProperties = jwtProperties;
  }

  /**
   * Genera un par de tokens (access + refresh)
   */
  public TokenPair generateTokenPair(Long userId, boolean isAdmin) {
    logger.debug("🔍 TokenService.generateTokenPair - Generando tokens para usuario {}", userId); // Generar access
                                                                                                  // token
    String accessToken = jwtUtil.generateToken(userId, isAdmin);

    // Generar refresh token
    String refreshToken = generateRefreshToken(userId);

    // Almacenar información del refresh token
    RefreshTokenInfo tokenInfo = new RefreshTokenInfo(userId, isAdmin,
        Instant.now().plusSeconds(jwtProperties.getRefreshExpirationSeconds()));
    storeRefreshToken(refreshToken, tokenInfo);

    // Registrar access token para el usuario (para poder invalidarlo después)
    userAccessTokens.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(accessToken);

    logger.info("✅ TokenService.generateTokenPair - Tokens generados para usuario {} (sin sesión)", userId);
    return new TokenPair(accessToken, refreshToken);
  }

  /**
   * Genera un par de tokens con información de sesión
   */
  public TokenPair generateTokenPairWithSession(Long userId, boolean isAdmin, String userAgent, String ipAddress) {
    logger.debug("🔍 TokenService.generateTokenPairWithSession - Generando tokens para usuario {}", userId);

    // Verificar límite de sesiones concurrentes
    enforceSessionLimits(userId); // Generar access token
    String accessToken = jwtUtil.generateToken(userId, isAdmin);

    // Generar refresh token
    String refreshToken = generateRefreshToken(userId);

    // Crear información de sesión
    String sessionId = UUID.randomUUID().toString();
    SessionInfo sessionInfo = new SessionInfo(sessionId, userId, accessToken, refreshToken, userAgent, ipAddress);

    // Almacenar información del refresh token
    RefreshTokenInfo tokenInfo = new RefreshTokenInfo(userId, isAdmin,
        Instant.now().plusSeconds(jwtProperties.getRefreshExpirationSeconds()));
    storeRefreshToken(refreshToken, tokenInfo);

    // Gestionar sesión activa
    registerActiveSession(sessionInfo);

    logger.info("✅ TokenService.generateTokenPairWithSession - Tokens y sesión generados para usuario {}", userId);
    return new TokenPair(accessToken, refreshToken);
  }

  /**
   * Refresca un access token usando un refresh token válido
   */
  public TokenPair refreshAccessToken(String refreshToken) {
    logger.debug("🔍 TokenService.refreshAccessToken - Refrescando token"); // Validar refresh token no nulo
    if (refreshToken == null || refreshToken.trim().isEmpty()) {
      throw new IllegalArgumentException("Refresh token inválido o expirado");
    }

    // Validar refresh token
    RefreshTokenInfo tokenInfo = getRefreshTokenInfo(refreshToken);
    if (tokenInfo == null) {
      throw new IllegalArgumentException("Refresh token inválido o expirado");
    }

    if (Instant.now().isAfter(tokenInfo.getExpiresAt())) {
      removeRefreshToken(refreshToken);
      throw new IllegalArgumentException("Refresh token expirado");
    }

    // Verificar si la sesión sigue siendo válida
    SessionInfo sessionInfo = getSessionByRefreshToken(refreshToken);
    if (sessionInfo != null && isSessionExpired(sessionInfo)) {
      invalidateSession(sessionInfo.getSessionId());
      throw new IllegalArgumentException("Sesión expirada");
    }

    // Generar nuevo par de tokens
    TokenPair newTokenPair = generateTokenPair(tokenInfo.getUserId(), tokenInfo.isAdmin());

    // Invalidar el refresh token anterior
    removeRefreshToken(refreshToken);

    logger.info("✅ TokenService.refreshAccessToken - Token refrescado para usuario {}", tokenInfo.getUserId());
    return newTokenPair;
  }

  /**
   * Invalida un token (logout)
   */
  public void invalidateToken(String accessToken) {
    logger.debug("🔍 TokenService.invalidateToken - Invalidando token");

    if (accessToken != null && !accessToken.trim().isEmpty()) {
      addToBlacklist(accessToken);

      // Invalidar sesión asociada
      SessionInfo sessionInfo = sessionsByToken.get(accessToken);
      if (sessionInfo != null) {
        invalidateSession(sessionInfo.getSessionId());
      }

      logger.info("✅ TokenService.invalidateToken - Token añadido a blacklist");
    }
  }

  /**
   * Invalida todos los tokens de un usuario
   */
  public void invalidateAllUserTokens(Long userId) {
    logger.debug("🔍 TokenService.invalidateAllUserTokens - Invalidando todos los tokens del usuario {}", userId);

    // Remover todos los refresh tokens del usuario
    refreshTokenStore.entrySet().removeIf(entry -> entry.getValue().getUserId().equals(userId));

    // Invalidar todas las sesiones del usuario
    Set<SessionInfo> userSessions = activeSessions.get(userId);
    if (userSessions != null) {
      for (SessionInfo session : userSessions) {
        addToBlacklist(session.getAccessToken());
        sessionsByToken.remove(session.getAccessToken());
      }
      activeSessions.remove(userId);
    }

    // Invalidar todos los access tokens del usuario (sin sesión)
    Set<String> userTokens = userAccessTokens.get(userId);
    if (userTokens != null) {
      for (String accessToken : userTokens) {
        addToBlacklist(accessToken);
      }
      userAccessTokens.remove(userId);
    }

    logger.info("✅ TokenService.invalidateAllUserTokens - Tokens invalidados para usuario {}", userId);
  }

  /**
   * Verifica si un token está en la blacklist
   */
  public boolean isTokenBlacklisted(String token) {
    return tokenBlacklist.contains(token);
  }

  /**
   * Obtiene las sesiones activas de un usuario
   */
  public List<SessionInfo> getActiveSessions(Long userId) {
    Set<SessionInfo> userSessions = activeSessions.get(userId);
    if (userSessions == null) {
      return new ArrayList<>();
    }

    // Filtrar sesiones expiradas
    userSessions.removeIf(this::isSessionExpired);

    return new ArrayList<>(userSessions);
  }

  /**
   * Invalida una sesión específica
   */
  public void invalidateSession(String sessionId) {
    logger.debug("🔍 TokenService.invalidateSession - Invalidando sesión {}", sessionId);

    // Buscar y remover la sesión
    for (Map.Entry<Long, Set<SessionInfo>> entry : activeSessions.entrySet()) {
      Set<SessionInfo> sessions = entry.getValue();
      SessionInfo toRemove = null;

      for (SessionInfo session : sessions) {
        if (session.getSessionId().equals(sessionId)) {
          toRemove = session;
          break;
        }
      }

      if (toRemove != null) {
        sessions.remove(toRemove);
        sessionsByToken.remove(toRemove.getAccessToken());
        addToBlacklist(toRemove.getAccessToken());

        if (sessions.isEmpty()) {
          activeSessions.remove(entry.getKey());
        }
        break;
      }
    }

    logger.info("✅ TokenService.invalidateSession - Sesión {} invalidada", sessionId);
  }

  /**
   * Limpia tokens expirados periódicamente
   */
  public void cleanupExpiredTokens() {
    logger.debug("🔍 TokenService.cleanupExpiredTokens - Limpiando tokens expirados");

    Instant now = Instant.now();
    int removedTokens = 0;
    int removedSessions = 0;

    // Limpiar refresh tokens expirados
    refreshTokenStore.entrySet().removeIf(entry -> now.isAfter(entry.getValue().getExpiresAt()));

    // Limpiar sesiones expiradas
    final int[] sessionsRemoved = { 0 }; // Array para hacerlo efectivamente final
    for (Map.Entry<Long, Set<SessionInfo>> entry : activeSessions.entrySet()) {
      Set<SessionInfo> sessions = entry.getValue();
      sessions.removeIf(session -> {
        if (isSessionExpired(session)) {
          sessionsByToken.remove(session.getAccessToken());
          addToBlacklist(session.getAccessToken());
          sessionsRemoved[0]++;
          return true;
        }
        return false;
      });

      if (sessions.isEmpty()) {
        activeSessions.remove(entry.getKey());
      }
    }
    removedSessions = sessionsRemoved[0];

    logger.debug("✅ TokenService.cleanupExpiredTokens - {} tokens y {} sesiones expiradas eliminadas",
        removedTokens, removedSessions);
  }

  // Métodos privados auxiliares

  private void enforceSessionLimits(Long userId) {
    Set<SessionInfo> userSessions = activeSessions.get(userId);
    if (userSessions == null) {
      return;
    }

    // Limpiar sesiones expiradas primero
    userSessions.removeIf(this::isSessionExpired);

    // Verificar límite de sesiones concurrentes
    int maxSessions = jwtProperties.getMaxConcurrentSessions();
    if (userSessions.size() >= maxSessions) {
      // Remover la sesión más antigua
      SessionInfo oldestSession = userSessions.stream()
          .min((s1, s2) -> s1.getCreatedAt().compareTo(s2.getCreatedAt()))
          .orElse(null);

      if (oldestSession != null) {
        invalidateSession(oldestSession.getSessionId());
        logger.info("Sesión más antigua invalidada para usuario {} por límite de sesiones concurrentes", userId);
      }
    }
  }

  private void registerActiveSession(SessionInfo sessionInfo) {
    Long userId = sessionInfo.getUserId();

    activeSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionInfo);
    sessionsByToken.put(sessionInfo.getAccessToken(), sessionInfo);

    logger.debug("Sesión {} registrada para usuario {}", sessionInfo.getSessionId(), userId);
  }

  private SessionInfo getSessionByRefreshToken(String refreshToken) {
    for (Set<SessionInfo> sessions : activeSessions.values()) {
      for (SessionInfo session : sessions) {
        if (session.getRefreshToken().equals(refreshToken)) {
          return session;
        }
      }
    }
    return null;
  }

  private boolean isSessionExpired(SessionInfo session) {
    Instant now = Instant.now();
    long sessionTimeoutSeconds = jwtProperties.getSessionTimeoutSeconds();

    // Verificar timeout por inactividad
    if (sessionTimeoutSeconds >= 0) {
      Instant expirationTime = session.getLastActivity().plusSeconds(sessionTimeoutSeconds);
      return now.isAfter(expirationTime) || (sessionTimeoutSeconds == 0 && !now.isBefore(expirationTime));
    }

    // Verificar expiración absoluta (basada en creación)
    long maxSessionDurationSeconds = jwtProperties.getMaxSessionDurationSeconds();
    if (maxSessionDurationSeconds >= 0) {
      Instant expirationTime = session.getCreatedAt().plusSeconds(maxSessionDurationSeconds);
      return now.isAfter(expirationTime) || (maxSessionDurationSeconds == 0 && !now.isBefore(expirationTime));
    }

    return false;
  }

  private String generateRefreshToken(Long userId) {
    return UUID.randomUUID().toString() + "-" + userId + "-" + System.currentTimeMillis();
  }

  private void storeRefreshToken(String refreshToken, RefreshTokenInfo tokenInfo) {
    refreshTokenStore.put(refreshToken, tokenInfo);
  }

  private RefreshTokenInfo getRefreshTokenInfo(String refreshToken) {
    return refreshTokenStore.get(refreshToken);
  }

  private void removeRefreshToken(String refreshToken) {
    refreshTokenStore.remove(refreshToken);
  }

  private void addToBlacklist(String token) {
    tokenBlacklist.add(token);
  }

  // Clases auxiliares

  public static class TokenPair {
    private final String accessToken;
    private final String refreshToken;

    public TokenPair(String accessToken, String refreshToken) {
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
      return accessToken;
    }

    public String getRefreshToken() {
      return refreshToken;
    }
  }

  private static class RefreshTokenInfo {
    private final Long userId;
    private final boolean isAdmin;
    private final Instant expiresAt;

    public RefreshTokenInfo(Long userId, boolean isAdmin, Instant expiresAt) {
      this.userId = userId;
      this.isAdmin = isAdmin;
      this.expiresAt = expiresAt;
    }

    public Long getUserId() {
      return userId;
    }

    public boolean isAdmin() {
      return isAdmin;
    }

    public Instant getExpiresAt() {
      return expiresAt;
    }
  }

  public static class SessionInfo {
    private final String sessionId;
    private final Long userId;
    private final String accessToken;
    private final String refreshToken;
    private final Instant createdAt;
    private final Instant lastActivity;
    private final String userAgent;
    private final String ipAddress;

    public SessionInfo(String sessionId, Long userId, String accessToken, String refreshToken,
        String userAgent, String ipAddress) {
      this.sessionId = sessionId;
      this.userId = userId;
      this.accessToken = accessToken;
      this.refreshToken = refreshToken;
      this.createdAt = Instant.now();
      this.lastActivity = Instant.now();
      this.userAgent = userAgent;
      this.ipAddress = ipAddress;
    }

    public String getSessionId() {
      return sessionId;
    }

    public Long getUserId() {
      return userId;
    }

    public String getAccessToken() {
      return accessToken;
    }

    public String getRefreshToken() {
      return refreshToken;
    }

    public Instant getCreatedAt() {
      return createdAt;
    }

    public Instant getLastActivity() {
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