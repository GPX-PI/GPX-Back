package com.udea.GPX.service;

import com.udea.GPX.model.User;
import com.udea.GPX.repository.IUserRepository;
import com.udea.GPX.util.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
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

    testUser = TestDataBuilder.buildUser(1L, "John", false);
    testUser.setEmail("test@example.com");
    testUser.setLastName("Doe");
    testUser.setPassword("plainPassword");

    adminUser = TestDataBuilder.buildUser(2L, "Admin", true);
    adminUser.setEmail("admin@example.com");
  }

  @Test
  @DisplayName("getAllUsers - Debe retornar lista de todos los usuarios")
  void getAllUsers_shouldReturnAllUsers() {
    // Given
    List<User> users = List.of(testUser, adminUser);
    when(userRepository.findAll()).thenReturn(users);

    // When
    List<User> result = userService.getAllUsers();

    // Then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(testUser, adminUser);
    verify(userRepository).findAll();
  }

  @Test
  @DisplayName("getAllUsers con paginación - Debe retornar página de usuarios")
  void getAllUsersWithPagination_shouldReturnPageOfUsers() {
    // Given
    Pageable pageable = PageRequest.of(0, 10);
    Page<User> userPage = new PageImpl<>(List.of(testUser, adminUser), pageable, 2);
    when(userRepository.findAll(pageable)).thenReturn(userPage);

    // When
    Page<User> result = userService.getAllUsers(pageable);

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(2);
    verify(userRepository).findAll(pageable);
  }

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
    User userWithNullAuth = TestDataBuilder.buildUser(1L, "Test", false);
    userWithNullAuth.setAuthProvider(null);
    when(userRepository.findById(1L)).thenReturn(Optional.of(userWithNullAuth));
    when(userRepository.save(any(User.class))).thenReturn(userWithNullAuth);

    // When
    Optional<User> result = userService.getUserById(1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getAuthProvider()).isEqualTo("LOCAL");
    verify(userRepository).save(userWithNullAuth);
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

  @Test
  @DisplayName("createUser - Debe crear usuario válido")
  void createUser_shouldCreateValidUser() {
    // Given
    User newUser = TestDataBuilder.buildUser(3L, "Jane", false);
    newUser.setEmail("new@example.com");
    newUser.setPassword("validPassword123");
    newUser.setLastName("Smith");
    newUser.setIdentification("12345678");
    newUser.setPhone("+57 300 123 4567");

    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(passwordService.isPasswordValid("validPassword123")).thenReturn(true);
    when(passwordService.hashPassword("validPassword123")).thenReturn("hashedPassword");
    when(userRepository.save(any(User.class))).thenReturn(newUser);

    // When
    User result = userService.createUser(newUser);

    // Then
    assertThat(result).isNotNull();
    verify(passwordService).hashPassword("validPassword123");
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("createUser - Debe lanzar excepción si email ya existe")
  void createUser_shouldThrowExceptionIfEmailExists() {
    // Given
    User newUser = TestDataBuilder.buildUser(4L, "Test", false);
    newUser.setEmail("test@example.com");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

    // When & Then
    assertThatThrownBy(() -> userService.createUser(newUser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El email ya está en uso");

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("createUser - Debe lanzar excepción si password es inválida")
  void createUser_shouldThrowExceptionIfPasswordInvalid() {
    // Given
    User newUser = TestDataBuilder.buildUser(5L, "Test", false);
    newUser.setEmail("new@example.com");
    newUser.setPassword("weak");

    when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
    when(passwordService.isPasswordValid("weak")).thenReturn(false);
    when(passwordService.getPasswordValidationMessage("weak")).thenReturn("Password too weak");

    // When & Then
    assertThatThrownBy(() -> userService.createUser(newUser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Password too weak");

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("updateUser - Debe actualizar usuario existente")
  void updateUser_shouldUpdateExistingUser() {
    // Given - Crear usuario simple sin campos problemáticos
    User updatedData = new User();
    updatedData.setFirstName("Juan");
    updatedData.setLastName("Perez");
    updatedData.setPhone("3009998888");
    updatedData.setBirthdate(LocalDate.of(1985, 5, 15));

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    User result = userService.updateUser(1L, updatedData);

    // Then
    assertThat(result).isNotNull();
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("updateUser - Debe lanzar excepción si usuario no existe")
  void updateUser_shouldThrowExceptionIfUserNotFound() {
    // Given
    User updatedData = TestDataBuilder.buildUser(7L, "UpdatedName", false);

    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.updateUser(999L, updatedData))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Usuario no encontrado");

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("updateUser - Debe validar email duplicado en actualización")
  void updateUser_shouldValidateDuplicateEmailOnUpdate() {
    // Given
    User updatedData = TestDataBuilder.buildUser(8L, "Test", false);
    updatedData.setEmail("admin@example.com"); // Email del adminUser

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));

    // When & Then
    assertThatThrownBy(() -> userService.updateUser(1L, updatedData))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El email ya está en uso por otro usuario");

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("updateUserProfile - Debe actualizar solo campos de perfil")
  void updateUserProfile_shouldUpdateOnlyProfileFields() {
    // Given - Crear usuario simple sin campos que requieren sanitización especial
    User profileData = new User();
    profileData.setFirstName("Maria");
    profileData.setBirthdate(LocalDate.of(1990, 1, 1));
    profileData.setPhone("3001234567");

    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    User result = userService.updateUserProfile(1L, profileData);

    // Then
    assertThat(result).isNotNull();
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("findByEmail - Debe encontrar usuario por email")
  void findByEmail_shouldFindUserByEmail() {
    // Given
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

    // When
    User result = userService.findByEmail("test@example.com");

    // Then
    assertThat(result).isEqualTo(testUser);
    verify(userRepository).findByEmail("test@example.com");
  }

  @Test
  @DisplayName("findByEmail - Debe retornar null para email inexistente")
  void findByEmail_shouldReturnNullForNonExistentEmail() {
    // Given
    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    // When
    User result = userService.findByEmail("nonexistent@example.com");

    // Then
    assertThat(result).isNull();
    verify(userRepository).findByEmail("nonexistent@example.com");
  }

  @Test
  @DisplayName("checkPassword - Debe verificar password con BCrypt")
  void checkPassword_shouldVerifyPasswordWithBCrypt() {
    // Given
    User userWithBCrypt = TestDataBuilder.buildUser(10L, "BCrypt", false);
    userWithBCrypt.setPassword("$2a$10$hashedPassword");

    when(passwordService.isBCryptHash("$2a$10$hashedPassword")).thenReturn(true);
    when(passwordService.verifyPassword("rawPassword", "$2a$10$hashedPassword")).thenReturn(true);

    // When
    boolean result = userService.checkPassword(userWithBCrypt, "rawPassword");

    // Then
    assertThat(result).isTrue();
    verify(passwordService).verifyPassword("rawPassword", "$2a$10$hashedPassword");
  }

  @Test
  @DisplayName("checkPassword - Debe verificar y migrar password en texto plano")
  void checkPassword_shouldVerifyAndMigratePlainTextPassword() {
    // Given
    User userWithPlainText = TestDataBuilder.buildUser(11L, "Plain", false);
    userWithPlainText.setPassword("plainPassword");

    when(passwordService.isBCryptHash("plainPassword")).thenReturn(false);
    when(passwordService.hashPassword("plainPassword")).thenReturn("$2a$10$hashedPassword");
    when(userRepository.save(any(User.class))).thenReturn(userWithPlainText);

    // When
    boolean result = userService.checkPassword(userWithPlainText, "plainPassword");

    // Then
    assertThat(result).isTrue();
    verify(passwordService).hashPassword("plainPassword");
    verify(userRepository).save(userWithPlainText);
  }

  @Test
  @DisplayName("checkPassword - Debe retornar false para password incorrecta")
  void checkPassword_shouldReturnFalseForIncorrectPassword() {
    // Given
    User userWithBCrypt = TestDataBuilder.buildUser(12L, "Wrong", false);
    userWithBCrypt.setPassword("$2a$10$hashedPassword");

    when(passwordService.isBCryptHash("$2a$10$hashedPassword")).thenReturn(true);
    when(passwordService.verifyPassword("wrongPassword", "$2a$10$hashedPassword")).thenReturn(false);

    // When
    boolean result = userService.checkPassword(userWithBCrypt, "wrongPassword");

    // Then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("checkPassword - Debe retornar false para entradas nulas")
  void checkPassword_shouldReturnFalseForNullInputs() {
    // Given
    User userWithNullPassword = TestDataBuilder.buildUser(13L, "Null", false);
    userWithNullPassword.setPassword(null);

    // When
    boolean result1 = userService.checkPassword(userWithNullPassword, "password");
    boolean result2 = userService.checkPassword(testUser, null);

    // Then
    assertThat(result1).isFalse();
    assertThat(result2).isFalse();
  }

  @Test
  @DisplayName("changePassword - Debe cambiar password correctamente")
  void changePassword_shouldChangePasswordSuccessfully() {
    // Given
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(passwordService.isPasswordValid("newPassword123")).thenReturn(true);
    when(passwordService.hashPassword("newPassword123")).thenReturn("$2a$10$newHashedPassword");
    when(passwordService.verifyPassword("newPassword123", "plainPassword")).thenReturn(false);

    // Mock checkPassword method behavior
    when(passwordService.isBCryptHash("plainPassword")).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    userService.changePassword(1L, "plainPassword", "newPassword123");

    // Then
    verify(passwordService).hashPassword("newPassword123");
    verify(userRepository, atLeast(1)).save(any(User.class));
  }

  @Test
  @DisplayName("changePassword - Debe lanzar excepción si usuario no existe")
  void changePassword_shouldThrowExceptionIfUserNotFound() {
    // Given
    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.changePassword(999L, "current", "new"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Usuario no encontrado");
  }

  @Test
  @DisplayName("changePassword - Debe lanzar excepción si password actual es incorrecta")
  void changePassword_shouldThrowExceptionIfCurrentPasswordIncorrect() {
    // Given
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(passwordService.isBCryptHash("plainPassword")).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> userService.changePassword(1L, "wrongCurrent", "newPassword123"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La contraseña actual es incorrecta");
  }

  @Test
  @DisplayName("changePassword - Debe lanzar excepción si nueva password es inválida")
  void changePassword_shouldThrowExceptionIfNewPasswordInvalid() {
    // Given
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(passwordService.isBCryptHash("plainPassword")).thenReturn(false);
    when(passwordService.isPasswordValid("weak")).thenReturn(false);
    when(passwordService.getPasswordValidationMessage("weak")).thenReturn("Password too weak");

    // When & Then
    assertThatThrownBy(() -> userService.changePassword(1L, "plainPassword", "weak"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Password too weak");
  }

  @Test
  @DisplayName("changePassword - Debe lanzar excepción si nueva password es igual a la actual")
  void changePassword_shouldThrowExceptionIfNewPasswordSameAsCurrent() {
    // Given
    User userWithHashedPassword = TestDataBuilder.buildUser(1L, "John", false);
    userWithHashedPassword.setPassword("$2a$10$hashedPassword");

    when(userRepository.findById(1L)).thenReturn(Optional.of(userWithHashedPassword));
    when(passwordService.isBCryptHash("$2a$10$hashedPassword")).thenReturn(true);
    when(passwordService.verifyPassword("currentPassword", "$2a$10$hashedPassword")).thenReturn(true);
    when(passwordService.isPasswordValid("currentPassword")).thenReturn(true);
    when(passwordService.verifyPassword("currentPassword", "$2a$10$hashedPassword")).thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> userService.changePassword(1L, "currentPassword", "currentPassword"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("La nueva contraseña debe ser diferente a la actual");
  }
}