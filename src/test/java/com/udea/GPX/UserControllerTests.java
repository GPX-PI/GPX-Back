package com.udea.GPX;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.udea.GPX.controller.UserController;
import com.udea.GPX.model.User;
import com.udea.GPX.service.UserService;
import com.udea.GPX.service.TokenService;
import com.udea.GPX.service.TokenService.TokenPair;
import com.udea.GPX.util.AuthUtils;
import com.udea.GPX.util.TestDataBuilder;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserControllerTests {

    @Mock
    private UserService userService;

    @Mock
    private com.udea.GPX.JwtUtil jwtUtil;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private UserController userController;

    // 🏗️ USANDO TestDataBuilder ESTANDARIZADO
    // Ya no necesitamos métodos buildUser locales

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        ReflectionTestUtils.setField(userController, "request", request);
        ReflectionTestUtils.setField(userController, "authUtils", authUtils);
    }

    @Test
    void getAllUsers_whenAdmin_shouldReturnOK() {
        // Arrange
        User adminUser = TestDataBuilder.buildUser(1L, "Admin", true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        List<User> users = Arrays.asList(
                TestDataBuilder.buildUser(1L, "Juan", false),
                TestDataBuilder.buildUser(2L, "María", true));
        when(userService.getAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    void getAllUsers_whenNotAdmin_shouldReturnForbidden() {
        // Arrange
        User nonAdminUser = TestDataBuilder.buildUser(1L, "Usuario", false);
        when(authentication.getPrincipal()).thenReturn(nonAdminUser);
        when(authUtils.isCurrentUserAdmin()).thenReturn(false);

        // Act
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getUserById_whenAdmin_shouldReturnOK() {
        // Arrange
        Long userId = 1L;
        User user = TestDataBuilder.buildUser(userId, "Juan", false);
        User adminUser = TestDataBuilder.buildUser(2L, "Admin", true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserOrAdmin(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<User> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Juan", Objects.requireNonNull(response.getBody()).getFirstName());
    }

    @Test
    void getUserById_whenSameUser_shouldReturnOK() {
        // Arrange
        Long userId = 1L;
        User user = TestDataBuilder.buildUser(userId, "Juan", false);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authUtils.isCurrentUserOrAdmin(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<User> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Juan", Objects.requireNonNull(response.getBody()).getFirstName());
    }

    @Test
    void getUserById_whenDifferentUser_shouldReturnForbidden() {
        // Arrange
        Long userId = 1L;
        User user = TestDataBuilder.buildUser(userId, "Juan", false);
        User otherUser = TestDataBuilder.buildUser(2L, "Otro", false);
        when(authentication.getPrincipal()).thenReturn(otherUser);

        // Act
        ResponseEntity<User> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void login_whenValidCredentials_shouldReturnTokenWithProfileInfo() {
        // Arrange
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "correo@ejemplo.com");
        loginData.put("password", "password123");
        User user = TestDataBuilder.buildUser(1L, "Juan", false);
        user.setAuthProvider("LOCAL");
        when(userService.findByEmail("correo@ejemplo.com")).thenReturn(user);
        when(userService.checkPassword(user, "password123")).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.isAdmin())).thenReturn("jwt-token");
        TokenPair mockTokenPair = new TokenPair("jwt-token", "refresh-token-12345");
        when(tokenService.generateTokenPairWithSession(eq(user.getId()), eq(user.isAdmin()), any(String.class),
                any(String.class))).thenReturn(mockTokenPair);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Test-Agent");

        // Act
        ResponseEntity<?> response = userController.login(loginData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("jwt-token", responseBody.get("token"));
        assertEquals(false, responseBody.get("admin"));
        assertEquals(1L, responseBody.get("userId"));
        assertEquals("LOCAL", responseBody.get("authProvider"));
        assertEquals("Juan", responseBody.get("firstName"));
        assertTrue((Boolean) responseBody.get("profileComplete")); // Usuario completo en test TestDataBuilder
    }

    @Test
    void login_whenValidCredentialsIncompleteProfile_shouldReturnTokenWithProfileIncomplete() {
        // Arrange
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "correo@ejemplo.com");
        loginData.put("password", "password123");
        User user = TestDataBuilder.buildUser(1L, "Juan", false);
        user.setLastName("Pérez");
        user.setEmail("correo@ejemplo.com");
        user.setPassword("password123");
        // Campos esenciales vacíos para perfil incompleto
        user.setIdentification(null);
        user.setPhone(null);
        user.setRole(null);

        when(userService.findByEmail("correo@ejemplo.com")).thenReturn(user);
        when(userService.checkPassword(user, "password123")).thenReturn(true);
        when(jwtUtil.generateToken(user.getId(), user.isAdmin())).thenReturn("jwt-token");
        TokenPair mockTokenPair = new TokenPair("jwt-token", "refresh-token-12345");
        when(tokenService.generateTokenPairWithSession(eq(user.getId()), eq(user.isAdmin()), any(String.class),
                any(String.class))).thenReturn(mockTokenPair);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Test-Agent");

        // Act
        ResponseEntity<?> response = userController.login(loginData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("jwt-token", responseBody.get("token"));
        assertEquals(false, responseBody.get("admin"));
        assertEquals(1L, responseBody.get("userId"));
        assertEquals("LOCAL", responseBody.get("authProvider"));
        assertEquals("Juan", responseBody.get("firstName"));
        assertFalse((Boolean) responseBody.get("profileComplete")); // Perfil incompleto
    }

    @Test
    void login_whenInvalidCredentials_shouldReturnUnauthorized() {
        // Arrange
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "correo@ejemplo.com");
        loginData.put("password", "wrongpassword");
        User user = TestDataBuilder.buildUser(1L, "Juan", false);
        when(userService.findByEmail("correo@ejemplo.com")).thenReturn(user);
        when(userService.checkPassword(user, "wrongpassword")).thenReturn(false);

        // Act
        ResponseEntity<?> response = userController.login(loginData);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void login_whenUserNotFound_shouldReturnUnauthorized() {
        // Arrange
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "noexiste@ejemplo.com");
        loginData.put("password", "password123");
        when(userService.findByEmail("noexiste@ejemplo.com")).thenReturn(null);

        // Act
        ResponseEntity<?> response = userController.login(loginData);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // ========== TESTS PARA SIMPLE REGISTER ==========

    @Test
    void simpleRegister_whenValidData_shouldReturnCreated() {
        // Arrange
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", "Juan");
        userData.put("lastName", "Pérez");
        userData.put("email", "juan@ejemplo.com");
        userData.put("password", "password123");

        User newUser = TestDataBuilder.buildUser(1L, "Juan", false);
        newUser.setLastName("Pérez");
        newUser.setEmail("juan@ejemplo.com");
        newUser.setPassword("password123");

        when(userService.findByEmail("juan@ejemplo.com")).thenReturn(null); // Email no existe
        when(userService.createUser(any(User.class))).thenReturn(newUser);
        when(jwtUtil.generateToken(newUser.getId(), newUser.isAdmin())).thenReturn("jwt-token");
        TokenPair mockTokenPair = new TokenPair("jwt-token", "refresh-token-12345");
        when(tokenService.generateTokenPairWithSession(eq(newUser.getId()), eq(newUser.isAdmin()), any(String.class),
                any(String.class))).thenReturn(mockTokenPair);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Test-Agent");

        // Act
        ResponseEntity<?> response = userController.simpleRegister(userData);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("jwt-token", responseBody.get("token"));
        assertEquals(1L, responseBody.get("userId"));
        assertEquals(false, responseBody.get("admin"));
        assertEquals("LOCAL", responseBody.get("authProvider"));
        assertFalse((Boolean) responseBody.get("profileComplete")); // Siempre false para registro simple
        assertTrue(responseBody.get("message").toString().contains("registrado exitosamente"));
    }

    @Test
    void simpleRegister_whenEmailExists_shouldReturnBadRequest() {
        // Arrange
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", "Juan");
        userData.put("lastName", "Pérez");
        userData.put("email", "existe@ejemplo.com");
        userData.put("password", "password123");

        User existingUser = TestDataBuilder.buildUser(1L, "Otro", false);
        existingUser.setLastName("Usuario");
        existingUser.setEmail("existe@ejemplo.com");
        when(userService.findByEmail("existe@ejemplo.com")).thenReturn(existingUser);

        // Act
        ResponseEntity<?> response = userController.simpleRegister(userData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El email ya está registrado", response.getBody());
    }

    @Test
    void simpleRegister_whenMissingRequiredFields_shouldReturnBadRequest() {
        // Arrange
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", ""); // Campo vacío
        userData.put("email", "juan@ejemplo.com");
        userData.put("password", "password123");

        // Act
        ResponseEntity<?> response = userController.simpleRegister(userData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El nombre es requerido", response.getBody());
    }

    @Test
    void simpleRegister_whenMissingEmail_shouldReturnBadRequest() {
        // Arrange
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", "Juan");
        userData.put("password", "password123");
        // email faltante

        // Act
        ResponseEntity<?> response = userController.simpleRegister(userData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El email es requerido", response.getBody());
    }

    @Test
    void simpleRegister_whenMissingPassword_shouldReturnBadRequest() {
        // Arrange
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", "Juan");
        userData.put("email", "juan@ejemplo.com");
        // password faltante

        // Act
        ResponseEntity<?> response = userController.simpleRegister(userData);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("La contraseña es requerida", response.getBody());
    }

    // ========== TESTS PARA CHECK EMAIL ==========

    @Test
    void checkEmailExists_whenEmailExists_shouldReturnTrue() {
        // Arrange
        String email = "existe@ejemplo.com";
        User existingUser = TestDataBuilder.buildUser(1L, "Juan", false);
        existingUser.setLastName("Pérez");
        existingUser.setEmail(email);
        when(userService.findByEmail(email.toLowerCase())).thenReturn(existingUser);

        // Act
        ResponseEntity<Map<String, Boolean>> response = userController.checkEmailExists(email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Boolean> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.get("exists"));
    }

    @Test
    void checkEmailExists_whenEmailDoesNotExist_shouldReturnFalse() {
        // Arrange
        String email = "noexiste@ejemplo.com";
        when(userService.findByEmail(email.toLowerCase())).thenReturn(null);

        // Act
        ResponseEntity<Map<String, Boolean>> response = userController.checkEmailExists(email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Boolean> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.get("exists"));
    }

    @Test
    void checkEmailExists_withSpacesAndUppercase_shouldNormalizeEmail() {
        // Arrange
        String email = "  EXISTE@EJEMPLO.COM  ";
        User existingUser = TestDataBuilder.buildUser(1L, "Juan", false);
        existingUser.setLastName("Pérez");
        existingUser.setEmail("existe@ejemplo.com");
        when(userService.findByEmail("existe@ejemplo.com")).thenReturn(existingUser);

        // Act
        ResponseEntity<Map<String, Boolean>> response = userController.checkEmailExists(email);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Boolean> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.get("exists"));

        // Verificar que se llamó con email normalizado
        verify(userService).findByEmail("existe@ejemplo.com");
    }

    // ========== TESTS PARA COMPLETE PROFILE ==========

    @Test
    void completeProfile_whenSameUser_shouldCompleteSuccessfully() {
        // Arrange
        Long userId = 1L;
        User user = TestDataBuilder.buildUser(userId, "Juan", false);
        user.setLastName("Pérez");
        user.setEmail("juan@ejemplo.com");

        Map<String, String> profileData = new HashMap<>();
        profileData.put("identification", "12345678");
        profileData.put("phone", "3001234567");
        profileData.put("role", "piloto");

        when(authentication.getPrincipal()).thenReturn(user);
        when(authUtils.isCurrentUserOrAdmin(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        User updatedUser = TestDataBuilder.buildUser(userId, "Juan", false);
        updatedUser.setLastName("Pérez");
        updatedUser.setEmail("juan@ejemplo.com");
        updatedUser.setIdentification("12345678");
        updatedUser.setPhone("3001234567");
        updatedUser.setRole("piloto");
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<?> response = userController.completeProfile(userId, profileData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.get("message").toString().contains("completado exitosamente"));
        assertNotNull(responseBody.get("user"));
    }

    @Test
    void completeProfile_whenAdmin_shouldCompleteSuccessfully() {
        // Arrange
        Long userId = 1L;
        User user = TestDataBuilder.buildUser(userId, "Juan", false);
        user.setLastName("Pérez");
        user.setEmail("juan@ejemplo.com");
        User adminUser = TestDataBuilder.buildUser(2L, "Admin", true);

        Map<String, String> profileData = new HashMap<>();
        profileData.put("identification", "12345678");
        profileData.put("phone", "3001234567");
        profileData.put("role", "piloto");

        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserOrAdmin(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        User updatedUser = TestDataBuilder.buildUser(userId, "Juan", false);
        updatedUser.setLastName("Pérez");
        updatedUser.setEmail("juan@ejemplo.com");
        updatedUser.setIdentification("12345678");
        updatedUser.setPhone("3001234567");
        updatedUser.setRole("piloto");
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<?> response = userController.completeProfile(userId, profileData);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.get("message").toString().contains("completado exitosamente"));
    }

    @Test
    void completeProfile_whenDifferentUser_shouldReturnForbidden() {
        // Arrange
        Long userId = 1L;
        User user = TestDataBuilder.buildUser(userId, "Juan", false);
        user.setLastName("Pérez");
        user.setEmail("juan@ejemplo.com");
        User otherUser = TestDataBuilder.buildUser(2L, "Otro", false);
        otherUser.setLastName("Usuario");
        otherUser.setEmail("otro@ejemplo.com");

        Map<String, String> profileData = new HashMap<>();
        profileData.put("identification", "12345678");

        when(authentication.getPrincipal()).thenReturn(otherUser);

        // Act
        ResponseEntity<?> response = userController.completeProfile(userId, profileData);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void completeProfile_whenUserNotFound_shouldReturnNotFound() {
        // Arrange
        Long userId = 1L; // Usar el mismo ID del usuario autenticado
        User user = TestDataBuilder.buildUser(userId, "Juan", false);
        user.setLastName("Pérez");
        user.setEmail("juan@ejemplo.com");

        Map<String, String> profileData = new HashMap<>();
        profileData.put("identification", "12345678");

        when(authentication.getPrincipal()).thenReturn(user);
        when(authUtils.isCurrentUserOrAdmin(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(Optional.empty()); // Usuario no existe

        // Act
        ResponseEntity<?> response = userController.completeProfile(userId, profileData);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateUser_whenAdmin_shouldUpdateSuccessfully() throws Exception {
        // Arrange
        Long userId = 1L;
        User existingUser = TestDataBuilder.buildUser(userId, "Juan", false);
        User updatedUser = TestDataBuilder.buildUser(userId, "JuanUpdated", false);
        User adminUser = TestDataBuilder.buildUser(2L, "Admin", true);
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(authUtils.isCurrentUserOrAdmin(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(Optional.of(existingUser));
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<?> response = userController.updateUserWithFiles(userId, updatedUser, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        User responseUser = (User) responseBody.get("user");
        assertEquals("JuanUpdated", Objects.requireNonNull(responseUser).getFirstName());
    }

    @Test
    void updateUser_whenSameUser_shouldUpdateSuccessfully() throws Exception {
        // Arrange
        Long userId = 1L;
        User existingUser = TestDataBuilder.buildUser(userId, "Juan", false);
        User updatedUser = TestDataBuilder.buildUser(userId, "JuanUpdated", false);
        when(authentication.getPrincipal()).thenReturn(existingUser);
        when(authUtils.isCurrentUserOrAdmin(userId)).thenReturn(true);
        when(userService.getUserById(userId)).thenReturn(Optional.of(existingUser));
        when(userService.updateUser(eq(userId), any(User.class))).thenReturn(updatedUser);

        // Act
        ResponseEntity<?> response = userController.updateUserWithFiles(userId, updatedUser, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        User responseUser = (User) responseBody.get("user");
        assertEquals("JuanUpdated", Objects.requireNonNull(responseUser).getFirstName());
    }

    @Test
    void updateUser_whenDifferentUser_shouldReturnForbidden() throws Exception {
        // Arrange
        Long userId = 1L;
        User existingUser = TestDataBuilder.buildUser(userId, "Juan", false);
        User updatedUser = TestDataBuilder.buildUser(userId, "JuanUpdated", false);
        User otherUser = TestDataBuilder.buildUser(2L, "Otro", false);
        when(authentication.getPrincipal()).thenReturn(otherUser);

        // Act
        ResponseEntity<?> response = userController.updateUserWithFiles(userId, updatedUser, null, null);
        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // ========== TESTS PARA GET GOOGLE LOGIN URL ==========

    @Test
    void getGoogleLoginUrl_shouldReturnLoginUrl() {
        // Act
        ResponseEntity<Map<String, String>> response = userController.getGoogleLoginUrl();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, String> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("/oauth2/authorization/google", responseBody.get("loginUrl"));
        assertTrue(responseBody.get("message").contains("OAuth2"));
    }
}
