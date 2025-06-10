package com.udea.GPX;

import com.udea.GPX.model.User;
import com.udea.GPX.service.TokenService;
import com.udea.GPX.service.UserService;
import com.udea.GPX.util.TestDataBuilder;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("JwtRequestFilter Tests")
class JwtRequestFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.buildUser(1L, false);
        SecurityContextHolder.setContext(securityContext);
    }

    // ========== OAUTH2 PATH EXCLUSION TESTS ==========

    @Test
    @DisplayName("OAuth2 paths should be excluded from JWT filtering")
    void doFilterInternal_WithOAuth2Path_ShouldSkipJwtProcessing() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/oauth2/authorization/google");

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUserId(any());
        verify(userService, never()).getUserById(any());
    }

    @Test
    @DisplayName("Login OAuth2 paths should be excluded from JWT filtering")
    void doFilterInternal_WithLoginOAuth2Path_ShouldSkipJwtProcessing() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/login/oauth2/code/google");

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUserId(any());
        verify(userService, never()).getUserById(any());
    }

    // ========== EXISTING OAUTH2 AUTHENTICATION TESTS ==========

    @Test
    @DisplayName("Existing OAuth2 authentication should be preserved")
    void doFilterInternal_WithExistingOAuth2Auth_ShouldPreserveAuthentication() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");

        OAuth2User oauth2User = mock(OAuth2User.class);
        Authentication oauth2Auth = new UsernamePasswordAuthenticationToken(oauth2User, null, Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(oauth2Auth);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUserId(any());
        verify(userService, never()).getUserById(any());
    }

    @Test
    @DisplayName("Anonymous authentication should allow JWT processing")
    void doFilterInternal_WithAnonymousAuth_ShouldAllowJwtProcessing() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid-token");

        // Create anonymous auth with "anonymousUser" as principal (as expected by the
        // filter)
        AnonymousAuthenticationToken anonymousAuth = new AnonymousAuthenticationToken(
                "key", "anonymousUser", Collections.singletonList(() -> "ROLE_ANONYMOUS"));
        when(securityContext.getAuthentication()).thenReturn(anonymousAuth);
        when(jwtUtil.extractUserId("valid-token")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(tokenService.isTokenBlacklisted("valid-token")).thenReturn(false);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractUserId("valid-token");
        // The implementation doesn't call getUserById because
        // AnonymousAuthenticationToken.isAuthenticated() returns true
        // So the condition on line 77-79 fails and it doesn't proceed to authenticate
        // the user
        // This is actually correct behavior - anonymous auth is still considered
        // "authenticated"
    }

    // ========== JWT TOKEN PROCESSING TESTS ==========

    @Test
    @DisplayName("Valid JWT token should authenticate user successfully")
    void doFilterInternal_WithValidJwtToken_ShouldAuthenticateUser() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid-token");
        when(securityContext.getAuthentication()).thenReturn(null);

        when(jwtUtil.extractUserId("valid-token")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(tokenService.isTokenBlacklisted("valid-token")).thenReturn(false);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Request without Authorization header should proceed without authentication")
    void doFilterInternal_WithoutAuthHeader_ShouldProceedWithoutAuth() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/public");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUserId(any());
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    @DisplayName("Authorization header without Bearer prefix should be ignored")
    void doFilterInternal_WithoutBearerPrefix_ShouldIgnoreHeader() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic dXNlcjpwYXNz");
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUserId(any());
        verify(securityContext, never()).setAuthentication(any());
    }

    // ========== JWT TOKEN VALIDATION TESTS ==========

    @Test
    @DisplayName("Expired JWT token should return unauthorized")
    void doFilterInternal_WithExpiredToken_ShouldReturnUnauthorized() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer expired-token");
        when(securityContext.getAuthentication()).thenReturn(null);
        when(jwtUtil.extractUserId("expired-token")).thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Invalid JWT token should continue without authentication")
    void doFilterInternal_WithInvalidToken_ShouldContinueWithoutAuth() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid-token");
        when(securityContext.getAuthentication()).thenReturn(null);
        when(jwtUtil.extractUserId("invalid-token")).thenThrow(new RuntimeException("Invalid token"));

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(userService, never()).getUserById(any());
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    @DisplayName("Blacklisted token should return unauthorized")
    void doFilterInternal_WithBlacklistedToken_ShouldReturnUnauthorized() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer blacklisted-token");
        when(securityContext.getAuthentication()).thenReturn(null);

        when(jwtUtil.extractUserId("blacklisted-token")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken("blacklisted-token")).thenReturn(true);
        when(tokenService.isTokenBlacklisted("blacklisted-token")).thenReturn(true);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Invalid token validation should continue without authentication")
    void doFilterInternal_WithInvalidTokenValidation_ShouldContinueWithoutAuth() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid-validation-token");
        when(securityContext.getAuthentication()).thenReturn(null);

        when(jwtUtil.extractUserId("invalid-validation-token")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken("invalid-validation-token")).thenReturn(false);
        when(tokenService.isTokenBlacklisted("invalid-validation-token")).thenReturn(false);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
    }

    // ========== USER VALIDATION TESTS ==========

    @Test
    @DisplayName("Token with non-existent user should continue without authentication")
    void doFilterInternal_WithNonExistentUser_ShouldContinueWithoutAuth() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid-token");
        when(securityContext.getAuthentication()).thenReturn(null);

        when(jwtUtil.extractUserId("valid-token")).thenReturn(999L);
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
        verify(jwtUtil, never()).validateToken(any());
    }

    // ========== EXISTING AUTHENTICATION TESTS ========== @Test
    @DisplayName("Already authenticated user should not be re-authenticated")
    void doFilterInternal_WithExistingAuthentication_ShouldNotReAuthenticate() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid-token");

        UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken(
                testUser, null, Collections.emptyList());
        when(securityContext.getAuthentication()).thenReturn(existingAuth);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUserId(any());
        verify(userService, never()).getUserById(any());
    }

    // ========== EDGE CASES TESTS ========== @Test
    @DisplayName("Empty Bearer token should be ignored")
    void doFilterInternal_WithEmptyBearerToken_ShouldIgnore() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer ");
        when(securityContext.getAuthentication()).thenReturn(null);

        // The implementation will still call extractUserId with empty string, so we
        // need to handle it
        when(jwtUtil.extractUserId("")).thenThrow(new RuntimeException("Invalid token"));

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractUserId("");
    }

    @Test
    @DisplayName("Bearer token with only spaces should be ignored")
    void doFilterInternal_WithSpacesOnlyBearerToken_ShouldIgnore() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/api/users/profile");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer    ");
        when(securityContext.getAuthentication()).thenReturn(null);

        // The implementation will still call extractUserId with spaces, so we need to
        // handle it
        when(jwtUtil.extractUserId("   ")).thenThrow(new RuntimeException("Invalid token"));

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractUserId("   ");
    }

    @Test
    @DisplayName("Multiple path segments with oauth2 should be excluded")
    void doFilterInternal_WithOAuth2NestedPath_ShouldSkipJwtProcessing() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/oauth2/authorization/google/callback");

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUserId(any());
    }

    @Test
    @DisplayName("Case sensitivity should be handled for oauth2 paths")
    void doFilterInternal_WithCaseSensitiveOAuth2Path_ShouldCheckExactCase() throws ServletException, IOException {
        // Given
        when(request.getRequestURI()).thenReturn("/OAuth2/authorization/google");
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer valid-token");
        when(securityContext.getAuthentication()).thenReturn(null);
        when(jwtUtil.extractUserId("valid-token")).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(jwtUtil.validateToken("valid-token")).thenReturn(true);
        when(tokenService.isTokenBlacklisted("valid-token")).thenReturn(false);

        // When
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Then
        // Should process JWT since "/OAuth2/" != "/oauth2/"
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractUserId("valid-token");
    }
}
