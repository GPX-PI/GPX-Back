package com.udea.GPX.controller;

import com.udea.GPX.dto.AuthResponseDTO;
import com.udea.GPX.exception.FileOperationException;
import com.udea.GPX.model.User;
import com.udea.GPX.service.FileTransactionService;
import com.udea.GPX.service.UserService;
import com.udea.GPX.util.InputSanitizer;
import com.udea.GPX.JwtUtil;
import com.udea.GPX.service.TokenService;
import com.udea.GPX.service.TokenService.TokenPair;
import com.udea.GPX.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.Arrays;
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Gestión de usuarios, perfiles y autenticación")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private FileTransactionService fileTransactionService;

    /**
     * 🛡️ HELPER PARA SANITIZACIÓN MANUAL DE MAPS
     * Sanitiza campos de Map<String, String> según su tipo
     */
    private Map<String, String> sanitizeUserInputMap(Map<String, String> inputData) {
        if (inputData == null)
            return null;

        Map<String, String> sanitized = new HashMap<>();

        for (Map.Entry<String, String> entry : inputData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null) {
                sanitized.put(key, null);
                continue;
            }

            try {
                // Sanitizar según el tipo de campo
                switch (key.toLowerCase()) {
                    case "email":
                        // Solo sanitizar emails no vacíos
                        if (!value.trim().isEmpty()) {
                            sanitized.put(key, InputSanitizer.sanitizeEmail(value));
                        } else {
                            sanitized.put(key, value.trim());
                        }
                        break;
                    case "firstname":
                    case "lastname":
                    case "teamname":
                        // Solo sanitizar nombres no vacíos
                        if (!value.trim().isEmpty()) {
                            sanitized.put(key, InputSanitizer.sanitizeName(value));
                        } else {
                            sanitized.put(key, value.trim());
                        }
                        break;
                    case "phone":
                    case "emergencyphone":
                        // Solo sanitizar teléfonos no vacíos
                        if (!value.trim().isEmpty()) {
                            sanitized.put(key, InputSanitizer.sanitizePhone(value));
                        } else {
                            sanitized.put(key, value.trim());
                        }
                        break;
                    case "identification":
                        // Solo sanitizar identificaciones no vacías
                        if (!value.trim().isEmpty()) {
                            sanitized.put(key, InputSanitizer.sanitizeIdentification(value));
                        } else {
                            sanitized.put(key, value.trim());
                        }
                        break;
                    case "wikiloc":
                    case "terrapirata":
                    case "instagram":
                    case "facebook":
                    case "picture":
                        // Solo sanitizar URLs no vacías
                        if (!value.trim().isEmpty()) {
                            sanitized.put(key, InputSanitizer.sanitizeUrl(value));
                        } else {
                            sanitized.put(key, value.trim());
                        }
                        break;
                    case "password":
                    case "currentpassword":
                    case "newpassword":
                        // Para passwords siempre aplicamos sanitización básica de texto
                        sanitized.put(key, InputSanitizer.sanitizeText(value));
                        break;
                    default:
                        // Para otros campos, sanitización general de texto
                        sanitized.put(key, InputSanitizer.sanitizeText(value));
                        break;
                }
            } catch (IllegalArgumentException e) {
                // Log del intento de ataque y rechazar request
                logger.warn("🚨 INTENTO DE ATAQUE DETECTADO - Campo: {}, Valor sospechoso detectado: {}",
                        key, e.getMessage());
                throw new IllegalArgumentException(
                        "Campo '" + key + "' contiene contenido no válido: " + e.getMessage());
            }
        }

        return sanitized;
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
        if (auth.getPrincipal() instanceof User) {
            // Autenticación JWT - Principal es User directamente
            return (User) auth.getPrincipal();
        } else if (auth.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
            // Autenticación OAuth2 - Principal es OAuth2User, necesitamos buscar el User en
            // BD
            org.springframework.security.oauth2.core.user.OAuth2User oauth2User = (org.springframework.security.oauth2.core.user.OAuth2User) auth
                    .getPrincipal();

            String email = oauth2User.getAttribute("email");
            if (email != null) {
                return userService.findByEmail(email);
            }
        }

        return null;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID del usuario", required = true, example = "1") @PathVariable Long id) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private void deleteOldFile(String oldFilePath) {
        if (oldFilePath != null && !oldFilePath.trim().isEmpty() && !oldFilePath.startsWith("http")) {
            try {
                File oldFile = new File(oldFilePath);
                if (oldFile.exists()) {
                    boolean deleted = oldFile.delete();
                    if (deleted) {
                        logger.debug("Archivo anterior eliminado: {}", oldFilePath);
                    } else {
                        logger.warn("No se pudo eliminar el archivo: {}", oldFilePath);
                    }
                }
            } catch (Exception e) {
                logger.error("Error al eliminar archivo anterior: {}", oldFilePath, e);
            }
        }
    }

    private String saveFile(MultipartFile file, String fileType) throws Exception {
        // Validar tipos de archivo permitidos
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        // Lista de tipos MIME permitidos para imágenes
        List<String> allowedImageTypes = Arrays.asList(
                "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");

        // Lista de tipos permitidos para documentos (insurance)
        List<String> allowedDocumentTypes = Arrays.asList(
                "application/pdf", "image/jpeg", "image/jpg", "image/png", "image/gif");

        // Validar que el archivo no esté vacío
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        // Validar contenido según el tipo de archivo
        if ("picture".equals(fileType)) {
            if (contentType == null || !allowedImageTypes.contains(contentType.toLowerCase())) {
                throw new IllegalArgumentException(
                        "Tipo de archivo no válido para foto de perfil. Tipos permitidos: JPG, PNG, GIF, WebP");
            }
        } else if ("insurance".equals(fileType)) {
            if (contentType == null || !allowedDocumentTypes.contains(contentType.toLowerCase())) {
                throw new IllegalArgumentException(
                        "Tipo de archivo no válido para seguro. Tipos permitidos: PDF, JPG, PNG, GIF");
            }
        }

        // Validar tamaño máximo (adicional a la configuración de Spring)
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new IllegalArgumentException("El archivo es demasiado grande (máximo 10MB)");
        }

        // Validar extensión del archivo
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("El archivo debe tener una extensión válida");
        }

        // Validar extensión coincida con content-type
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if ("picture".equals(fileType)) {
            List<String> validImageExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp");
            if (!validImageExtensions.contains(extension)) {
                throw new IllegalArgumentException("Extensión no válida para imagen: " + extension);
            }
        } else if ("insurance".equals(fileType)) {
            List<String> validDocExtensions = Arrays.asList(".pdf", ".jpg", ".jpeg", ".png", ".gif");
            if (!validDocExtensions.contains(extension)) {
                throw new IllegalArgumentException("Extensión no válida para documento: " + extension);
            }
        }

        String uploadsDir = "uploads/";
        String filePath = uploadsDir + System.currentTimeMillis() + "_" + originalFilename;
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return filePath;
    }

    // Endpoint para actualizar datos del usuario con archivos
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<?> updateUserWithFiles(
            @PathVariable Long id,
            @RequestPart("user") User user,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            @RequestPart(value = "insurance", required = false) MultipartFile insurance) {

        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Obtener el usuario actual para acceder a las rutas de archivos anteriores
        Optional<User> currentUserOpt = userService.getUserById(id);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = currentUserOpt.get();

        // Procesar archivos usando el servicio transaccional
        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            String profilePhotoPath = fileTransactionService.updateFileTransactional(
                    profilePhoto, currentUser.getPicture(), "picture", "uploads/profiles/");
            user.setPicture(profilePhotoPath);
        }

        if (insurance != null && !insurance.isEmpty()) {
            String insurancePath = fileTransactionService.updateFileTransactional(
                    insurance, currentUser.getInsurance(), "insurance", "uploads/insurance/");
            user.setInsurance(insurancePath);
        }

        // Actualizar usuario en base de datos
        User updatedUser = userService.updateUser(id, user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Usuario actualizado exitosamente");
        response.put("user", updatedUser);

        return ResponseEntity.ok(response);
    }

    // Endpoint para actualizar solo datos del usuario (sin archivos)
    @PutMapping(value = "/{id}/profile", consumes = { "application/json" })
    public ResponseEntity<?> updateUserProfile(
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
            // Crear una respuesta más informativa para errores de validación
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("type", "VALIDATION_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Usuario no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("type", "RUNTIME_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("type", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}/picture")
    public ResponseEntity<?> updateUserPicture(
            @PathVariable Long id,
            @RequestBody Map<String, String> pictureData) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            // 🛡️ SANITIZACIÓN DE URL DE IMAGEN
            Map<String, String> sanitizedData = sanitizeUserInputMap(pictureData);

            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            String newPictureUrl = sanitizedData.get("pictureUrl");

            // Si se proporciona una URL nueva
            if (newPictureUrl != null) {
                // Eliminar archivo anterior si no es una URL externa
                deleteOldFile(user.getPicture());

                // Actualizar con la nueva URL (puede ser externa o null para eliminar)
                user.setPicture(newPictureUrl.trim().isEmpty() ? null : newPictureUrl);
            } else {
                // Si no se proporciona URL, eliminar foto actual
                deleteOldFile(user.getPicture());
                user.setPicture(null);
            }

            User updatedUser = userService.updateUser(id, user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Foto de perfil actualizada exitosamente");
            response.put("user", updatedUser);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Error de sanitización - entrada maliciosa detectada
            logger.warn("🚨 Intento de actualizar foto con URL maliciosa detectada: {}", e.getMessage());
            return ResponseEntity.badRequest().body("URL de imagen inválida: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la foto de perfil: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/insurance")
    public ResponseEntity<?> removeInsurance(@PathVariable Long id) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();

            // Eliminar archivo actual si existe
            deleteOldFile(user.getInsurance());
            user.setInsurance(null);

            User updatedUser = userService.updateUser(id, user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Documento de seguro eliminado exitosamente");
            response.put("user", updatedUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el documento de seguro: " + e.getMessage());
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<?> login(
            @Parameter(description = "Credenciales de login", required = true, schema = @Schema(example = "{\"email\":\"usuario@email.com\",\"password\":\"password123\"}")) @RequestBody Map<String, String> loginData) {
        try {
            // 🛡️ SANITIZACIÓN DE INPUTS DE LOGIN
            Map<String, String> sanitizedData = sanitizeUserInputMap(loginData);
            String email = sanitizedData.get("email");
            String password = sanitizedData.get("password");

            User user = userService.findByEmail(email);
            if (user == null || !userService.checkPassword(user, password)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
            }
            // Obtener información de la request para la sesión
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(request);

            // Generar par de tokens (access + refresh) con información de sesión
            TokenPair tokens = tokenService.generateTokenPairWithSession(user.getId(), user.isAdmin(), userAgent,
                    ipAddress);

            // Verificar si el perfil está completo (usando el mismo servicio OAuth2)
            boolean profileComplete = isLocalProfileComplete(user);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokens.getAccessToken());
            response.put("refreshToken", tokens.getRefreshToken());
            response.put("token", tokens.getAccessToken()); // Mantener compatibilidad
            response.put("admin", user.isAdmin());
            response.put("userId", user.getId());
            response.put("authProvider", user.getAuthProvider());
            response.put("profileComplete", profileComplete);
            response.put("firstName", user.getFirstName());
            response.put("picture", user.getPicture()); // Agregar foto de perfil

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Error de sanitización - entrada maliciosa detectada
            logger.warn("🚨 Intento de login con datos maliciosos detectado: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Datos de entrada inválidos: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error durante el login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
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
        response.put("loginUrl", "/oauth2/authorization/google");
        response.put("message", "Redirigir a esta URL para iniciar el flujo OAuth2 con Google");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        try {
            // 🛡️ SANITIZACIÓN DE EMAIL EN QUERY PARAM
            String sanitizedEmail = InputSanitizer.sanitizeEmail(email);
            boolean exists = userService.findByEmail(sanitizedEmail.trim().toLowerCase()) != null;
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("🚨 Email malicioso detectado en check-email: {}", e.getMessage());
            Map<String, Boolean> response = new HashMap<>();
            response.put("exists", false);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{id}/complete-profile")
    public ResponseEntity<?> completeProfile(@PathVariable Long id, @RequestBody Map<String, String> profileData) {
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
            if (sanitizedData.containsKey("identification")) {
                user.setIdentification(sanitizedData.get("identification"));
            }
            if (sanitizedData.containsKey("phone")) {
                user.setPhone(sanitizedData.get("phone"));
            }
            if (sanitizedData.containsKey("role")) {
                user.setRole(sanitizedData.get("role"));
            }

            // Campos opcionales adicionales
            if (sanitizedData.containsKey("lastName")) {
                user.setLastName(sanitizedData.get("lastName"));
            }
            if (sanitizedData.containsKey("birthdate")) {
                // Aquí podrías agregar lógica para parsear la fecha
            }

            User updatedUser = userService.updateUser(id, user);

            Map<String, Object> response = new HashMap<>();
            response.put("user", updatedUser);
            response.put("message", "Perfil completado exitosamente");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Error de sanitización - entrada maliciosa detectada
            logger.warn("🚨 Intento de completar perfil con datos maliciosos detectado: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Datos de entrada inválidos: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al completar el perfil: " + e.getMessage());
        }
    }

    @PostMapping("/simple-register")
    @Operation(summary = "Registro simple", description = "Registra un nuevo usuario con datos básicos", tags = {
            "Autenticación" })
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> simpleRegister(
            @Parameter(description = "Datos del nuevo usuario", required = true, schema = @Schema(example = "{\"firstName\":\"Juan\",\"lastName\":\"Pérez\",\"email\":\"juan@email.com\",\"password\":\"password123\"}")) @RequestBody Map<String, String> userData) {
        try {
            // 🛡️ SANITIZACIÓN DE INPUTS DE REGISTRO
            Map<String, String> sanitizedData = sanitizeUserInputMap(userData);

            // Validar datos requeridos
            String firstName = sanitizedData.get("firstName");
            String lastName = sanitizedData.get("lastName");
            String email = sanitizedData.get("email");
            String password = sanitizedData.get("password");

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
            newUser.setAuthProvider("LOCAL");
            newUser.setAdmin(false);

            // Los demás campos se dejan null - se completarán después
            User savedUser = userService.createUser(newUser);

            // Obtener información de la request para la sesión
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(request);

            // Generar par de tokens (access + refresh) con información de sesión
            TokenPair tokens = tokenService.generateTokenPairWithSession(savedUser.getId(), savedUser.isAdmin(),
                    userAgent, ipAddress);

            // Respuesta similar al OAuth2
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", tokens.getAccessToken());
            response.put("refreshToken", tokens.getRefreshToken());
            response.put("token", tokens.getAccessToken()); // Mantener compatibilidad
            response.put("userId", savedUser.getId());
            response.put("admin", savedUser.isAdmin());
            response.put("authProvider", savedUser.getAuthProvider());
            response.put("profileComplete", false); // Siempre false para registro simple
            response.put("firstName", savedUser.getFirstName());
            response.put("picture", savedUser.getPicture()); // Agregar foto de perfil
            response.put("message", "Usuario registrado exitosamente. Por favor completa tu perfil.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            // Error de sanitización - entrada maliciosa detectada
            logger.warn("🚨 Intento de registro con datos maliciosos detectado: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Datos de entrada inválidos: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar usuario: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> unifiedLogout(
            HttpServletRequest request,
            HttpServletResponse response,
            @AuthenticationPrincipal Object principal) {

        try {
            String authProvider = "LOCAL";
            String userInfo = "Unknown";
            Long userId = null;

            // Detectar tipo de autenticación
            if (principal instanceof User user) {
                // Autenticación JWT
                authProvider = user.getAuthProvider() != null ? user.getAuthProvider() : "LOCAL";
                userInfo = user.getEmail();
                userId = user.getId();
            } else if (principal instanceof OAuth2User oauth2User) {
                // Autenticación OAuth2
                authProvider = "GOOGLE";
                userInfo = oauth2User.getAttribute("email");
                // Para OAuth2, intentar obtener el userId del contexto si está disponible
                Object userIdAttr = oauth2User.getAttribute("sub");
                if (userIdAttr != null) {
                    userInfo += " (Google ID: " + userIdAttr + ")";
                }
            }

            // Realizar logout completo
            performLogout(request, response, authProvider);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Sesión cerrada exitosamente");
            responseBody.put("provider", authProvider);
            responseBody.put("user", userInfo);
            responseBody.put("userId", userId);
            responseBody.put("requiresGoogleLogout", "GOOGLE".equals(authProvider));
            responseBody.put("timestamp", System.currentTimeMillis());
            responseBody.put("success", true);

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            // Limpiar contexto aunque haya error
            SecurityContextHolder.clearContext();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al cerrar sesión");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("forceLogout", true);
            errorResponse.put("success", false);
            errorResponse.put("timestamp", System.currentTimeMillis());

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
            logLogoutEvent(authProvider);

        } catch (Exception e) {
            logger.error("Error durante logout con provider {}: {}", authProvider, e.getMessage(), e);
            // Asegurar que al menos el contexto se limpie
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Método para registrar eventos de logout
     */
    private void logLogoutEvent(String authProvider) {
        // Logging del evento de logout puede ser implementado aquí
        // usando un sistema de logging profesional como SLF4J
    }

    /**
     * Obtiene la dirección IP real del cliente, considerando proxies y load
     * balancers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Endpoint para cambiar contraseña
     */
    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordData) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            // 🛡️ SANITIZACIÓN DE PASSWORDS
            Map<String, String> sanitizedData = sanitizeUserInputMap(passwordData);
            String currentPassword = sanitizedData.get("currentPassword");
            String newPassword = sanitizedData.get("newPassword");

            // Validar que se proporcionen ambas contraseñas
            if (currentPassword == null || currentPassword.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "La contraseña actual es requerida");
                errorResponse.put("type", "VALIDATION_ERROR");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "La nueva contraseña es requerida");
                errorResponse.put("type", "VALIDATION_ERROR");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Verificar que el usuario tenga contraseña (no sea OAuth2)
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            if (user.getPassword() == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "No puedes cambiar la contraseña de una cuenta OAuth2");
                errorResponse.put("type", "VALIDATION_ERROR");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Cambiar contraseña
            userService.changePassword(id, currentPassword.trim(), newPassword.trim());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Contraseña cambiada exitosamente");
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("type", "VALIDATION_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Usuario no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("type", "RUNTIME_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("type", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint para cambiar el rol de administrador de un usuario
     */
    @PatchMapping("/{id}/admin")
    public ResponseEntity<?> toggleAdminRole(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> adminData) {
        if (!authUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // No permitir que un admin se quite sus propios permisos
        User authUser = getAuthenticatedUser();
        if (authUser != null && authUser.getId().equals(id)) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "No puedes cambiar tu propio rol de administrador");
            errorResponse.put("type", "VALIDATION_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        try {
            Boolean isAdmin = adminData.get("admin");
            if (isAdmin == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "El campo 'admin' es requerido");
                errorResponse.put("type", "VALIDATION_ERROR");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            user.setAdmin(isAdmin);
            User updatedUser = userService.updateUser(id, user);

            Map<String, Object> response = new HashMap<>();
            response.put("message",
                    isAdmin ? "Usuario promovido a administrador" : "Permisos de administrador removidos");
            response.put("user", updatedUser);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("Usuario no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("type", "RUNTIME_ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("type", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint adicional para validar si el token JWT sigue siendo válido
     */
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken() {
        try {
            User authUser = getAuthenticatedUser();

            // Verificar que tenemos un usuario válido
            if (authUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("valid", false);
                errorResponse.put("message", "Usuario no encontrado o token inválido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }

            // Construir respuesta exitosa
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("userId", authUser.getId());
            response.put("email", authUser.getEmail());
            response.put("firstName", authUser.getFirstName());
            response.put("lastName", authUser.getLastName());
            response.put("admin", authUser.isAdmin());
            response.put("authProvider", authUser.getAuthProvider());
            response.put("profileComplete", isLocalProfileComplete(authUser));

            // Campos adicionales útiles para el frontend
            response.put("picture", authUser.getPicture());
            response.put("googleId", authUser.getGoogleId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("message", "Error interno del servidor");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
