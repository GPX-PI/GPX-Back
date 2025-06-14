package com.udea.gpx.service;

import com.udea.gpx.constants.AppConstants;
import com.udea.gpx.model.User;
import com.udea.gpx.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("UserService Tests")
class UserServiceTest {

  @Mock
  private IUserRepository userRepository;

  @Mock
  private PasswordService passwordService;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private User adminUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Configurar usuario de prueba
    testUser = new User();
    testUser.setId(1L);
    testUser.setFirstName("John");
    testUser.setLastName("Doe");
    testUser.setEmail("john.doe@example.com");
    testUser.setPassword("hashedPassword");
    testUser.setRole("USER");
    testUser.setAuthProvider("LOCAL");

    // Configurar usuario administrador
    adminUser = new User();
    adminUser.setId(2L);
    adminUser.setFirstName("Admin");
    adminUser.setLastName("User");
    adminUser.setEmail("admin@example.com");
    adminUser.setPassword("hashedAdminPassword");
    adminUser.setRole("ADMIN");
    adminUser.setAuthProvider("LOCAL");
  }

  // ==================== TESTS PARA getAllUsers ====================

  @Test
  @DisplayName("getAllUsers - Debe retornar lista de todos los usuarios")
  void getAllUsers_shouldReturnAllUsers() {
    // Given
    List<User> users = Arrays.asList(testUser, adminUser);
    when(userRepository.findAll()).thenReturn(users);

    // When
    List<User> result = userService.getAllUsers();

    // Then
    assertThat(result)
        .hasSize(2)
        .containsExactly(testUser, adminUser);
    verify(userRepository).findAll();
  }

  @Test
  @DisplayName("getAllUsers con paginación - Debe retornar página de usuarios")
  void getAllUsersWithPagination_shouldReturnPageOfUsers() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    Page<User> userPage = new PageImpl<>(Arrays.asList(testUser, adminUser), pageable, 2);
    when(userRepository.findAll(pageable)).thenReturn(userPage);

    // When
    Page<User> result = userService.getAllUsers(pageable);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(2);
    verify(userRepository).findAll(pageable);
  }

  // ==================== TESTS PARA getUserById ====================

  @Test
  @DisplayName("getUserById - Debe retornar usuario existente")
  void getUserById_shouldReturnExistingUser() {
    // Given
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

    // When
    Optional<User> result = userService.getUserById(1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    verify(userRepository).findById(1L);
  }

  @Test
  @DisplayName("getUserById - Debe actualizar authProvider nulo")
  void getUserById_shouldUpdateNullAuthProvider() {
    // Given
    User userWithNullAuth = new User();
    userWithNullAuth.setId(1L);
    userWithNullAuth.setFirstName("Test");
    userWithNullAuth.setAuthProvider(null);

    when(userRepository.findById(1L)).thenReturn(Optional.of(userWithNullAuth));
    when(userRepository.save(any(User.class))).thenReturn(userWithNullAuth);

    // When
    Optional<User> result = userService.getUserById(1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getAuthProvider()).isEqualTo("LOCAL");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("getUserById - Debe actualizar authProvider vacío")
  void getUserById_shouldUpdateEmptyAuthProvider() {
    // Given
    User userWithEmptyAuth = new User();
    userWithEmptyAuth.setId(1L);
    userWithEmptyAuth.setFirstName("Test");
    userWithEmptyAuth.setAuthProvider("");

    when(userRepository.findById(1L)).thenReturn(Optional.of(userWithEmptyAuth));
    when(userRepository.save(any(User.class))).thenReturn(userWithEmptyAuth);

    // When
    Optional<User> result = userService.getUserById(1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getAuthProvider()).isEqualTo("LOCAL");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("getUserById - Debe manejar excepción al actualizar authProvider")
  void getUserById_shouldHandleExceptionWhenUpdatingAuthProvider() {
    // Given
    User userWithNullAuth = new User();
    userWithNullAuth.setId(1L);
    userWithNullAuth.setFirstName("Test");
    userWithNullAuth.setAuthProvider(null);

    when(userRepository.findById(1L)).thenReturn(Optional.of(userWithNullAuth));
    when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Error al guardar"));

    // When
    Optional<User> result = userService.getUserById(1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getAuthProvider()).isEqualTo("LOCAL");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("getUserById - Debe retornar vacío para usuario inexistente")
  void getUserById_shouldReturnEmptyForNonExistentUser() {
    // Given
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    // When
    Optional<User> result = userService.getUserById(999L);

    // Then
    assertThat(result).isEmpty();
    verify(userRepository).findById(999L);
  }

  // ==================== TESTS PARA createUser ====================

  @Test
  @DisplayName("createUser - Debe crear usuario con contraseña hasheada")
  void createUser_shouldCreateUserWithHashedPassword() {
    // Given
    User newUser = new User();
    newUser.setEmail("new@example.com");
    newUser.setPassword("ValidPassword123!");
    newUser.setFirstName("New");
    newUser.setLastName("User");

    when(passwordService.isPasswordValid(anyString())).thenReturn(true);
    when(passwordService.hashPassword(anyString())).thenReturn("hashedPassword");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User createdUser = userService.createUser(newUser);

    // Then
    assertThat(createdUser).isNotNull();
    assertThat(createdUser.getPassword()).isEqualTo("hashedPassword");
    verify(passwordService).isPasswordValid("ValidPassword123!");
    verify(passwordService).hashPassword("ValidPassword123!");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
    assertThat(savedUser.getPassword()).isEqualTo("hashedPassword");
  }

  @Test
  @DisplayName("createUser - Debe sanitizar campos del usuario")
  void createUser_shouldSanitizeUserFields() {
    // Given
    User newUser = new User();
    newUser.setEmail(" new@example.com "); // con espacios para sanitizar
    newUser.setPassword("ValidPassword123!");
    newUser.setFirstName(" John "); // con espacios para sanitizar
    newUser.setLastName(" Doe "); // con espacios para sanitizar
    newUser.setIdentification(" 1234567890 "); // con espacios para sanitizar
    newUser.setPhone(" +1234567890 "); // con espacios para sanitizar

    when(passwordService.isPasswordValid(anyString())).thenReturn(true);
    when(passwordService.hashPassword(anyString())).thenReturn("hashedPassword");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User createdUser = userService.createUser(newUser);

    // Then
    assertThat(createdUser).isNotNull();

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getEmail()).isEqualTo("new@example.com"); // sanitizado
    assertThat(savedUser.getFirstName()).isEqualTo("John"); // sanitizado
    assertThat(savedUser.getLastName()).isEqualTo("Doe"); // sanitizado
    assertThat(savedUser.getIdentification()).isEqualTo("1234567890"); // sanitizado
    assertThat(savedUser.getPhone()).isEqualTo("+1234567890"); // sanitizado
  }

  @Test
  @DisplayName("createUser - Debe lanzar excepción si el email ya está en uso")
  void createUser_shouldThrowExceptionWhenEmailIsAlreadyInUse() {
    // Given
    User newUser = new User();
    newUser.setEmail("existing@example.com");
    newUser.setPassword("ValidPassword123!");

    when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(testUser));

    // When & Then
    assertThatThrownBy(() -> userService.createUser(newUser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(AppConstants.Messages.EMAIL_YA_EN_USO);

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("createUser - Debe lanzar excepción si la contraseña es inválida")
  void createUser_shouldThrowExceptionWhenPasswordIsInvalid() {
    // Given
    User newUser = new User();
    newUser.setEmail("new@example.com");
    newUser.setPassword("weak");

    when(passwordService.isPasswordValid("weak")).thenReturn(false);
    when(passwordService.getPasswordValidationMessage("weak")).thenReturn("La contraseña es demasiado débil");

    // When & Then
    assertThatThrownBy(() -> userService.createUser(newUser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La contraseña es demasiado débil");

    verify(passwordService).isPasswordValid("weak");
    verify(userRepository, never()).save(any(User.class));
  }

  // ==================== TESTS PARA updateUser ====================

  @Test
  @DisplayName("updateUser - Debe actualizar campos del usuario")
  void updateUser_shouldUpdateUserFields() {
    // Given
    User updatedUser = new User();
    updatedUser.setFirstName("Updated");
    updatedUser.setLastName("Name");
    updatedUser.setPhone("+9876543210");
    updatedUser.setBirthdate(LocalDate.of(1990, 5, 15));

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = userService.updateUser(1L, updatedUser);

    // Then
    assertThat(result).isNotNull();

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getFirstName()).isEqualTo("Updated");
    assertThat(savedUser.getLastName()).isEqualTo("Name");
    assertThat(savedUser.getPhone()).isEqualTo("+9876543210");
    assertThat(savedUser.getBirthdate()).isEqualTo(LocalDate.of(1990, 5, 15));
  }

  @Test
  @DisplayName("updateUser - Debe actualizar email si es válido")
  void updateUser_shouldUpdateEmailIfValid() {
    // Given
    User updatedUser = new User();
    updatedUser.setEmail("newemail@example.com");

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.findByEmail("newemail@example.com")).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = userService.updateUser(1L, updatedUser);

    // Then
    assertThat(result).isNotNull();

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getEmail()).isEqualTo("newemail@example.com");
  }

  @Test
  @DisplayName("updateUser - Debe lanzar excepción si el email ya está en uso por otro usuario")
  void updateUser_shouldThrowExceptionWhenEmailIsAlreadyUsedByAnotherUser() {
    // Given
    User updatedUser = new User();
    updatedUser.setEmail("admin@example.com");

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

    // When & Then
    assertThatThrownBy(() -> userService.updateUser(1L, updatedUser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(AppConstants.Messages.EMAIL_YA_EN_USO_POR_OTRO_USUARIO);

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("updateUser - Debe lanzar excepción si el usuario no existe")
  void updateUser_shouldThrowExceptionIfUserDoesNotExist() {
    // Given
    User updatedUser = new User();
    updatedUser.setFirstName("Updated");

    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.updateUser(999L, updatedUser))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining(AppConstants.Messages.USUARIO_NO_ENCONTRADO);

    verify(userRepository, never()).save(any(User.class));
  }

  // ==================== TESTS PARA updateUserProfile ====================

  @Test
  @DisplayName("updateUserProfile - Debe actualizar perfil sin archivos")
  void updateUserProfile_shouldUpdateProfileWithoutFiles() {
    // Given
    User updatedUser = new User();
    updatedUser.setFirstName("Updated");
    updatedUser.setLastName("Profile");
    updatedUser.setPhone("+9876543210");
    updatedUser.setPicture("should-not-update.jpg"); // No debería actualizarse
    updatedUser.setInsurance("should-not-update.pdf"); // No debería actualizarse

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = userService.updateUserProfile(1L, updatedUser);

    // Then
    assertThat(result).isNotNull();

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getFirstName()).isEqualTo("Updated");
    assertThat(savedUser.getLastName()).isEqualTo("Profile");
    assertThat(savedUser.getPhone()).isEqualTo("+9876543210");

    // Verificar que picture e insurance no se actualizaron
    assertThat(savedUser.getPicture()).isEqualTo(testUser.getPicture());
    assertThat(savedUser.getInsurance()).isEqualTo(testUser.getInsurance());
  }

  @Test
  @DisplayName("updateUserProfile - Debe sanitizar campos de perfil")
  void updateUserProfile_shouldSanitizeProfileFields() {
    // Given
    User updatedUser = new User();
    updatedUser.setFirstName(" Updated ");
    updatedUser.setLastName(" Profile ");
    updatedUser.setPhone(" +1234567890 ");
    updatedUser.setEmergencyPhone(" +0987654321 ");
    updatedUser.setInstagram(" instagram_handle ");

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = userService.updateUserProfile(1L, updatedUser);

    // Then
    assertThat(result)
        .isNotNull()
        .extracting(
            User::getFirstName,
            User::getLastName,
            User::getPhone,
            User::getEmergencyPhone,
            User::getInstagram)
        .containsExactly(
            "Updated",
            "Profile",
            "+1234567890",
            "+0987654321",
            "instagram_handle");

    verify(userRepository).save(any(User.class));
  }

  // ==================== TESTS PARA findByEmail ====================

  @Test
  @DisplayName("findByEmail - Debe encontrar usuario por email")
  void findByEmail_shouldFindUserByEmail() {
    // Given
    when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));

    // When
    User result = userService.findByEmail("john.doe@example.com");

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(testUser);
  }

  @Test
  @DisplayName("findByEmail - Debe actualizar authProvider nulo al encontrar por email")
  void findByEmail_shouldUpdateNullAuthProviderWhenFoundByEmail() {
    // Given
    User userWithNullAuth = new User();
    userWithNullAuth.setId(1L);
    userWithNullAuth.setEmail("nullauth@example.com");
    userWithNullAuth.setAuthProvider(null);

    when(userRepository.findByEmail("nullauth@example.com")).thenReturn(Optional.of(userWithNullAuth));
    when(userRepository.save(any(User.class))).thenReturn(userWithNullAuth);

    // When
    User result = userService.findByEmail("nullauth@example.com");

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getAuthProvider()).isEqualTo("LOCAL");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("findByEmail - Debe retornar null si no encuentra el email")
  void findByEmail_shouldReturnNullWhenEmailNotFound() {
    // Given
    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    // When
    User result = userService.findByEmail("nonexistent@example.com");

    // Then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("findByEmail - Debe buscar usuario por email con espacios")
  void findByEmail_shouldHandleEmailWithSpaces() {
    // Given
    String emailWithSpaces = "  john.doe@example.com  ";

    // Se espera que el método findByEmail no sanea el email, sino que lo pasa
    // directamente
    when(userRepository.findByEmail(emailWithSpaces)).thenReturn(Optional.empty());

    // When
    User result = userService.findByEmail(emailWithSpaces);

    // Then
    assertThat(result).isNull();

    // Verificar que la búsqueda se realizó con el email exactamente como se pasó
    verify(userRepository).findByEmail(emailWithSpaces);
  }

  // ==================== TESTS PARA checkPassword ====================

  @Test
  @DisplayName("checkPassword - Debe verificar contraseña BCrypt")
  void checkPassword_shouldVerifyBCryptPassword() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPassword("$2a$10$hashedBCryptPassword"); // Formato BCrypt

    when(passwordService.isBCryptHash("$2a$10$hashedBCryptPassword")).thenReturn(true);
    when(passwordService.verifyPassword("correctPassword", "$2a$10$hashedBCryptPassword")).thenReturn(true);

    // When
    boolean result = userService.checkPassword(user, "correctPassword");

    // Then
    assertThat(result).isTrue();
    verify(passwordService).isBCryptHash("$2a$10$hashedBCryptPassword");
    verify(passwordService).verifyPassword("correctPassword", "$2a$10$hashedBCryptPassword");
    verify(userRepository, never()).save(any(User.class)); // No debe actualizar la contraseña
  }

  @Test
  @DisplayName("checkPassword - Debe fallar verificación de contraseña BCrypt incorrecta")
  void checkPassword_shouldFailVerificationForIncorrectBCryptPassword() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPassword("$2a$10$hashedBCryptPassword"); // Formato BCrypt

    when(passwordService.isBCryptHash("$2a$10$hashedBCryptPassword")).thenReturn(true);
    when(passwordService.verifyPassword("wrongPassword", "$2a$10$hashedBCryptPassword")).thenReturn(false);

    // When
    boolean result = userService.checkPassword(user, "wrongPassword");

    // Then
    assertThat(result).isFalse();
    verify(passwordService).verifyPassword("wrongPassword", "$2a$10$hashedBCryptPassword");
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("checkPassword - Debe verificar y actualizar contraseña en texto plano")
  void checkPassword_shouldVerifyAndUpdatePlainTextPassword() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPassword("plainTextPassword"); // No formato BCrypt

    when(passwordService.isBCryptHash("plainTextPassword")).thenReturn(false);
    when(passwordService.hashPassword("plainTextPassword")).thenReturn("$2a$10$newHashedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);

    // When
    boolean result = userService.checkPassword(user, "plainTextPassword");

    // Then
    assertThat(result).isTrue();

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getPassword()).isEqualTo("$2a$10$newHashedPassword");
  }

  @Test
  @DisplayName("checkPassword - Debe manejar excepción al actualizar contraseña en texto plano")
  void checkPassword_shouldHandleExceptionWhenUpdatingPlainTextPassword() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPassword("plainTextPassword"); // No formato BCrypt

    when(passwordService.isBCryptHash("plainTextPassword")).thenReturn(false);
    when(passwordService.hashPassword("plainTextPassword")).thenReturn("$2a$10$newHashedPassword");
    when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Error al guardar"));

    // When
    boolean result = userService.checkPassword(user, "plainTextPassword");

    // Then
    assertThat(result).isTrue(); // Aún debe retornar true porque la verificación fue exitosa
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("checkPassword - Debe fallar para contraseña en texto plano incorrecta")
  void checkPassword_shouldFailForIncorrectPlainTextPassword() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPassword("plainTextPassword"); // No formato BCrypt

    when(passwordService.isBCryptHash("plainTextPassword")).thenReturn(false);

    // When
    boolean result = userService.checkPassword(user, "wrongPassword");

    // Then
    assertThat(result).isFalse();
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("checkPassword - Debe retornar falso si la contraseña es nula")
  void checkPassword_shouldReturnFalseIfPasswordIsNull() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPassword(null);

    // When
    boolean result = userService.checkPassword(user, "anyPassword");

    // Then
    assertThat(result).isFalse();
    verify(passwordService, never()).isBCryptHash(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("checkPassword - Debe retornar falso si la contraseña proporcionada es nula")
  void checkPassword_shouldReturnFalseIfProvidedPasswordIsNull() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPassword("somePassword");

    // When
    boolean result = userService.checkPassword(user, null);

    // Then
    assertThat(result).isFalse();
    verify(passwordService, never()).isBCryptHash(any());
    verify(userRepository, never()).save(any());
  }

  // ==================== TESTS PARA changePassword ====================

  @Test
  @DisplayName("changePassword - Debe cambiar contraseña exitosamente")
  void changePassword_shouldChangePasswordSuccessfully() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPassword("$2a$10$oldHashedPassword");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(passwordService.isPasswordValid("newPassword123")).thenReturn(true);
    when(passwordService.verifyPassword("newPassword123", "$2a$10$oldHashedPassword")).thenReturn(false);
    when(passwordService.hashPassword("newPassword123")).thenReturn("$2a$10$newHashedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);

    // Mockear checkPassword para que retorne true con la contraseña actual
    doReturn(true).when(passwordService).verifyPassword("currentPassword", "$2a$10$oldHashedPassword");
    doReturn(true).when(passwordService).isBCryptHash("$2a$10$oldHashedPassword");

    // When
    userService.changePassword(1L, "currentPassword", "newPassword123");

    // Then
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getPassword()).isEqualTo("$2a$10$newHashedPassword");
  }

  @Test
  @DisplayName("changePassword - Debe lanzar excepción si el usuario no existe")
  void changePassword_shouldThrowExceptionIfUserDoesNotExist() {
    // Given
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.changePassword(999L, "currentPassword", "newPassword123"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining(AppConstants.Messages.USUARIO_NO_ENCONTRADO);

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("changePassword - Debe lanzar excepción si la contraseña actual es incorrecta")
  void changePassword_shouldThrowExceptionIfCurrentPasswordIsIncorrect() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPassword("$2a$10$oldHashedPassword");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    // Mockear checkPassword para que retorne false (contraseña incorrecta)
    doReturn(false).when(passwordService).verifyPassword("wrongCurrentPassword", "$2a$10$oldHashedPassword");
    doReturn(true).when(passwordService).isBCryptHash("$2a$10$oldHashedPassword");

    // When & Then
    assertThatThrownBy(() -> userService.changePassword(1L, "wrongCurrentPassword", "newPassword123"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(AppConstants.Messages.PASSWORD_ACTUAL_INCORRECTA);

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("changePassword - Debe lanzar excepción si la nueva contraseña es inválida")
  void changePassword_shouldThrowExceptionIfNewPasswordIsInvalid() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPassword("$2a$10$oldHashedPassword");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(passwordService.isPasswordValid("weakPassword")).thenReturn(false);
    when(passwordService.getPasswordValidationMessage("weakPassword")).thenReturn("La contraseña es demasiado débil");

    // Mockear checkPassword para que retorne true con la contraseña actual
    doReturn(true).when(passwordService).verifyPassword("currentPassword", "$2a$10$oldHashedPassword");
    doReturn(true).when(passwordService).isBCryptHash("$2a$10$oldHashedPassword");

    // When & Then
    assertThatThrownBy(() -> userService.changePassword(1L, "currentPassword", "weakPassword"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La contraseña es demasiado débil");

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("changePassword - Debe lanzar excepción si la nueva contraseña es igual a la actual")
  void changePassword_shouldThrowExceptionIfNewPasswordIsSameAsOld() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPassword("$2a$10$oldHashedPassword");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(passwordService.isPasswordValid("currentPassword")).thenReturn(true);
    when(passwordService.verifyPassword("currentPassword", "$2a$10$oldHashedPassword")).thenReturn(true);

    // Mockear checkPassword para que retorne true con la contraseña actual
    doReturn(true).when(passwordService).isBCryptHash("$2a$10$oldHashedPassword");

    // When & Then
    assertThatThrownBy(() -> userService.changePassword(1L, "currentPassword", "currentPassword"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(AppConstants.Messages.PASSWORD_NUEVA_DEBE_SER_DIFERENTE);

    verify(userRepository, never()).save(any());
  }

  // ==================== TESTS PARA updateUserPictureUrl ====================

  @Test
  @DisplayName("updateUserPictureUrl - Debe actualizar URL de imagen")
  void updateUserPictureUrl_shouldUpdatePictureUrl() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPicture("old-picture.jpg");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = userService.updateUserPictureUrl(1L, "new-picture.jpg");

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getPicture()).isEqualTo("new-picture.jpg");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getPicture()).isEqualTo("new-picture.jpg");
  }

  @Test
  @DisplayName("updateUserPictureUrl - Debe eliminar URL de imagen si es nula o vacía")
  void updateUserPictureUrl_shouldRemovePictureUrlIfNullOrEmpty() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setPicture("old-picture.jpg");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = userService.updateUserPictureUrl(1L, "");

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getPicture()).isNull();

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getPicture()).isNull();
  }

  @Test
  @DisplayName("updateUserPictureUrl - Debe lanzar excepción si la URL es demasiado larga")
  void updateUserPictureUrl_shouldThrowExceptionIfUrlIsTooLong() {
    // Given
    User user = new User();
    user.setId(1L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    // Crear una URL muy larga (más de 2048 caracteres)
    StringBuilder longUrl = new StringBuilder();
    for (int i = 0; i < 2049; i++) {
      longUrl.append("a");
    }
    String tooLongUrl = longUrl.toString();

    // When & Then
    assertThatThrownBy(() -> userService.updateUserPictureUrl(1L, tooLongUrl))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("URL demasiado larga");

    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("updateUserPictureUrl - Debe lanzar excepción si el usuario no existe")
  void updateUserPictureUrl_shouldThrowExceptionIfUserDoesNotExist() {
    // Given
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.updateUserPictureUrl(999L, "picture.jpg"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining(AppConstants.Messages.USUARIO_NO_ENCONTRADO);

    verify(userRepository, never()).save(any());
  }

  // ==================== TESTS PARA updateUserInsuranceUrl ====================

  @Test
  @DisplayName("updateUserInsuranceUrl - Debe actualizar URL del seguro")
  void updateUserInsuranceUrl_shouldUpdateInsuranceUrl() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setInsurance("old-insurance.pdf");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = userService.updateUserInsuranceUrl(1L, "new-insurance.pdf");

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getInsurance()).isEqualTo("new-insurance.pdf");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getInsurance()).isEqualTo("new-insurance.pdf");
  }

  @Test
  @DisplayName("updateUserInsuranceUrl - Debe eliminar URL del seguro si es nula o vacía")
  void updateUserInsuranceUrl_shouldRemoveInsuranceUrlIfNullOrEmpty() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setInsurance("old-insurance.pdf");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = userService.updateUserInsuranceUrl(1L, "");

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getInsurance()).isNull();

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getInsurance()).isNull();
  }

  // ==================== TESTS PARA removeUserInsurance ====================

  @Test
  @DisplayName("removeUserInsurance - Debe eliminar el seguro del usuario")
  void removeUserInsurance_shouldRemoveUserInsurance() {
    // Given
    User user = new User();
    user.setId(1L);
    user.setInsurance("insurance.pdf");

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = userService.removeUserInsurance(1L);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getInsurance()).isNull();

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser.getInsurance()).isNull();
  }

  @Test
  @DisplayName("removeUserInsurance - Debe lanzar excepción si el usuario no existe")
  void removeUserInsurance_shouldThrowExceptionIfUserDoesNotExist() {
    // Given
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.removeUserInsurance(999L))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining(AppConstants.Messages.USUARIO_NO_ENCONTRADO);

    verify(userRepository, never()).save(any());
  }

  // ==================== TESTS PARA updateMedicalFields ====================
  // @Test
  @DisplayName("updateUser - Debe actualizar campos médicos")
  void updateUser_shouldUpdateMedicalFields() {
    // Given
    User updatedUser = new User();
    updatedUser.setEps("Nueva EPS");
    updatedUser.setRh("O+");
    updatedUser.setAlergies("Penicilina, polen");

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = userService.updateUser(1L, updatedUser);

    // Then
    assertThat(result).isNotNull();

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser)
        .extracting(User::getEps, User::getRh, User::getAlergies)
        .containsExactly("Nueva EPS", "O+", "Penicilina, polen");
  }

  @Test
  @DisplayName("updateUser - Debe actualizar campos sociales")
  void updateUser_shouldUpdateSocialFields() {
    // Given
    User updatedUser = new User();
    updatedUser.setWikiloc("wikiloc_user");
    updatedUser.setTerrapirata("terra_pirata");
    updatedUser.setInstagram("instagram_handle");
    updatedUser.setFacebook("facebook_profile");

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = userService.updateUser(1L, updatedUser);

    // Then
    assertThat(result).isNotNull();

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User savedUser = userCaptor.getValue();

    assertThat(savedUser)
        .extracting(User::getWikiloc, User::getTerrapirata, User::getInstagram, User::getFacebook)
        .containsExactly("wikiloc_user", "terra_pirata", "instagram_handle", "facebook_profile");
  }
}
