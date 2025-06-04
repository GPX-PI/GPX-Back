package com.udea.GPX.controller;

import com.udea.GPX.model.User;
import com.udea.GPX.service.UserService;
import com.udea.GPX.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

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

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin() && !authUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private String saveFile(MultipartFile file) throws Exception {
        String uploadsDir = "uploads/";
        String originalFilename = file.getOriginalFilename();
        String filePath = uploadsDir + System.currentTimeMillis() + "_" + originalFilename;
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return filePath;
    }

    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestPart("user") User user,
            @RequestPart(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            @RequestPart(value = "insurance", required = false) MultipartFile insurance) {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin() && !authUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                String profilePhotoPath = saveFile(profilePhoto);
                user.setPicture(profilePhotoPath);
            }
            if (insurance != null && !insurance.isEmpty()) {
                String insurancePath = saveFile(insurance);
                user.setInsurance(insurancePath);
            }
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAdminUsers() {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin()) {
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
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authUser.isAdmin() && !authUser.getId().equals(id)) {
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
            newUser.setPassword(password); // En producción, usar encoder para hashear
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
            response.put("message", "Usuario registrado exitosamente. Por favor completa tu perfil.");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al registrar usuario: " + e.getMessage());
        }
    }
}