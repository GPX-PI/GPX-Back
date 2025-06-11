package com.udea.gpx.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import com.udea.gpx.model.User;
import com.udea.gpx.service.UserService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("AuthUtils Tests")
class AuthUtilsTest {

  @Mock
  private UserService userService;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private AuthUtils authUtils;

  private User testUser;
  private User adminUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    SecurityContextHolder.setContext(securityContext);

    testUser = TestDataBuilder.buildUser(1L, "TestUser", false);
    adminUser = TestDataBuilder.buildUser(2L, "AdminUser", true);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  // ========== GET CURRENT USER TESTS ==========

  @Test
  @DisplayName("getCurrentUser - Debe retornar null cuando no hay autenticación")
  void getCurrentUser_shouldReturnNullWhenNoAuthentication() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(null);

    // When
    User result = authUtils.getCurrentUser();

    // Then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("getCurrentUser - Debe retornar null cuando usuario no está autenticado")
  void getCurrentUser_shouldReturnNullWhenNotAuthenticated() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(false);

    // When
    User result = authUtils.getCurrentUser();

    // Then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("getCurrentUser - Debe retornar usuario cuando principal es User")
  void getCurrentUser_shouldReturnUserWhenPrincipalIsUser() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(testUser);

    // When
    User result = authUtils.getCurrentUser();

