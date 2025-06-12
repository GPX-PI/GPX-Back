package com.udea.gpx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.servlet.view.RedirectView;

import com.udea.gpx.controller.OAuth2Controller;
import com.udea.gpx.model.User;
import com.udea.gpx.service.OAuth2Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OAuth2Controller Unit Tests")
class OAuth2ControllerTestsNew {

    @Mock
    private OAuth2Service oauth2Service;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private OAuth2Controller oauth2Controller;

    private User testUser;
    private OAuth2User mockOAuth2User;

    @BeforeEach
    void setUp() {
        // Set up the frontend redirect URL using reflection since it's private
        try {
            java.lang.reflect.Field field = OAuth2Controller.class.getDeclaredField("frontendRedirectUrl");
            field.setAccessible(true);
            field.set(oauth2Controller, "http://localhost:3000/oauth2/redirect");
        } catch (Exception e) {
            // If reflection fails, we'll handle this in tests
        }

        // Create test user
        testUser = createUser(1L, "Juan", "Pérez", "juan@gmail.com", false);

        // Create mock OAuth2User
        mockOAuth2User = createMockOAuth2User("juan@gmail.com", "Juan", "Pérez");
    }

    // Helper methods
    private User createUser(Long id, String firstName, String lastName, String email, boolean isAdmin) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setAdmin(isAdmin);
        user.setAuthProvider("GOOGLE");
        user.setGoogleId("google-user-id-123");
        return user;
    }

    private OAuth2User createMockOAuth2User(String email, String firstName, String lastName) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("given_name", firstName);
        attributes.put("family_name", lastName);
        attributes.put("name", firstName + " " + lastName);
        attributes.put("picture", "https://avatar.google.com/test.jpg");
        attributes.put("sub", "google-user-id-123");

        return new DefaultOAuth2User(
                Set.of(),
                attributes,
                "email");
    }

    // ========== OAUTH2 LOGIN SUCCESS TESTS ==========

    @Test
    @DisplayName("oauth2LoginSuccess - Debe redireccionar exitosamente cuando el perfil está completo")
    void oauth2LoginSuccess_whenCompleteProfile_shouldRedirectSuccessfully() {
        // Given
        testUser.setIdentification("12345678");
        testUser.setPhone("3001234567");
        testUser.setRole("piloto");

        when(oauth2Service.processOAuth2User(mockOAuth2User)).thenReturn(testUser);
        when(oauth2Service.isProfileComplete(testUser)).thenReturn(true);
        when(jwtUtil.generateToken(testUser.getId(), testUser.isAdmin())).thenReturn("jwt-token");

        // When
        RedirectView result = oauth2Controller.oauth2LoginSuccess(mockOAuth2User);

        // Then
        assertNotNull(result);
        String resultUrl = result.getUrl();
        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("token=jwt-token"));
        assertTrue(resultUrl.contains("userId=1"));
        assertTrue(resultUrl.contains("admin=false"));
        assertTrue(resultUrl.contains("profileComplete=true"));
        assertTrue(resultUrl.contains("firstName=Juan"));

        verify(oauth2Service).processOAuth2User(mockOAuth2User);
        verify(oauth2Service).isProfileComplete(testUser);
        verify(jwtUtil).generateToken(testUser.getId(), testUser.isAdmin());
    }

    @Test
    @DisplayName("oauth2LoginSuccess - Debe redireccionar cuando el perfil está incompleto")
    void oauth2LoginSuccess_whenIncompleteProfile_shouldRedirectToComplete() {
        // Given
        when(oauth2Service.processOAuth2User(mockOAuth2User)).thenReturn(testUser);
        when(oauth2Service.isProfileComplete(testUser)).thenReturn(false);
        when(jwtUtil.generateToken(testUser.getId(), testUser.isAdmin())).thenReturn("jwt-token");

        // When
        RedirectView result = oauth2Controller.oauth2LoginSuccess(mockOAuth2User);

        // Then
        assertNotNull(result);
        String resultUrl = result.getUrl();
        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("token=jwt-token"));
        assertTrue(resultUrl.contains("userId=1"));
        assertTrue(resultUrl.contains("admin=false"));
        assertTrue(resultUrl.contains("profileComplete=false"));
        assertTrue(resultUrl.contains("firstName=Juan"));

        verify(oauth2Service).processOAuth2User(mockOAuth2User);
        verify(oauth2Service).isProfileComplete(testUser);
        verify(jwtUtil).generateToken(testUser.getId(), testUser.isAdmin());
    }

    @Test
    @DisplayName("oauth2LoginSuccess - Debe incluir flag de admin cuando el usuario es administrador")
    void oauth2LoginSuccess_whenAdminUser_shouldIncludeAdminFlag() {
        // Given
        User adminUser = createUser(1L, "Admin", "User", "admin@gmail.com", true);
        OAuth2User adminOAuth2User = createMockOAuth2User("admin@gmail.com", "Admin", "User");

        when(oauth2Service.processOAuth2User(adminOAuth2User)).thenReturn(adminUser);
        when(oauth2Service.isProfileComplete(adminUser)).thenReturn(true);
        when(jwtUtil.generateToken(adminUser.getId(), adminUser.isAdmin())).thenReturn("jwt-token");

        // When
        RedirectView result = oauth2Controller.oauth2LoginSuccess(adminOAuth2User);

        // Then
        assertNotNull(result);
        String resultUrl = result.getUrl();
        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("admin=true"));
        assertTrue(resultUrl.contains("firstName=Admin"));

        verify(oauth2Service).processOAuth2User(adminOAuth2User);
        verify(oauth2Service).isProfileComplete(adminUser);
        verify(jwtUtil).generateToken(adminUser.getId(), adminUser.isAdmin());
    }

    @Test
    @DisplayName("oauth2LoginSuccess - Debe redireccionar con error cuando OAuth2User es null")
    void oauth2LoginSuccess_whenOAuth2UserIsNull_shouldRedirectWithError() {
        // When
        RedirectView result = oauth2Controller.oauth2LoginSuccess(null);

        // Then
        assertNotNull(result);
        String resultUrl = result.getUrl();
        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("error=oauth2_failed"));
        assertTrue(resultUrl.contains("message="));

        verify(oauth2Service, never()).processOAuth2User(any());
        verify(jwtUtil, never()).generateToken(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("oauth2LoginSuccess - Debe redireccionar con error cuando el procesamiento falla")
    void oauth2LoginSuccess_whenProcessingFails_shouldRedirectWithError() {
        // Given
        when(oauth2Service.processOAuth2User(mockOAuth2User))
                .thenThrow(new RuntimeException("Error al procesar usuario"));

        // When
        RedirectView result = oauth2Controller.oauth2LoginSuccess(mockOAuth2User);

        // Then
        assertNotNull(result);
        String resultUrl = result.getUrl();
        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("error=oauth2_failed"));
        assertTrue(resultUrl.contains("message=Error al procesar usuario"));

        verify(oauth2Service).processOAuth2User(mockOAuth2User);
        verify(jwtUtil, never()).generateToken(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("oauth2LoginSuccess - Debe redireccionar con error cuando la generación de JWT falla")
    void oauth2LoginSuccess_whenJwtGenerationFails_shouldRedirectWithError() {
        // Given
        when(oauth2Service.processOAuth2User(mockOAuth2User)).thenReturn(testUser);
        when(jwtUtil.generateToken(testUser.getId(), testUser.isAdmin()))
                .thenThrow(new RuntimeException("Error al generar JWT"));

        // When
        RedirectView result = oauth2Controller.oauth2LoginSuccess(mockOAuth2User);

        // Then
        assertNotNull(result);
        String resultUrl = result.getUrl();
        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("error=oauth2_failed"));
        assertTrue(resultUrl.contains("message=Error al generar JWT"));

        verify(oauth2Service).processOAuth2User(mockOAuth2User);
        verify(jwtUtil).generateToken(testUser.getId(), testUser.isAdmin());
    }

    @Test
    @DisplayName("oauth2LoginSuccess - Debe manejar gracefully firstName null")
    void oauth2LoginSuccess_withNullFirstName_shouldHandleGracefully() {
        // Given
        User userWithNullName = createUser(1L, null, "User", "test@gmail.com", false);
        OAuth2User oAuth2UserWithNullName = createMockOAuth2User("test@gmail.com", null, "User");

        when(oauth2Service.processOAuth2User(oAuth2UserWithNullName)).thenReturn(userWithNullName);
        when(oauth2Service.isProfileComplete(userWithNullName)).thenReturn(false);
        when(jwtUtil.generateToken(userWithNullName.getId(), userWithNullName.isAdmin())).thenReturn("jwt-token");

        // When
        RedirectView result = oauth2Controller.oauth2LoginSuccess(oAuth2UserWithNullName);

        // Then
        assertNotNull(result);
        String resultUrl = result.getUrl();
        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("firstName="));

        verify(oauth2Service).processOAuth2User(oAuth2UserWithNullName);
        verify(oauth2Service).isProfileComplete(userWithNullName);
        verify(jwtUtil).generateToken(userWithNullName.getId(), userWithNullName.isAdmin());
    }

    // ========== GET PROFILE STATUS TESTS ==========

    @Test
    @DisplayName("getProfileStatus - Debe retornar estado del perfil cuando está completo")
    void getProfileStatus_whenCompleteProfile_shouldReturnComplete() {
        // Given
        testUser.setIdentification("12345678");
        testUser.setPhone("3001234567");
        testUser.setRole("piloto");
        testUser.setPicture("https://avatar.google.com/juan.jpg");

        when(oauth2Service.processOAuth2User(mockOAuth2User)).thenReturn(testUser);
        when(oauth2Service.isProfileComplete(testUser)).thenReturn(true);
        when(oauth2Service.getMissingProfileFields(testUser)).thenReturn(new String[0]);

        // When
        ResponseEntity<Map<String, Object>> response = oauth2Controller.getProfileStatus(mockOAuth2User);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(true, responseBody.get("isComplete"));
        assertEquals(testUser.getId(), responseBody.get("userId"));
        assertEquals("Juan", responseBody.get("firstName"));
        assertEquals("Pérez", responseBody.get("lastName"));
        assertEquals("juan@gmail.com", responseBody.get("email"));

        verify(oauth2Service).processOAuth2User(mockOAuth2User);
        verify(oauth2Service).isProfileComplete(testUser);
        verify(oauth2Service).getMissingProfileFields(testUser);
    }

    @Test
    @DisplayName("getProfileStatus - Debe retornar estado incompleto con campos faltantes")
    void getProfileStatus_whenIncompleteProfile_shouldReturnIncompleteWithMissingFields() {
        // Given
        String[] missingFields = { "identification", "phone", "role" };

        when(oauth2Service.processOAuth2User(mockOAuth2User)).thenReturn(testUser);
        when(oauth2Service.isProfileComplete(testUser)).thenReturn(false);
        when(oauth2Service.getMissingProfileFields(testUser)).thenReturn(missingFields);

        // When
        ResponseEntity<Map<String, Object>> response = oauth2Controller.getProfileStatus(mockOAuth2User);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(false, responseBody.get("isComplete"));
        assertEquals(missingFields, responseBody.get("missingFields"));
        assertEquals(testUser.getId(), responseBody.get("userId"));

        verify(oauth2Service).processOAuth2User(mockOAuth2User);
        verify(oauth2Service).isProfileComplete(testUser);
        verify(oauth2Service).getMissingProfileFields(testUser);
    }

    @Test
    @DisplayName("getProfileStatus - Debe retornar BAD_REQUEST cuando OAuth2User es null")
    void getProfileStatus_whenOAuth2UserIsNull_shouldReturnBadRequest() {
        // When
        ResponseEntity<Map<String, Object>> response = oauth2Controller.getProfileStatus(null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        verify(oauth2Service, never()).processOAuth2User(any());
    }

    // ========== GET USER INFO TESTS ==========

    @Test
    @DisplayName("getCurrentUserInfo - Debe retornar información del usuario OAuth2")
    void getCurrentUserInfo_whenValidOAuth2User_shouldReturnUserInfo() {
        // When
        ResponseEntity<Map<String, Object>> response = oauth2Controller.getCurrentUserInfo(mockOAuth2User);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Juan Pérez", responseBody.get("name"));
        assertEquals("juan@gmail.com", responseBody.get("email"));
        assertEquals("https://avatar.google.com/test.jpg", responseBody.get("picture"));
        assertEquals("google-user-id-123", responseBody.get("sub"));
    }

    @Test
    @DisplayName("getCurrentUserInfo - Debe retornar BAD_REQUEST cuando OAuth2User es null")
    void getCurrentUserInfo_whenOAuth2UserIsNull_shouldReturnBadRequest() {
        // When
        ResponseEntity<Map<String, Object>> response = oauth2Controller.getCurrentUserInfo(null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ========== URL ENCODING TESTS ==========

    @Test
    @DisplayName("oauth2LoginSuccess - Debe codificar correctamente el firstName")
    void oauth2LoginSuccess_shouldProperlyEncodeFirstName() {
        // Given
        User userWithSpaceName = createUser(1L, "Test User", "LastName", "test@gmail.com", false);
        OAuth2User oAuth2UserWithSpaceName = createMockOAuth2User("test@gmail.com", "Test User", "LastName");

        when(oauth2Service.processOAuth2User(oAuth2UserWithSpaceName)).thenReturn(userWithSpaceName);
        when(oauth2Service.isProfileComplete(userWithSpaceName)).thenReturn(true);
        when(jwtUtil.generateToken(userWithSpaceName.getId(), userWithSpaceName.isAdmin())).thenReturn("jwt-token");

        // When
        RedirectView result = oauth2Controller.oauth2LoginSuccess(oAuth2UserWithSpaceName);

        // Then
        assertNotNull(result);
        String resultUrl = result.getUrl();
        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("firstName=Test+User"),
                "La URL debe contener firstName codificado como 'Test+User', pero fue: " + resultUrl);

        verify(oauth2Service).processOAuth2User(oAuth2UserWithSpaceName);
        verify(oauth2Service).isProfileComplete(userWithSpaceName);
        verify(jwtUtil).generateToken(userWithSpaceName.getId(), userWithSpaceName.isAdmin());
    }

    @Test
    @DisplayName("oauth2LoginSuccess - Debe codificar caracteres especiales en firstName")
    void oauth2LoginSuccess_shouldEncodeSpecialCharactersInFirstName() {
        // Given
        User userWithSpecialName = createUser(1L, "José María", "González", "jose@gmail.com", false);
        OAuth2User oAuth2UserWithSpecialName = createMockOAuth2User("jose@gmail.com", "José María", "González");

        when(oauth2Service.processOAuth2User(oAuth2UserWithSpecialName)).thenReturn(userWithSpecialName);
        when(oauth2Service.isProfileComplete(userWithSpecialName)).thenReturn(true);
        when(jwtUtil.generateToken(userWithSpecialName.getId(), userWithSpecialName.isAdmin())).thenReturn("jwt-token");

        // When
        RedirectView result = oauth2Controller.oauth2LoginSuccess(oAuth2UserWithSpecialName);

        // Then
        assertNotNull(result);
        String resultUrl = result.getUrl();
        assertNotNull(resultUrl);
        assertTrue(resultUrl.contains("firstName=Jos%C3%A9+Mar%C3%ADa") || resultUrl.contains("firstName=José+María"),
                "La URL debe contener firstName con caracteres especiales codificados, pero fue: " + resultUrl);

        verify(oauth2Service).processOAuth2User(oAuth2UserWithSpecialName);
        verify(oauth2Service).isProfileComplete(userWithSpecialName);
        verify(jwtUtil).generateToken(userWithSpecialName.getId(), userWithSpecialName.isAdmin());
    }
}
