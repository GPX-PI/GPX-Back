package com.udea.gpx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.udea.gpx.JwtUtil;
import com.udea.gpx.config.JwtProperties;
import com.udea.gpx.service.TokenService.SessionInfo;
import com.udea.gpx.service.TokenService.TokenPair;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TokenService Tests")
class TokenServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private TokenService tokenService;

    private final Long testUserId = 1L;
    private final boolean isAdmin = false;
    private final String testAccessToken = "test.access.token";
    private final String userAgent = "Mozilla/5.0 Test Browser";
    private final String ipAddress = "192.168.1.1";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup default mocks
        when(jwtProperties.getRefreshExpirationSeconds()).thenReturn(86400L); // 24 hours
        when(jwtProperties.getSessionTimeoutSeconds()).thenReturn(3600L); // 1 hour
        when(jwtProperties.getMaxSessionDurationSeconds()).thenReturn(86400L); // 24 hours
        when(jwtProperties.getMaxConcurrentSessions()).thenReturn(5);
    }

    // ========== GENERATE TOKEN PAIR TESTS ==========

    @Test
    @DisplayName("generateTokenPair - Debe generar par de tokens exitosamente")
    void generateTokenPair_shouldGenerateTokenPairSuccessfully() {
        // Given
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);

        // When
        TokenPair result = tokenService.generateTokenPair(testUserId, isAdmin);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(testAccessToken);
        assertThat(result.getRefreshToken()).isNotNull();
        verify(jwtUtil).generateToken(testUserId, isAdmin);
    }

    @Test
    @DisplayName("generateTokenPairWithSession - Debe generar tokens con información de sesión")
    void generateTokenPairWithSession_shouldGenerateTokensWithSessionInfo() {
        // Given
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);

        // When
        TokenPair result = tokenService.generateTokenPairWithSession(testUserId, isAdmin, userAgent, ipAddress);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(testAccessToken);
        assertThat(result.getRefreshToken()).isNotNull();
        verify(jwtUtil).generateToken(testUserId, isAdmin);
    }

    @Test
    @DisplayName("generateTokenPair - Debe generar diferentes refresh tokens para llamadas múltiples")
    void generateTokenPair_shouldGenerateDifferentRefreshTokensForMultipleCalls() {
        // Given
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);

        // When
        TokenPair result1 = tokenService.generateTokenPair(testUserId, isAdmin);
        TokenPair result2 = tokenService.generateTokenPair(testUserId, isAdmin);

        // Then
        assertThat(result1.getRefreshToken()).isNotEqualTo(result2.getRefreshToken());
        verify(jwtUtil, times(2)).generateToken(testUserId, isAdmin);
    }

    // ========== REFRESH ACCESS TOKEN TESTS ==========

    @Test
    @DisplayName("refreshAccessToken - Debe refrescar token con refresh token válido")
    void refreshAccessToken_shouldRefreshTokenWithValidRefreshToken() {
        // Given
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);

        // Primero generar un par de tokens
        TokenPair originalPair = tokenService.generateTokenPair(testUserId, isAdmin);

        String newAccessToken = "new.access.token";
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(newAccessToken);

        // When
        TokenPair refreshedPair = tokenService.refreshAccessToken(originalPair.getRefreshToken());

        // Then
        assertThat(refreshedPair).isNotNull();
        assertThat(refreshedPair.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(refreshedPair.getRefreshToken()).isNotNull();
    }

    @Test
    @DisplayName("refreshAccessToken - Debe lanzar excepción con refresh token inválido")
    void refreshAccessToken_shouldThrowExceptionWithInvalidRefreshToken() {
        // Given
        String invalidRefreshToken = "invalid-refresh-token";

        // When & Then
        assertThatThrownBy(() -> tokenService.refreshAccessToken(invalidRefreshToken))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Refresh token inválido o expirado");
    }

    @Test
    @DisplayName("refreshAccessToken - Debe lanzar excepción con refresh token null")
    void refreshAccessToken_shouldThrowExceptionWithNullRefreshToken() {
        // When & Then
        assertThatThrownBy(() -> tokenService.refreshAccessToken(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Refresh token inválido o expirado");
    }

    // ========== INVALIDATE TOKEN TESTS ==========

    @Test
    @DisplayName("invalidateToken - Debe invalidar token exitosamente")
    void invalidateToken_shouldInvalidateTokenSuccessfully() {
        // Given
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);
        TokenPair tokenPair = tokenService.generateTokenPair(testUserId, isAdmin);

        // When
        tokenService.invalidateToken(tokenPair.getAccessToken());

        // Then
        assertThat(tokenService.isTokenBlacklisted(tokenPair.getAccessToken())).isTrue();
    }

    @Test
    @DisplayName("invalidateToken - Debe manejar token null graciosamente")
    void invalidateToken_shouldHandleNullTokenGracefully() {
        // When & Then
        assertThatCode(() -> tokenService.invalidateToken(null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("invalidateToken - Debe manejar token vacío graciosamente")
    void invalidateToken_shouldHandleEmptyTokenGracefully() {
        // When & Then
        assertThatCode(() -> tokenService.invalidateToken("   "))
                .doesNotThrowAnyException();
    }

    // ========== INVALIDATE ALL USER TOKENS TESTS ==========

    @Test
    @DisplayName("invalidateAllUserTokens - Debe invalidar todos los tokens del usuario")
    void invalidateAllUserTokens_shouldInvalidateAllUserTokens() {
        // Given
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);

        // Generar múltiples tokens para el usuario
        TokenPair token1 = tokenService.generateTokenPair(testUserId, isAdmin);
        TokenPair token2 = tokenService.generateTokenPair(testUserId, isAdmin);

        // When
        tokenService.invalidateAllUserTokens(testUserId);

        // Then
        assertThat(tokenService.isTokenBlacklisted(token1.getAccessToken())).isTrue();
        assertThat(tokenService.isTokenBlacklisted(token2.getAccessToken())).isTrue();
    }

    @Test
    @DisplayName("invalidateAllUserTokens - Debe manejar usuario sin tokens activos")
    void invalidateAllUserTokens_shouldHandleUserWithoutActiveTokens() {
        // When & Then
        assertThatCode(() -> tokenService.invalidateAllUserTokens(999L))
                .doesNotThrowAnyException();
    }

    // ========== IS TOKEN BLACKLISTED TESTS ==========

    @Test
    @DisplayName("isTokenBlacklisted - Debe retornar false para token no blacklisted")
    void isTokenBlacklisted_shouldReturnFalseForNonBlacklistedToken() {
        // Given
        String cleanToken = "clean.token.123";

        // When
        boolean result = tokenService.isTokenBlacklisted(cleanToken);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isTokenBlacklisted - Debe retornar true para token blacklisted")
    void isTokenBlacklisted_shouldReturnTrueForBlacklistedToken() {
        // Given
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);
        TokenPair tokenPair = tokenService.generateTokenPair(testUserId, isAdmin);
        tokenService.invalidateToken(tokenPair.getAccessToken());

        // When
        boolean result = tokenService.isTokenBlacklisted(tokenPair.getAccessToken());

        // Then
        assertThat(result).isTrue();
    }

    // ========== GET ACTIVE SESSIONS TESTS ==========

    @Test
    @DisplayName("getActiveSessions - Debe retornar sesiones activas del usuario")
    void getActiveSessions_shouldReturnActiveUserSessions() {
        // Given
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);

        // Generar tokens con sesión
        tokenService.generateTokenPairWithSession(testUserId, isAdmin, userAgent, ipAddress);

        // When
        List<SessionInfo> sessions = tokenService.getActiveSessions(testUserId);

        // Then
        assertThat(sessions).isNotEmpty();
        assertThat(sessions.get(0).getUserId()).isEqualTo(testUserId);
        assertThat(sessions.get(0).getUserAgent()).isEqualTo(userAgent);
        assertThat(sessions.get(0).getIpAddress()).isEqualTo(ipAddress);
    }

    @Test
    @DisplayName("getActiveSessions - Debe retornar lista vacía para usuario sin sesiones")
    void getActiveSessions_shouldReturnEmptyListForUserWithoutSessions() {
        // When
        List<SessionInfo> sessions = tokenService.getActiveSessions(999L);

        // Then
        assertThat(sessions).isEmpty();
    }

    @Test
    @DisplayName("getActiveSessions - Debe filtrar sesiones expiradas")
    void getActiveSessions_shouldFilterExpiredSessions() {
        // Given
        when(jwtProperties.getSessionTimeoutSeconds()).thenReturn(0L); // Expiración inmediata
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);

        // Generar sesión que expirará inmediatamente
        tokenService.generateTokenPairWithSession(testUserId, isAdmin, userAgent, ipAddress);

        // When
        List<SessionInfo> sessions = tokenService.getActiveSessions(testUserId);

        // Then
        assertThat(sessions).isEmpty();
    }

    // ========== INVALIDATE SESSION TESTS ==========

    @Test
    @DisplayName("invalidateSession - Debe invalidar sesión específica")
    void invalidateSession_shouldInvalidateSpecificSession() {
        // Given
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);
        TokenPair tokenPair = tokenService.generateTokenPairWithSession(testUserId, isAdmin, userAgent, ipAddress);

        List<SessionInfo> sessions = tokenService.getActiveSessions(testUserId);
        assertThat(sessions).hasSize(1);
        String sessionId = sessions.get(0).getSessionId();

        // When
        tokenService.invalidateSession(sessionId);

        // Then
        List<SessionInfo> remainingSessions = tokenService.getActiveSessions(testUserId);
        assertThat(remainingSessions).isEmpty();
        assertThat(tokenService.isTokenBlacklisted(tokenPair.getAccessToken())).isTrue();
    }

    @Test
    @DisplayName("invalidateSession - Debe manejar sesión inexistente graciosamente")
    void invalidateSession_shouldHandleNonExistentSessionGracefully() {
        // When & Then
        assertThatCode(() -> tokenService.invalidateSession("non-existent-session-id"))
                .doesNotThrowAnyException();
    }

    // ========== CLEANUP EXPIRED TOKENS TESTS ==========

    @Test
    @DisplayName("cleanupExpiredTokens - Debe limpiar tokens expirados")
    void cleanupExpiredTokens_shouldCleanupExpiredTokens() {
        // Given
        when(jwtProperties.getRefreshExpirationSeconds()).thenReturn(0L); // Expiración inmediata
        when(jwtProperties.getSessionTimeoutSeconds()).thenReturn(0L); // Expiración inmediata
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);

        // Generar tokens que expirarán inmediatamente
        tokenService.generateTokenPair(testUserId, isAdmin);
        tokenService.generateTokenPairWithSession(testUserId, isAdmin, userAgent, ipAddress);

        // When
        tokenService.cleanupExpiredTokens();

        // Then
        List<SessionInfo> sessions = tokenService.getActiveSessions(testUserId);
        assertThat(sessions).isEmpty();
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("generateTokenPair - Debe manejar usuario admin correctamente")
    void generateTokenPair_shouldHandleAdminUserCorrectly() {
        // Given
        String adminAccessToken = "admin.access.token";
        when(jwtUtil.generateToken(testUserId, true)).thenReturn(adminAccessToken);

        // When
        TokenPair result = tokenService.generateTokenPair(testUserId, true);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(adminAccessToken);
        verify(jwtUtil).generateToken(testUserId, true);
    }

    @Test
    @DisplayName("generateTokenPairWithSession - Debe manejar user agent y IP nulos")
    void generateTokenPairWithSession_shouldHandleNullUserAgentAndIp() {
        // Given
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);

        // When
        TokenPair result = tokenService.generateTokenPairWithSession(testUserId, isAdmin, null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(testAccessToken);

        List<SessionInfo> sessions = tokenService.getActiveSessions(testUserId);
        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).getUserAgent()).isNull();
        assertThat(sessions.get(0).getIpAddress()).isNull();
    }

    @Test
    @DisplayName("Session limits - Debe enforcar límites de sesiones concurrentes")
    void sessionLimits_shouldEnforceConcurrentSessionLimits() {
        // Given
        when(jwtProperties.getMaxConcurrentSessions()).thenReturn(2);
        when(jwtUtil.generateToken(testUserId, isAdmin)).thenReturn(testAccessToken);

        // When - Generar más sesiones que el límite
        tokenService.generateTokenPairWithSession(testUserId, isAdmin, "Browser1", "IP1");
        tokenService.generateTokenPairWithSession(testUserId, isAdmin, "Browser2", "IP2");
        tokenService.generateTokenPairWithSession(testUserId, isAdmin, "Browser3", "IP3"); // Debería eliminar la más
                                                                                           // antigua

        // Then
        List<SessionInfo> sessions = tokenService.getActiveSessions(testUserId);
        assertThat(sessions).hasSize(2); // No debe exceder el límite
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    @DisplayName("Flow completo - Generar, refrescar, invalidar tokens")
    void fullFlow_generateRefreshInvalidateTokens() {
        // Given
        when(jwtUtil.generateToken(testUserId, isAdmin))
                .thenReturn(testAccessToken)
                .thenReturn("refreshed.access.token");

        // When - Generate
        TokenPair originalPair = tokenService.generateTokenPair(testUserId, isAdmin);

        // Then - Verify generation
        assertThat(originalPair.getAccessToken()).isEqualTo(testAccessToken);
        assertThat(tokenService.isTokenBlacklisted(originalPair.getAccessToken())).isFalse();

        // When - Refresh
        TokenPair refreshedPair = tokenService.refreshAccessToken(originalPair.getRefreshToken());

        // Then - Verify refresh
        assertThat(refreshedPair.getAccessToken()).isEqualTo("refreshed.access.token");

        // When - Invalidate
        tokenService.invalidateToken(refreshedPair.getAccessToken());

        // Then - Verify invalidation
        assertThat(tokenService.isTokenBlacklisted(refreshedPair.getAccessToken())).isTrue();
    }

    @Test
    @DisplayName("Multiple users - Debe aislar tokens entre diferentes usuarios")
    void multipleUsers_shouldIsolateTokensBetweenDifferentUsers() {
        // Given
        Long user1Id = 1L;
        Long user2Id = 2L;
        when(jwtUtil.generateToken(user1Id, isAdmin)).thenReturn("user1.token");
        when(jwtUtil.generateToken(user2Id, isAdmin)).thenReturn("user2.token");

        // When
        TokenPair user1Tokens = tokenService.generateTokenPair(user1Id, isAdmin);
        TokenPair user2Tokens = tokenService.generateTokenPair(user2Id, isAdmin);

        // Invalidar tokens del usuario 1
        tokenService.invalidateAllUserTokens(user1Id);

        // Then
        assertThat(tokenService.isTokenBlacklisted(user1Tokens.getAccessToken())).isTrue();
        assertThat(tokenService.isTokenBlacklisted(user2Tokens.getAccessToken())).isFalse();

        List<SessionInfo> user1Sessions = tokenService.getActiveSessions(user1Id);
        List<SessionInfo> user2Sessions = tokenService.getActiveSessions(user2Id);

        assertThat(user1Sessions).isEmpty();
        assertThat(user2Sessions).isEmpty(); // Porque no se generaron con sesión
    }
}