    // Then
    assertThat(result).isEqualTo(testUser);
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getFirstName()).isEqualTo("TestUser");
  }

  @Test
  @DisplayName("getCurrentUser - Debe retornar usuario OAuth2 por email")
  void getCurrentUser_shouldReturnOAuth2UserByEmail() {
    // Given
    String email = "oauth.user@test.com";
    User oauthUser = TestDataBuilder.buildUser(3L, "OAuthUser", false);

    // Crear mock de DefaultOidcUser
    OidcIdToken idToken = createMockOidcIdToken(email);
    DefaultOidcUser oidcUser = new DefaultOidcUser(
        List.of(() -> "USER"),
        idToken);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(oidcUser);
    when(userService.findByEmail(email)).thenReturn(oauthUser);

    // When
    User result = authUtils.getCurrentUser();

    // Then
    assertThat(result).isEqualTo(oauthUser);
    verify(userService).findByEmail(email);
  }

  @Test
  @DisplayName("getCurrentUser - Debe retornar null para OAuth2 sin email")
  void getCurrentUser_shouldReturnNullForOAuth2WithoutEmail() {
    // Given
    OidcIdToken idToken = createMockOidcIdToken(null);
    DefaultOidcUser oidcUser = new DefaultOidcUser(
        List.of(() -> "USER"),
        idToken);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(oidcUser);

    // When
    User result = authUtils.getCurrentUser();

    // Then
    assertThat(result).isNull();
    verify(userService, never()).findByEmail(any());
  }

  @Test
  @DisplayName("getCurrentUser - Debe retornar null para principal desconocido")
  void getCurrentUser_shouldReturnNullForUnknownPrincipal() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn("unknown-principal");

    // When
    User result = authUtils.getCurrentUser();

    // Then
    assertThat(result).isNull();
  }

  // ========== IS CURRENT USER ADMIN TESTS ==========

  @Test
  @DisplayName("isCurrentUserAdmin - Debe retornar true para usuario admin")
  void isCurrentUserAdmin_shouldReturnTrueForAdminUser() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(adminUser);

    // When
    boolean result = authUtils.isCurrentUserAdmin();

    // Then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("isCurrentUserAdmin - Debe retornar false para usuario normal")
  void isCurrentUserAdmin_shouldReturnFalseForNormalUser() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(testUser);

    // When
    boolean result = authUtils.isCurrentUserAdmin();

    // Then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("isCurrentUserAdmin - Debe retornar false cuando no hay usuario")
  void isCurrentUserAdmin_shouldReturnFalseWhenNoUser() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(null);

    // When
    boolean result = authUtils.isCurrentUserAdmin();

    // Then
    assertThat(result).isFalse();
  }

  // ========== GET CURRENT USER ID TESTS ==========

  @Test
  @DisplayName("getCurrentUserId - Debe retornar ID del usuario actual")
  void getCurrentUserId_shouldReturnCurrentUserId() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(testUser);

    // When
    Long result = authUtils.getCurrentUserId();

    // Then
    assertThat(result).isEqualTo(1L);
  }

  @Test
  @DisplayName("getCurrentUserId - Debe retornar null cuando no hay usuario")
  void getCurrentUserId_shouldReturnNullWhenNoUser() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(null);

    // When
    Long result = authUtils.getCurrentUserId();

    // Then
    assertThat(result).isNull();
  }

  // ========== IS CURRENT USER OR ADMIN TESTS ==========

  @Test
  @DisplayName("isCurrentUserOrAdmin - Debe retornar true para admin")
  void isCurrentUserOrAdmin_shouldReturnTrueForAdmin() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(adminUser);

    // When
    boolean result = authUtils.isCurrentUserOrAdmin(999L);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("isCurrentUserOrAdmin - Debe retornar true para mismo usuario")
  void isCurrentUserOrAdmin_shouldReturnTrueForSameUser() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(testUser);

    // When
    boolean result = authUtils.isCurrentUserOrAdmin(1L);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("isCurrentUserOrAdmin - Debe retornar false para usuario diferente")
  void isCurrentUserOrAdmin_shouldReturnFalseForDifferentUser() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(testUser);

    // When
    boolean result = authUtils.isCurrentUserOrAdmin(999L);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("isCurrentUserOrAdmin - Debe retornar false cuando no hay usuario")
  void isCurrentUserOrAdmin_shouldReturnFalseWhenNoUser() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(null);

    // When
    boolean result = authUtils.isCurrentUserOrAdmin(1L);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("isCurrentUserOrAdmin - Debe retornar false para userId null")
  void isCurrentUserOrAdmin_shouldReturnFalseForNullUserId() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(testUser);

    // When
    boolean result = authUtils.isCurrentUserOrAdmin(null);

    // Then
    assertThat(result).isFalse();
  }

  // ========== INTEGRATION TESTS ==========

  @Test
  @DisplayName("getCurrentUser - Debe manejar OAuth2 user que no existe en BD")
  void getCurrentUser_shouldHandleOAuth2UserNotInDatabase() {
    // Given
    String email = "nonexistent@test.com";
    OidcIdToken idToken = createMockOidcIdToken(email);
    DefaultOidcUser oidcUser = new DefaultOidcUser(
        List.of(() -> "USER"),
        idToken);

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(oidcUser);
    when(userService.findByEmail(email)).thenReturn(null);

    // When
    User result = authUtils.getCurrentUser();

    // Then
    assertThat(result).isNull();
    verify(userService).findByEmail(email);
  }

  @Test
  @DisplayName("Flow completo - Admin puede acceder a cualquier usuario")
  void fullFlow_adminCanAccessAnyUser() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(adminUser);

    // When
    User currentUser = authUtils.getCurrentUser();
    boolean isAdmin = authUtils.isCurrentUserAdmin();
    Long userId = authUtils.getCurrentUserId();
    boolean canAccessOtherUser = authUtils.isCurrentUserOrAdmin(999L);

    // Then
    assertThat(currentUser).isEqualTo(adminUser);
    assertThat(isAdmin).isTrue();
    assertThat(userId).isEqualTo(2L);
    assertThat(canAccessOtherUser).isTrue();
  }

  @Test
  @DisplayName("Flow completo - Usuario normal solo puede acceder a sí mismo")
  void fullFlow_normalUserCanOnlyAccessSelf() {
    // Given
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(testUser);

    // When
    User currentUser = authUtils.getCurrentUser();
    boolean isAdmin = authUtils.isCurrentUserAdmin();
    Long userId = authUtils.getCurrentUserId();
    boolean canAccessSelf = authUtils.isCurrentUserOrAdmin(1L);
    boolean canAccessOther = authUtils.isCurrentUserOrAdmin(999L);

    // Then
    assertThat(currentUser).isEqualTo(testUser);
    assertThat(isAdmin).isFalse();
    assertThat(userId).isEqualTo(1L);
    assertThat(canAccessSelf).isTrue();
    assertThat(canAccessOther).isFalse();
  }

  // ========== HELPER METHODS ==========

  private OidcIdToken createMockOidcIdToken(String email) {
    Map<String, Object> claims;
    if (email != null) {
      claims = Map.of(
          "sub", "oauth-user-123",
          "email", email,
          "name", "OAuth User");
    } else {
      claims = Map.of(
          "sub", "oauth-user-123",
          "name", "OAuth User");
    }

    return new OidcIdToken(
        "mock-token",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        claims);
  }
}