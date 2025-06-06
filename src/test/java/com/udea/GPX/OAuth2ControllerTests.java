package com.udea.GPX;

import com.udea.GPX.controller.OAuth2Controller;
import com.udea.GPX.model.User;
import com.udea.GPX.service.OAuth2Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.view.RedirectView;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Disabled;

@SpringBootTest
@Disabled("No se ejecuta en la CI/CD")

public class OAuth2ControllerTests {

    @Mock
    private OAuth2Service oAuth2Service;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private OAuth2Controller oAuth2Controller;

    private User buildSimpleUser(Long id, String firstName, String lastName, String email) {
        User user = new User();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setAuthProvider("GOOGLE");
        user.setGoogleId("google-user-id-123");
        user.setAdmin(false);
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configurar la URL de redirección del frontend
        ReflectionTestUtils.setField(oAuth2Controller, "frontendRedirectUrl", "http://localhost:3000/oauth2/redirect");
    }

    // ========== TESTS PARA OAUTH2 LOGIN SUCCESS ==========

    @Test
    void oauth2LoginSuccess_whenCompleteProfile_shouldRedirectToDashboard() {
        // Arrange
        OAuth2User oAuth2User = createMockOAuth2User("juan@gmail.com", "Juan", "Pérez");

        User user = buildSimpleUser(1L, "Juan", "Pérez", "juan@gmail.com");
        user.setIdentification("12345678");
        user.setPhone("3001234567");
        user.setRole("piloto");
        when(oAuth2Service.processOAuth2User(oAuth2User)).thenReturn(user);
        when(oAuth2Service.isProfileComplete(user)).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.isAdmin())).thenReturn("jwt-token");

        // Act
        RedirectView result = oAuth2Controller.oauth2LoginSuccess(oAuth2User);

        // Assert
        assertNotNull(result);
        String expectedUrl = "http://localhost:3000/oauth2/redirect?token=jwt-token&userId=1&admin=false&provider=google&profileComplete=true&firstName=Juan";
        assertEquals(expectedUrl, result.getUrl());
    }

    @Test
    void oauth2LoginSuccess_whenIncompleteProfile_shouldRedirectToCompleteProfile() {
        // Arrange
        OAuth2User oAuth2User = createMockOAuth2User("juan@gmail.com", "Juan", "Pérez");

        User user = buildSimpleUser(1L, "Juan", "Pérez", "juan@gmail.com");
        // Sin campos esenciales para perfil incompleto
        when(oAuth2Service.processOAuth2User(oAuth2User)).thenReturn(user);
        when(oAuth2Service.isProfileComplete(user)).thenReturn(false);
        when(jwtUtil.generateToken(user.getId(), user.isAdmin())).thenReturn("jwt-token");

        // Act
        RedirectView result = oAuth2Controller.oauth2LoginSuccess(oAuth2User);

        // Assert
        assertNotNull(result);
        String expectedUrl = "http://localhost:3000/oauth2/redirect?token=jwt-token&userId=1&admin=false&provider=google&profileComplete=false&firstName=Juan";
        assertEquals(expectedUrl, result.getUrl());
    }

    @Test
    void oauth2LoginSuccess_whenAdminUser_shouldIncludeAdminFlag() {
        // Arrange
        OAuth2User oAuth2User = createMockOAuth2User("admin@gmail.com", "Admin", "User");

        User user = buildSimpleUser(1L, "Admin", "User", "admin@gmail.com");
        user.setAdmin(true); // Usuario administrador
        user.setIdentification("12345678");
        user.setPhone("3001234567");
        user.setRole("administrador");
        when(oAuth2Service.processOAuth2User(oAuth2User)).thenReturn(user);
        when(oAuth2Service.isProfileComplete(user)).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.isAdmin())).thenReturn("jwt-token");

        // Act
        RedirectView result = oAuth2Controller.oauth2LoginSuccess(oAuth2User);

        // Assert
        assertNotNull(result);
        String expectedUrl = "http://localhost:3000/oauth2/redirect?token=jwt-token&userId=1&admin=true&provider=google&profileComplete=true&firstName=Admin";
        assertEquals(expectedUrl, result.getUrl());
    }

    @Test
    void oauth2LoginSuccess_whenProcessingFails_shouldRedirectWithError() {
        // Arrange
        OAuth2User oAuth2User = createMockOAuth2User("error@gmail.com", "Error", "User");
        when(oAuth2Service.processOAuth2User(oAuth2User)).thenThrow(new RuntimeException("Error al procesar usuario"));

        // Act
        RedirectView result = oAuth2Controller.oauth2LoginSuccess(oAuth2User);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUrl().contains("error=oauth2_failed"));
        assertTrue(result.getUrl().contains("message=Error al procesar usuario"));
    }

    @Test
    void oauth2LoginSuccess_whenJwtGenerationFails_shouldRedirectWithError() {
        // Arrange
        OAuth2User oAuth2User = createMockOAuth2User("jwt-error@gmail.com", "JWT", "Error");

        User user = buildSimpleUser(1L, "JWT", "Error", "jwt-error@gmail.com");
        when(oAuth2Service.processOAuth2User(oAuth2User)).thenReturn(user);
        when(oAuth2Service.isProfileComplete(user)).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.isAdmin()))
                .thenThrow(new RuntimeException("Error al generar JWT"));

        // Act
        RedirectView result = oAuth2Controller.oauth2LoginSuccess(oAuth2User);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUrl().contains("error=oauth2_failed"));
        assertTrue(result.getUrl().contains("message=Error al generar JWT"));
    }

    @Test
    void oauth2LoginSuccess_withNullFirstName_shouldHandleGracefully() {
        // Arrange
        OAuth2User oAuth2User = createMockOAuth2User("test@gmail.com", null, "User");

        User user = buildSimpleUser(1L, null, "User", "test@gmail.com");
        when(oAuth2Service.processOAuth2User(oAuth2User)).thenReturn(user);
        when(oAuth2Service.isProfileComplete(user)).thenReturn(false);
        when(jwtUtil.generateToken(user.getId(), user.isAdmin())).thenReturn("jwt-token");

        // Act
        RedirectView result = oAuth2Controller.oauth2LoginSuccess(oAuth2User);

        // Assert
        assertNotNull(result);
        assertTrue(result.getUrl().contains("firstName="));
    }

    // ========== TESTS PARA GET PROFILE STATUS ==========

    @Test
    void getProfileStatus_whenCompleteProfile_shouldReturnComplete() {
        // Arrange
        OAuth2User oAuth2User = createMockOAuth2User("juan@gmail.com", "Juan", "Pérez");

        User user = buildSimpleUser(1L, "Juan", "Pérez", "juan@gmail.com");
        user.setIdentification("12345678");
        user.setPhone("3001234567");
        user.setRole("piloto");
        user.setPicture("https://avatar.google.com/juan.jpg");

        when(oAuth2Service.processOAuth2User(oAuth2User)).thenReturn(user);
        when(oAuth2Service.isProfileComplete(user)).thenReturn(true);
        when(oAuth2Service.getMissingProfileFields(user)).thenReturn(new String[0]);

        // Act
        ResponseEntity<Map<String, Object>> response = oAuth2Controller.getProfileStatus(oAuth2User);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("isComplete"));
        assertEquals("Juan", responseBody.get("firstName"));
        assertEquals("Pérez", responseBody.get("lastName"));
        assertEquals("juan@gmail.com", responseBody.get("email"));
        assertEquals("https://avatar.google.com/juan.jpg", responseBody.get("picture"));
        assertEquals(1L, responseBody.get("userId"));
        assertTrue(((Object[]) responseBody.get("missingFields")).length == 0);
    }

    @Test
    void getProfileStatus_whenIncompleteProfile_shouldReturnIncompleteWithMissingFields() {
        // Arrange
        OAuth2User oAuth2User = createMockOAuth2User("juan@gmail.com", "Juan", "Pérez");

        User user = buildSimpleUser(1L, "Juan", "Pérez", "juan@gmail.com");
        // Sin campos esenciales

        when(oAuth2Service.processOAuth2User(oAuth2User)).thenReturn(user);
        when(oAuth2Service.isProfileComplete(user)).thenReturn(false);
        when(oAuth2Service.getMissingProfileFields(user))
                .thenReturn(new String[] { "identification", "phone", "role" });

        // Act
        ResponseEntity<Map<String, Object>> response = oAuth2Controller.getProfileStatus(oAuth2User);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("isComplete"));
        assertEquals("Juan", responseBody.get("firstName"));
        assertEquals("Pérez", responseBody.get("lastName"));
        assertEquals("juan@gmail.com", responseBody.get("email"));
        assertEquals(1L, responseBody.get("userId"));

        Object[] missingFields = (Object[]) responseBody.get("missingFields");
        assertEquals(3, missingFields.length);
        assertTrue(java.util.Arrays.asList(missingFields).contains("identification"));
        assertTrue(java.util.Arrays.asList(missingFields).contains("phone"));
        assertTrue(java.util.Arrays.asList(missingFields).contains("role"));
    }

    @Test
    void getProfileStatus_whenOAuth2UserIsNull_shouldReturnBadRequest() {
        // Act
        ResponseEntity<Map<String, Object>> response = oAuth2Controller.getProfileStatus(null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ========== TESTS PARA GET USER INFO ==========

    @Test
    void getCurrentUserInfo_whenValidOAuth2User_shouldReturnUserInfo() {
        // Arrange
        OAuth2User oAuth2User = createMockOAuth2User("test@gmail.com", "Test", "User");

        // Act
        ResponseEntity<Map<String, Object>> response = oAuth2Controller.getCurrentUserInfo(oAuth2User);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Test User", responseBody.get("name"));
        assertEquals("test@gmail.com", responseBody.get("email"));
        assertEquals("https://avatar.google.com/test.jpg", responseBody.get("picture"));
        assertEquals("google-user-id-123", responseBody.get("sub"));
    }

    @Test
    void getCurrentUserInfo_whenOAuth2UserIsNull_shouldReturnBadRequest() {
        // Act
        ResponseEntity<Map<String, Object>> response = oAuth2Controller.getCurrentUserInfo(null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // ========== TESTS PARA URL ENCODING ==========

    @Test
    void oauth2LoginSuccess_shouldProperlyEncodeFirstName() {
        // Arrange
        OAuth2User oAuth2User = createMockOAuth2User("test@gmail.com", "Test User", "With Spaces");

        User user = buildSimpleUser(1L, "Test User", "With Spaces", "test@gmail.com");
        when(oAuth2Service.processOAuth2User(oAuth2User)).thenReturn(user);
        when(oAuth2Service.isProfileComplete(user)).thenReturn(false);
        when(jwtUtil.generateToken(user.getId(), user.isAdmin())).thenReturn("jwt-token");

        // Act
        RedirectView result = oAuth2Controller.oauth2LoginSuccess(oAuth2User);

        // Assert
        assertNotNull(result);
        String redirectUrl = result.getUrl();

        // Verificar que el firstName está correctamente codificado (espacios como + en
        // query params)
        assertTrue(redirectUrl.contains("firstName=Test+User"),
                "La URL debe contener firstName codificado como 'Test+User', pero fue: " + redirectUrl);

        // Verificar que NO contiene espacios sin codificar
        assertFalse(redirectUrl.contains("firstName=Test User"),
                "La URL no debe contener espacios sin codificar en firstName");

        // Verificar otros parámetros esperados
        assertTrue(redirectUrl.contains("token=jwt-token"));
        assertTrue(redirectUrl.contains("userId=1"));
        assertTrue(redirectUrl.contains("admin=false"));
        assertTrue(redirectUrl.contains("provider=google"));
        assertTrue(redirectUrl.contains("profileComplete=false"));
    }

    @Test
    void oauth2LoginSuccess_shouldEncodeSpecialCharactersInFirstName() {
        // Arrange
        OAuth2User oAuth2User = createMockOAuth2User("test@gmail.com", "José María", "González");

        User user = buildSimpleUser(1L, "José María", "González", "test@gmail.com");
        when(oAuth2Service.processOAuth2User(oAuth2User)).thenReturn(user);
        when(oAuth2Service.isProfileComplete(user)).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.isAdmin())).thenReturn("jwt-token");

        // Act
        RedirectView result = oAuth2Controller.oauth2LoginSuccess(oAuth2User);

        // Assert
        assertNotNull(result);
        String redirectUrl = result.getUrl();

        // Verificar que los caracteres especiales están codificados
        // URLEncoder codifica espacios como + y caracteres especiales como %XX
        assertTrue(redirectUrl.contains("firstName=Jos%C3%A9+Mar%C3%ADa"),
                "La URL debe contener firstName con caracteres especiales codificados, pero fue: " + redirectUrl);

        // Verificar que NO contiene caracteres sin codificar
        assertFalse(redirectUrl.contains("firstName=José María"),
                "La URL no debe contener caracteres especiales sin codificar en firstName");
    }
}