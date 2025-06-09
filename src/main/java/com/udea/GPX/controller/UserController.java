package com.udea.GPX.controller;

import com.udea.GPX.model.User;
import com.udea.GPX.service.UserService;
import com.udea.GPX.JwtUtil;
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

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @SuppressWarnings("unused")
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthUtils authUtils;

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

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private void deleteOldFile(String oldFilePath) {
        if (oldFilePath != null && !oldFilePath.trim().isEmpty()) {
            // Solo eliminar si es un archivo local (no una URL externa)
            if (!oldFilePath.startsWith("http://") && !oldFilePath.startsWith("https://")) {
                try {
                    File oldFile = new File(oldFilePath);
                    if (oldFile.exists()) {
                        boolean deleted = oldFile.delete();
                        if (deleted) {
                            System.out.println("Archivo anterior eliminado: " + oldFilePath);
                        } else {
                            System.err.println("No se pudo eliminar el archivo: " + oldFilePath);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error al eliminar archivo anterior: " + e.getMessage());
                }
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
        try {
            // Obtener el usuario actual para acceder a las rutas de archivos anteriores
            Optional<User> currentUserOpt = userService.getUserById(id);

            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                // Eliminar foto anterior si existe
                if (currentUserOpt.isPresent()) {
                    deleteOldFile(currentUserOpt.get().getPicture());
                }
                String profilePhotoPath = saveFile(profilePhoto, "picture");
                user.setPicture(profilePhotoPath);
            }
            if (insurance != null && !insurance.isEmpty()) {
                // Eliminar seguro anterior si existe
                if (currentUserOpt.isPresent()) {
                    deleteOldFile(currentUserOpt.get().getInsurance());
                }
                String insurancePath = saveFile(insurance, "insurance");
                user.setInsurance(insurancePath);
            }
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
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
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            String newPictureUrl = pictureData.get("pictureUrl");

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
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");
        User user = userService.findByEmail(email);
        if (user == null || !userService.checkPassword(user, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
        String token = jwtUtil.generateToken(user.getId(), user.isAdmin());

        // Verificar si el perfil está completo (usando el mismo servicio OAuth2)
        boolean profileComplete = isLocalProfileComplete(user);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("admin", user.isAdmin());
        response.put("userId", user.getId());
        response.put("authProvider", user.getAuthProvider());
        response.put("profileComplete", profileComplete);
        response.put("firstName", user.getFirstName());
        response.put("picture", user.getPicture()); // Agregar foto de perfil

        return ResponseEntity.ok(response);
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
        boolean exists = userService.findByEmail(email.trim().toLowerCase()) != null;
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/complete-profile")
    public ResponseEntity<?> completeProfile(@PathVariable Long id, @RequestBody Map<String, String> profileData) {
        if (!authUtils.isCurrentUserOrAdmin(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();

            // Solo actualizar campos esenciales para completar el perfil
            if (profileData.containsKey("identification")) {
                user.setIdentification(profileData.get("identification"));
            }
            if (profileData.containsKey("phone")) {
                user.setPhone(profileData.get("phone"));
            }
            if (profileData.containsKey("role")) {
                user.setRole(profileData.get("role"));
            }

            // Campos opcionales adicionales
            if (profileData.containsKey("lastName")) {
                user.setLastName(profileData.get("lastName"));
            }
            if (profileData.containsKey("birthdate")) {
                // Aquí podrías agregar lógica para parsear la fecha
            }

            User updatedUser = userService.updateUser(id, user);

            Map<String, Object> response = new HashMap<>();
            response.put("user", updatedUser);
            response.put("message", "Perfil completado exitosamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al completar el perfil: " + e.getMessage());
        }
    }

    @PostMapping("/simple-register")
    public ResponseEntity<?> simpleRegister(@RequestBody Map<String, String> userData) {
        try {
            // Validar datos requeridos
            String firstName = userData.get("firstName");
            String lastName = userData.get("lastName");
            String email = userData.get("email");
            String password = userData.get("password");

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

            // Generar token JWT
            String token = jwtUtil.generateToken(savedUser.getId(), savedUser.isAdmin());

            // Respuesta similar al OAuth2
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", savedUser.getId());
            response.put("admin", savedUser.isAdmin());
            response.put("authProvider", savedUser.getAuthProvider());
            response.put("profileComplete", false); // Siempre false para registro simple
            response.put("firstName", savedUser.getFirstName());
            response.put("picture", savedUser.getPicture()); // Agregar foto de perfil
            response.put("message", "Usuario registrado exitosamente. Por favor completa tu perfil.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
            // 1. Limpiar el contexto de seguridad de Spring
            SecurityContextHolder.clearContext();

            // 2. Invalidar la sesión HTTP si existe (especialmente importante para OAuth2)
            if (request.getSession(false) != null) {
                request.getSession(false).invalidate();
            }

            // 3. Usar el handler de logout de Spring Security
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

            // 4. Log del evento de logout
            logLogoutEvent(authProvider);

        } catch (Exception e) {
            // Log del error pero no fallar el logout
            System.err.println("Error durante logout: " + e.getMessage());
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
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

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

