package com.udea.gpx.controller;

import com.udea.gpx.dto.AuthResponseDTO;
import com.udea.gpx.exception.InternalServerException;
import com.udea.gpx.model.User;
import com.udea.gpx.service.UserService;
import com.udea.gpx.util.InputSanitizer;
import com.udea.gpx.service.TokenService;
import com.udea.gpx.service.TokenService.TokenPair;
import com.udea.gpx.util.AuthUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Gestión de usuarios, perfiles y autenticación")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // Constants for field names to avoid duplication
    private static final String EMAIL_FIELD = "email";
    private static final String FIRSTNAME_FIELD = "firstname";
    private static final String LASTNAME_FIELD = "lastname";
    private static final String TEAMNAME_FIELD = "teamname";
    private static final String PHONE_FIELD = "phone";
    private static final String EMERGENCYPHONE_FIELD = "emergencyphone";
    private static final String IDENTIFICATION_FIELD = "identification";
    private static final String WIKILOC_FIELD = "wikiloc";
    private static final String TERRAPIRATA_FIELD = "terrapirata";
    private static final String INSTAGRAM_FIELD = "instagram";
    private static final String FACEBOOK_FIELD = "facebook";
    private static final String PICTURE_FIELD = "picture";
    private static final String PASSWORD_FIELD = "password";
    private static final String CURRENTPASSWORD_FIELD = "currentPassword";
    private static final String NEWPASSWORD_FIELD = "newPassword";
    // Error type constants
    private static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    private static final String RUNTIME_ERROR = "RUNTIME_ERROR";
    private static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    // Response field constants
    private static final String MESSAGE_FIELD = "message";
    private static final String USER_FIELD = "user";
    private static final String ADMIN_FIELD = "admin";
    private static final String USER_ID_FIELD = "userId";
    private static final String AUTH_PROVIDER_FIELD = "authProvider";
    private static final String PROFILE_COMPLETE_FIELD = "profileComplete";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String LAST_NAME_FIELD = "lastName";
    private static final String SUCCESS_FIELD = "success";
    private static final String VALID_FIELD = "valid";

    // Auth provider constants
    private static final String LOCAL_PROVIDER = "LOCAL";
    private static final String GOOGLE_PROVIDER = "GOOGLE";

    // Common error messages
    private static final String USER_NOT_FOUND_MSG = "Usuario no encontrado";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Error interno del servidor";
    private static final String INVALID_INPUT_DATA_MSG = "Datos de entrada inválidos: ";
    private static final String INVALID_CREDENTIALS_MSG = "Credenciales incorrectas";

    // Additional response field constants used in methods
    private static final String ACCESS_TOKEN_FIELD = "accessToken";
    private static final String REFRESH_TOKEN_FIELD = "refreshToken";
    private static final String TOKEN_FIELD = "token";
    private static final String PROVIDER_FIELD = "provider";
    private static final String TIMESTAMP_FIELD = "timestamp";
    private static final String EXISTS_FIELD = "exists";
    private static final String LOGIN_URL_FIELD = "loginUrl";
    private static final String REQUIRES_GOOGLE_LOGOUT_FIELD = "requiresGoogleLogout";
    private static final String FORCE_LOGOUT_FIELD = "forceLogout";

    // Error response fields
    private static final String ERROR_FIELD = "error";
    private static final String TYPE_FIELD = "type";

    // HTTP headers
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    private static final String X_REAL_IP_HEADER = "X-Real-IP";
    private static final String UNKNOWN_IP = "unknown";

    // Additional field constants
    private static final String GOOGLE_ID_FIELD = "googleId";
    private static final String SUB_FIELD = "sub";
    private static final String ROLE_FIELD = "role";
    private static final String PICTURE_URL_FIELD = "pictureUrl";
    private static final String INSURANCE_URL_FIELD = "insuranceUrl";

    private final UserService userService;
    private final HttpServletRequest request;
    private final TokenService tokenService;
    private final AuthUtils authUtils;

    // Constructor injection instead of field injection
    public UserController(UserService userService, HttpServletRequest request,
            TokenService tokenService, AuthUtils authUtils) {
        this.userService = userService;
        this.request = request;
        this.tokenService = tokenService;
        this.authUtils = authUtils;
    }

    /**
     * 🛡️ HELPER PARA SANITIZACIÓN MANUAL DE MAPS
     * Sanitiza campos de Map<String, String> según su tipo
     */
    private Map<String, String> sanitizeUserInputMap(Map<String, String> inputData) {
        if (inputData == null)
            return new HashMap<>();

        Map<String, String> sanitized = new HashMap<>();

        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null) {
                sanitized.put(key, null);
                continue;
            }

            try {
                sanitized.put(key, sanitizeFieldValue(key.toLowerCase(), value));
            } catch (IllegalArgumentException e) {
                // Propagar la excepción con contexto adicional
                throw new IllegalArgumentException(
                        "Campo '" + key + "' contiene contenido no válido: " + e.getMessage(), e);
            }
        }

        return sanitized;
    }

    /**
     * Sanitiza el valor de un campo específico según su tipo
     */
    private String sanitizeFieldValue(String fieldKey, String value) {
        String trimmedValue = value.trim();

        switch (fieldKey) {
            case EMAIL_FIELD:
                return trimmedValue.isEmpty() ? trimmedValue : InputSanitizer.sanitizeEmail(value);
            case FIRSTNAME_FIELD, LASTNAME_FIELD, TEAMNAME_FIELD:
                return trimmedValue.isEmpty() ? trimmedValue : InputSanitizer.sanitizeName(value);
            case PHONE_FIELD, EMERGENCYPHONE_FIELD:
                return trimmedValue.isEmpty() ? trimmedValue : InputSanitizer.sanitizePhone(value);
            case IDENTIFICATION_FIELD:
                return trimmedValue.isEmpty() ? trimmedValue : InputSanitizer.sanitizeIdentification(value);
            case WIKILOC_FIELD, TERRAPIRATA_FIELD, INSTAGRAM_FIELD, FACEBOOK_FIELD:
                return trimmedValue.isEmpty() ? trimmedValue : InputSanitizer.sanitizeSocialField(value);
            case PICTURE_FIELD:
                // Para URLs de imagen no usar InputSanitizer.sanitizeUrl porque es muy
                // restrictivo
                // En su lugar usar solo sanitizeText + validación específica de imagen
                return trimmedValue.isEmpty() ? trimmedValue : InputSanitizer.sanitizeText(value);
            case PASSWORD_FIELD, CURRENTPASSWORD_FIELD, NEWPASSWORD_FIELD:
                return InputSanitizer.sanitizeText(value);
            default:
                return InputSanitizer.sanitizeText(value);
        }
    }

    /**
     * Método auxiliar para obtener el usuario autenticado del contexto de seguridad
     * Maneja tanto autenticación JWT como OAuth2
     */
    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        // Manejar diferentes tipos de Principal
        if (auth.getPrincipal() instanceof User user) {
            // Autenticación JWT - Principal es User directamente
            return user;
        } else if (auth.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
            // Autenticación OAuth2 - Principal es OAuth2User, necesitamos buscar el User en
            // BD
            String email = oauth2User.getAttribute(EMAIL_FIELD);
            if (email != null) {
                return userService.findByEmail(email);
            }
        }

        return null;
    }

    /**
     * Método auxiliar para crear respuestas de error estandarizadas
     */
    private Map<String, Object> createErrorResponse(String message, String type) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put(ERROR_FIELD, message);
        errorResponse.put(TYPE_FIELD, type);
        return errorResponse;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<User>> getAllUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los datos de un usuario específico. Solo accesible por el propio usuario o administradores")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID del usuario", required = true, example = "1") @PathVariable Long id) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========== GESTIÓN DE PERFIL SOLO CON URLs ==========

    @PutMapping(value = "/{id}/profile", consumes = { "application/json" })
    @Operation(summary = "Actualizar perfil", description = "Actualiza los datos del perfil del usuario (sin archivos)")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<Object> updateUserProfile(
            @PathVariable Long id,
            @RequestBody User user) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            // Solo actualizar campos de datos, NO tocar archivos (picture, insurance)
            User updatedUser = userService.updateUserProfile(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            // Registrar error de validación para seguimiento de seguridad
            logger.warn("Error de validación en actualización de perfil de usuario {}: {}", id, e.getMessage());
            // Crear una respuesta más informativa para errores de validación
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage(), VALIDATION_ERROR));
        } catch (InternalServerException e) {
            // Manejar errores internos específicos del servidor
            logger.error("Error interno en actualización de perfil de usuario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(INTERNAL_SERVER_ERROR_MSG, INTERNAL_ERROR));
        } catch (RuntimeException e) {
            if (e.getMessage().contains(USER_NOT_FOUND_MSG)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage(), RUNTIME_ERROR));
        } catch (Exception e) {
            // Manejar errores internos (incluyendo InternalServerException) y otros errores
            // generales
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(INTERNAL_SERVER_ERROR_MSG, INTERNAL_ERROR));
        }
    }

    @PutMapping("/{id}/picture")
    @Operation(summary = "Actualizar foto de perfil", description = "Actualiza la URL de la foto de perfil del usuario")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Foto actualizada exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    @ApiResponse(responseCode = "400", description = "URL inválida")
    public ResponseEntity<Object> updateUserPicture(
            @PathVariable Long id,
            @RequestBody Map<String, String> pictureData) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            // 🛡️ SIN SANITIZACIÓN ADICIONAL PARA URLs DE IMAGEN (igual que
            // EventController)
            String newPictureUrl = pictureData.get(PICTURE_URL_FIELD);

            // Validar URL si se proporciona (MISMA LÓGICA QUE EventController)
            if (newPictureUrl != null && !newPictureUrl.trim().isEmpty() && !isValidImageUrl(newPictureUrl)) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("URL de imagen inválida", VALIDATION_ERROR));
            }

            // Usar método específico SIN sanitización (igual que
            // EventService.updateEventPictureUrl)
            User updatedUser = userService.updateUserPictureUrl(id, newPictureUrl);

            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE_FIELD, "Foto de perfil actualizada exitosamente");
            response.put(USER_FIELD, updatedUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al actualizar la foto de perfil: {}", e.getMessage(), e);
            if (e.getMessage() != null && e.getMessage().contains(USER_NOT_FOUND_MSG)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar la foto de perfil: " + e.getMessage(),
                            INTERNAL_ERROR));
        }
    }

    @PutMapping("/{id}/insurance")
    @Operation(summary = "Actualizar seguro", description = "Actualiza la URL del documento de seguro del usuario")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Seguro actualizado exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    @ApiResponse(responseCode = "400", description = "URL inválida")
    public ResponseEntity<Object> updateUserInsurance(
            @PathVariable Long id,
            @RequestBody Map<String, String> insuranceData) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            // 🛡️ SIN SANITIZACIÓN ADICIONAL PARA URLs DE DOCUMENTO (consistente con
            // imagen)
            String newInsuranceUrl = insuranceData.get(INSURANCE_URL_FIELD);

            // Validar URL si se proporciona
            if (newInsuranceUrl != null && !newInsuranceUrl.trim().isEmpty() && !isValidDocumentUrl(newInsuranceUrl)) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("URL de documento inválida", VALIDATION_ERROR));
            }

            // Usar método específico SIN sanitización (consistente con
            // updateUserPictureUrl)
            User updatedUser = userService.updateUserInsuranceUrl(id, newInsuranceUrl);

            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE_FIELD, "Documento de seguro actualizado exitosamente");
            response.put(USER_FIELD, updatedUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al actualizar el documento de seguro: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar el documento de seguro: " + e.getMessage(),
                            INTERNAL_ERROR));
        }
    }

    @DeleteMapping("/{id}/insurance")
    @Operation(summary = "Eliminar seguro", description = "Elimina el documento de seguro del usuario")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponse(responseCode = "200", description = "Seguro eliminado exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    public ResponseEntity<Object> removeInsurance(@PathVariable Long id) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            // Usar método específico para eliminar seguro (más confiable)
            User updatedUser = userService.removeUserInsurance(id);

            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE_FIELD, "Documento de seguro eliminado exitosamente");
            response.put(USER_FIELD, updatedUser);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains(USER_NOT_FOUND_MSG)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage(), RUNTIME_ERROR));
        } catch (Exception e) {
            logger.error("Error al eliminar el documento de seguro para el usuario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al eliminar el documento de seguro: " + e.getMessage(),
                            INTERNAL_ERROR));
        }
    }

    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAdminUsers() {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        List<User> adminUsers = userService.getAllUsers().stream()
                .filter(User::isAdmin)
                .toList();
        return ResponseEntity.ok(adminUsers);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con email y contraseña, retorna tokens JWT", tags = {
            "Autenticación" })
    @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    public ResponseEntity<Object> login(
            @Parameter(description = "Credenciales de login", required = true, schema = @Schema(example = "{\"email\":\"usuario@email.com\",\"password\":\"password123\"}")) @RequestBody Map<String, String> loginData) {
        try {
            // 🛡️ SANITIZACIÓN DE INPUTS DE LOGIN
            Map<String, String> sanitizedData = sanitizeUserInputMap(loginData);
            String email = sanitizedData.get(EMAIL_FIELD);
            String password = sanitizedData.get(PASSWORD_FIELD);

            User user = userService.findByEmail(email);
            if (user == null || !userService.checkPassword(user, password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_CREDENTIALS_MSG);
            }
            // Obtener información de la request para la sesión
            String userAgent = request.getHeader(USER_AGENT_HEADER);
            String ipAddress = getClientIpAddress(request);

            // Generar par de tokens (access + refresh) con información de sesión
            TokenPair tokens = tokenService.generateTokenPairWithSession(user.getId(), user.isAdmin(), userAgent,
                    ipAddress);

            // Verificar si el perfil está completo (usando el mismo servicio OAuth2)
            boolean profileComplete = isLocalProfileComplete(user);

            Map<String, Object> response = new HashMap<>();
            response.put(ACCESS_TOKEN_FIELD, tokens.getAccessToken());
            response.put(REFRESH_TOKEN_FIELD, tokens.getRefreshToken());
            response.put(TOKEN_FIELD, tokens.getAccessToken()); // Mantener compatibilidad
            response.put(ADMIN_FIELD, user.isAdmin());
            response.put(USER_ID_FIELD, user.getId());
            response.put(AUTH_PROVIDER_FIELD, user.getAuthProvider());
            response.put(PROFILE_COMPLETE_FIELD, profileComplete);
            response.put(FIRST_NAME_FIELD, user.getFirstName());
            response.put(PICTURE_FIELD, user.getPicture()); // Agregar foto de perfil

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Error de sanitización - entrada maliciosa detectada
            logger.warn("🚨 Intento de login con datos maliciosos detectado: {}", e.getMessage());
            return ResponseEntity.badRequest().body(INVALID_INPUT_DATA_MSG + e.getMessage());
        } catch (Exception e) {
            logger.error("Error durante el login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR_MSG);
        }
    }

    // Método auxiliar para verificar perfil completo (similar al OAuth2Service)
    private boolean isLocalProfileComplete(User user) {
        return user.getIdentification() != null && !user.getIdentification().trim().isEmpty() &&
                user.getPhone() != null && !user.getPhone().trim().isEmpty() &&
                user.getRole() != null && !user.getRole().trim().isEmpty();
    }

    @GetMapping("/oauth2/login-url")
    public ResponseEntity<Map<String, String>> getGoogleLoginUrl() {
        Map<String, String> response = new HashMap<>();
        response.put(LOGIN_URL_FIELD, "/oauth2/authorization/google");
        response.put(MESSAGE_FIELD, "Redirigir a esta URL para iniciar el flujo OAuth2 con Google");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        try {
            // 🛡️ SANITIZACIÓN DE EMAIL EN QUERY PARAM
            String sanitizedEmail = InputSanitizer.sanitizeEmail(email);
            boolean exists = userService.findByEmail(sanitizedEmail.trim().toLowerCase()) != null;
            Map<String, Boolean> response = new HashMap<>();
            response.put(EXISTS_FIELD, exists);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("🚨 Email malicioso detectado en check-email: {}", e.getMessage());
            Map<String, Boolean> response = new HashMap<>();
            response.put(EXISTS_FIELD, false);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{id}/complete-profile")
    public ResponseEntity<Object> completeProfile(@PathVariable Long id, @RequestBody Map<String, String> profileData) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            // 🛡️ SANITIZACIÓN DE INPUTS DE PERFIL
            Map<String, String> sanitizedData = sanitizeUserInputMap(profileData);

            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();

            // Solo actualizar campos esenciales para completar el perfil
            if (sanitizedData.containsKey(IDENTIFICATION_FIELD)) {
                user.setIdentification(sanitizedData.get(IDENTIFICATION_FIELD));
            }
            if (sanitizedData.containsKey(PHONE_FIELD)) {
                user.setPhone(sanitizedData.get(PHONE_FIELD));
            }
            if (sanitizedData.containsKey(ROLE_FIELD)) {
                user.setRole(sanitizedData.get(ROLE_FIELD));
            }

            // Campos opcionales adicionales
            if (sanitizedData.containsKey(LAST_NAME_FIELD)) {
                user.setLastName(sanitizedData.get(LAST_NAME_FIELD));
            }
            if (sanitizedData.containsKey("birthdate")) {
                // Aquí podrías agregar lógica para parsear la fecha
            }

            User updatedUser = userService.updateUser(id, user);

            Map<String, Object> response = new HashMap<>();
            response.put(USER_FIELD, updatedUser);
            response.put(MESSAGE_FIELD, "Perfil completado exitosamente");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Error de sanitización - entrada maliciosa detectada
            logger.warn("🚨 Intento de completar perfil con datos maliciosos detectado: {}", e.getMessage());
            return ResponseEntity.badRequest().body(INVALID_INPUT_DATA_MSG + e.getMessage());
        } catch (Exception e) {
            logger.error("Error al completar el perfil para el usuario {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al completar el perfil: " + e.getMessage());
        }
    }

    @PostMapping("/simple-register")
    @Operation(summary = "Registro simple", description = "Registra un nuevo usuario con datos básicos", tags = {
            "Autenticación" })
    @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado")
    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<Object> simpleRegister(
            @Parameter(description = "Datos del nuevo usuario", required = true, schema = @Schema(example = "{\"firstName\":\"Juan\",\"lastName\":\"Pérez\",\"email\":\"juan@email.com\",\"password\":\"password123\"}")) @RequestBody Map<String, String> userData) {
        try {
            // 🛡️ SANITIZACIÓN DE INPUTS DE REGISTRO
            Map<String, String> sanitizedData = sanitizeUserInputMap(userData);

            // Validar datos requeridos
            String firstName = sanitizedData.get(FIRST_NAME_FIELD);
            String lastName = sanitizedData.get(LAST_NAME_FIELD);
            String email = sanitizedData.get(EMAIL_FIELD);
            String password = sanitizedData.get(PASSWORD_FIELD);

            if (firstName == null || firstName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre es requerido");
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El email es requerido");
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("La contraseña es requerida");
            }

            // Verificar si el email ya existe
            if (userService.findByEmail(email) != null) {
                return ResponseEntity.badRequest().body("El email ya está registrado");
            }

            // Crear usuario con datos mínimos
            User newUser = new User();
            newUser.setFirstName(firstName.trim());
            newUser.setLastName(lastName != null ? lastName.trim() : "");
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setPassword(password); // Se hasheará en UserService.createUser()
            newUser.setAuthProvider(LOCAL_PROVIDER);
            newUser.setAdmin(false);

            // Los demás campos se dejan null - se completarán después
            User savedUser = userService.createUser(newUser);

            // Obtener información de la request para la sesión
            String userAgent = request.getHeader(USER_AGENT_HEADER);
            String ipAddress = getClientIpAddress(request);

            // Generar par de tokens (access + refresh) con información de sesión
            TokenPair tokens = tokenService.generateTokenPairWithSession(savedUser.getId(), savedUser.isAdmin(),
                    userAgent, ipAddress);

            // Respuesta similar al OAuth2
            Map<String, Object> response = new HashMap<>();
            response.put(ACCESS_TOKEN_FIELD, tokens.getAccessToken());
            response.put(REFRESH_TOKEN_FIELD, tokens.getRefreshToken());
            response.put(TOKEN_FIELD, tokens.getAccessToken()); // Mantener compatibilidad
            response.put(USER_ID_FIELD, savedUser.getId());
            response.put(ADMIN_FIELD, savedUser.isAdmin());
            response.put(AUTH_PROVIDER_FIELD, savedUser.getAuthProvider());
            response.put(PROFILE_COMPLETE_FIELD, false); // Siempre false para registro simple
            response.put(FIRST_NAME_FIELD, savedUser.getFirstName());
            response.put(PICTURE_FIELD, savedUser.getPicture()); // Agregar foto de perfil
            response.put(MESSAGE_FIELD, "Usuario registrado exitosamente. Por favor completa tu perfil.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            // Error de sanitización - entrada maliciosa detectada
            logger.warn("🚨 Intento de registro con datos maliciosos detectado: {}", e.getMessage());
            return ResponseEntity.badRequest().body(INVALID_INPUT_DATA_MSG + e.getMessage());
        } catch (Exception e) {
            logger.error("Error al registrar usuario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar usuario: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> unifiedLogout(
            HttpServletRequest request,
            HttpServletResponse response,
            @AuthenticationPrincipal Object principal) {

        try {
            String authProvider = LOCAL_PROVIDER;
            String userInfo = "Unknown";
            Long userId = null;

            // Detectar tipo de autenticación
            if (principal instanceof User user) {
                // Autenticación JWT
                authProvider = user.getAuthProvider() != null ? user.getAuthProvider() : LOCAL_PROVIDER;
                userInfo = user.getEmail();
                userId = user.getId();
            } else if (principal instanceof OAuth2User oauth2User) {
                // Autenticación OAuth2
                authProvider = GOOGLE_PROVIDER;
                userInfo = oauth2User.getAttribute(EMAIL_FIELD);
                // Para OAuth2, intentar obtener el userId del contexto si está disponible
                Object userIdAttr = oauth2User.getAttribute(SUB_FIELD);
                if (userIdAttr != null) {
                    userInfo += " (Google ID: " + userIdAttr + ")";
                }
            }

            // Realizar logout completo
            performLogout(request, response, authProvider);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put(MESSAGE_FIELD, "Sesión cerrada exitosamente");
            responseBody.put(PROVIDER_FIELD, authProvider);
            responseBody.put("user", userInfo);
            responseBody.put(USER_ID_FIELD, userId);
            responseBody.put(REQUIRES_GOOGLE_LOGOUT_FIELD, GOOGLE_PROVIDER.equals(authProvider));
            responseBody.put(TIMESTAMP_FIELD, System.currentTimeMillis());
            responseBody.put(SUCCESS_FIELD, true);

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            // Limpiar contexto aunque haya error
            SecurityContextHolder.clearContext();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ERROR_FIELD, "Error al cerrar sesión");
            errorResponse.put(MESSAGE_FIELD, e.getMessage());
            errorResponse.put(FORCE_LOGOUT_FIELD, true);
            errorResponse.put(SUCCESS_FIELD, false);
            errorResponse.put(TIMESTAMP_FIELD, System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Método privado para realizar el logout completo
     */
    private void performLogout(HttpServletRequest request, HttpServletResponse response, String authProvider) {
        try {
            // 1. Invalidar la sesión HTTP si existe
            if (request.getSession(false) != null) {
                request.getSession().invalidate();
            }

            // 2. Limpiar contexto de seguridad
            SecurityContextHolder.clearContext();

            // 3. Usar el handler de logout de Spring Security
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

            // 4. Log del evento de logout
            logLogoutEvent();

        } catch (Exception e) {
            logger.error("Error durante logout con provider {}: {}", authProvider, e.getMessage(), e);
            // Asegurar que al menos el contexto se limpie
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Método para registrar eventos de logout
     */
    private void logLogoutEvent() {
        // Logging del evento de logout puede ser implementado aquí
        // usando un sistema de logging profesional como SLF4J
    }

    /**
     * Obtiene la dirección IP real del cliente, considerando proxies y load
     * balancers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !UNKNOWN_IP.equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader(X_REAL_IP_HEADER);
        if (xRealIp != null && !xRealIp.isEmpty() && !UNKNOWN_IP.equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Endpoint para cambiar contraseña
     */
    @PutMapping("/{id}/change-password")
    public ResponseEntity<Object> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordData) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            // 🛡️ SANITIZACIÓN DE PASSWORDS
            Map<String, String> sanitizedData = sanitizeUserInputMap(passwordData);
            String currentPassword = sanitizedData.get(CURRENTPASSWORD_FIELD);
            String newPassword = sanitizedData.get(NEWPASSWORD_FIELD);

            // Validar que se proporcionen ambas contraseñas
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("La contraseña actual es requerida", VALIDATION_ERROR));
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("La nueva contraseña es requerida", VALIDATION_ERROR));
            }

            // Verificar que el usuario tenga contraseña (no sea OAuth2)
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            if (user.getPassword() == null) {
                return ResponseEntity.badRequest().body(
                        createErrorResponse("No puedes cambiar la contraseña de una cuenta OAuth2", VALIDATION_ERROR));
            }

            // Cambiar contraseña
            userService.changePassword(id, currentPassword.trim(), newPassword.trim());

            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE_FIELD, "Contraseña cambiada exitosamente");
            response.put(SUCCESS_FIELD, true);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage(), VALIDATION_ERROR));
        } catch (RuntimeException e) {
            if (e.getMessage().contains(USER_NOT_FOUND_MSG)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage(), RUNTIME_ERROR));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(INTERNAL_SERVER_ERROR_MSG, INTERNAL_ERROR));
        }
    }

    /**
     * Endpoint para cambiar el rol de administrador de un usuario
     */
    @PatchMapping("/{id}/admin")
    public ResponseEntity<Object> toggleAdminRole(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> adminData) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // No permitir que un admin se quite sus propios permisos
        User authUser = getAuthenticatedUser();
        if (authUser != null && authUser.getId().equals(id)) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("No puedes cambiar tu propio rol de administrador", VALIDATION_ERROR));
        }
        try {
            Boolean isAdmin = adminData.get(ADMIN_FIELD);
            if (isAdmin == null) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("El campo 'admin' es requerido", VALIDATION_ERROR));
            }

            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            user.setAdmin(isAdmin);
            User updatedUser = userService.updateUser(id, user);

            Map<String, Object> response = new HashMap<>();
            response.put(MESSAGE_FIELD,
                    isAdmin ? "Usuario promovido a administrador" : "Permisos de administrador removidos");
            response.put(USER_FIELD, updatedUser);
            response.put(SUCCESS_FIELD, true);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().contains(USER_NOT_FOUND_MSG)) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage(), RUNTIME_ERROR));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse(INTERNAL_SERVER_ERROR_MSG, INTERNAL_ERROR));
        }
    }

    /**
     * Endpoint adicional para validar si el token JWT sigue siendo válido
     */
    @GetMapping("/validate-token")
    public ResponseEntity<Object> validateToken() {
        try {
            User authUser = getAuthenticatedUser();

            // Verificar que tenemos un usuario válido
            if (authUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put(VALID_FIELD, false);
                errorResponse.put(MESSAGE_FIELD, "Usuario no encontrado o token inválido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            // Construir respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put(VALID_FIELD, true);
            response.put(USER_ID_FIELD, authUser.getId());
            response.put(EMAIL_FIELD, authUser.getEmail());
            response.put(FIRST_NAME_FIELD, authUser.getFirstName());
            response.put(LAST_NAME_FIELD, authUser.getLastName());
            response.put(ADMIN_FIELD, authUser.isAdmin());
            response.put(AUTH_PROVIDER_FIELD, authUser.getAuthProvider());
            response.put(PROFILE_COMPLETE_FIELD, isLocalProfileComplete(authUser));

            // Campos adicionales útiles para el frontend
            response.put(PICTURE_FIELD, authUser.getPicture());
            response.put(GOOGLE_ID_FIELD, authUser.getGoogleId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(VALID_FIELD, false);
            errorResponse.put(MESSAGE_FIELD, INTERNAL_SERVER_ERROR_MSG);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Valida que la URL sea una imagen válida de fuentes permitidas
     */
    private boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return true; // Permitir vacío para eliminar imagen
        }

        // Debe ser HTTPS
        if (!url.startsWith("https://")) {
            return false;
        }

        // Debe contener una extensión de imagen válida o ser de servicios conocidos
        String lowerUrl = url.toLowerCase();
        return lowerUrl.matches(".*\\.(jpg|jpeg|png|webp|gif)(\\?.*)?$") ||
                lowerUrl.contains("imgur.com") ||
                lowerUrl.contains("cloudinary.com") ||
                lowerUrl.contains("drive.google.com") ||
                lowerUrl.contains("dropbox.com") ||
                lowerUrl.contains("unsplash.com") ||
                lowerUrl.contains("plus.unsplash.com") ||
                lowerUrl.contains("pexels.com") ||
                lowerUrl.contains("googleusercontent.com") ||
                lowerUrl.contains("lh3.googleusercontent.com") ||
                lowerUrl.contains("lh4.googleusercontent.com") ||
                lowerUrl.contains("lh5.googleusercontent.com") ||
                lowerUrl.contains("lh6.googleusercontent.com");
    }

    /**
     * Valida que la URL sea un documento válido de fuentes permitidas
     */
    private boolean isValidDocumentUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return true; // Permitir vacío para eliminar documento
        }

        // Debe ser HTTPS
        if (!url.startsWith("https://")) {
            return false;
        }

        // Debe contener una extensión de documento válida o ser de servicios conocidos
        String lowerUrl = url.toLowerCase();
        return lowerUrl.matches(".*\\.(pdf|doc|docx|jpg|jpeg|png)(\\?.*)?$") ||
                lowerUrl.contains("drive.google.com") ||
                lowerUrl.contains("dropbox.com") ||
                lowerUrl.contains("onedrive.com") ||
                lowerUrl.contains("docs.google.com");
    }
}
