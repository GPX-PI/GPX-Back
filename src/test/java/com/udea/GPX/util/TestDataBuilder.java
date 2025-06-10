package com.udea.GPX.util;

import com.udea.GPX.model.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder centralizado para crear objetos de test y eliminar duplicación de
 * código
 */
public class TestDataBuilder {

  // =================== USER BUILDERS ===================

  /**
   * Crea un usuario básico para tests
   */
  public static User buildUser(Long id, boolean admin) {
    return buildUser(id, "TestUser", admin);
  }

  /**
   * Crea un usuario con nombre personalizado
   */
  public static User buildUser(Long id, String firstName, boolean admin) {
    User user = new User();
    user.setId(id);
    user.setFirstName(firstName);
    user.setLastName("TestLastName");
    user.setEmail(firstName.toLowerCase() + "@test.com");
    user.setIdentification("12345678" + id);
    user.setPhone("3001234567");
    user.setRole("USER");
    user.setBirthdate(LocalDate.of(1990, 1, 1));
    user.setTypeOfId("CC");
    user.setTeamName("Team Test");
    user.setEps("Test EPS");
    user.setRh("O+");
    user.setEmergencyPhone("3007654321");
    user.setAlergies("Ninguna");
    user.setPassword("hashedPassword");
    user.setAdmin(admin);
    user.setAuthProvider("LOCAL");
    user.setPicture("test-picture.jpg");
    user.setInsurance("test-insurance.pdf");
    user.setWikiloc("https://wikiloc.com/test");
    user.setTerrapirata("https://terrapirata.com/test");
    user.setInstagram("@test_user");
    user.setFacebook("https://facebook.com/test");
    return user;
  }

  /**
   * Crea un usuario administrador por defecto
   */
  public static User buildAdminUser() {
    return buildUser(1L, "Admin", true);
  }

  /**
   * Crea un usuario normal por defecto
   */
  public static User buildNormalUser() {
    return buildUser(2L, "User", false);
  }

  // =================== VEHICLE BUILDERS ===================

  /**
   * Crea un vehículo básico para tests
   */
  public static Vehicle buildVehicle(Long id, User owner, Category category) {
    Vehicle vehicle = new Vehicle();
    vehicle.setId(id);
    vehicle.setUser(owner);
    vehicle.setCategory(category);
    vehicle.setName("Test Vehicle " + id);
    vehicle.setSoat("SOAT-" + id);
    vehicle.setPlates("ABC-" + String.format("%03d", id));
    return vehicle;
  }

  /**
   * Crea un vehículo con propietario específico
   */
  public static Vehicle buildVehicle(Long id, User owner) {
    Category category = buildCategory(1L, "Test Category");
    return buildVehicle(id, owner, category);
  }

  // =================== CATEGORY BUILDERS ===================

  /**
   * Crea una categoría básica para tests
   */
  public static Category buildCategory(Long id, String name) {
    Category category = new Category();
    category.setId(id);
    category.setName(name);
    category.setDetails("Test category details");
    return category;
  }

  // =================== EVENT BUILDERS ===================

  /**
   * Crea un evento básico para tests
   */
  public static Event buildEvent(Long id, String name) {
    Event event = new Event();
    event.setId(id);
    event.setName(name);
    event.setLocation("Test Location");
    event.setDetails("Test event details");
    event.setStartDate(LocalDate.now().plusDays(1));
    event.setEndDate(LocalDate.now().plusDays(3));
    event.setPicture("test-event.jpg");
    return event;
  }

  /**
   * Crea un evento con fechas específicas
   */
  public static Event buildEvent(Long id, String name, LocalDate startDate, LocalDate endDate) {
    Event event = buildEvent(id, name);
    event.setStartDate(startDate);
    event.setEndDate(endDate);
    return event;
  }

  // =================== STAGE BUILDERS ===================

  /**
   * Crea una etapa básica para tests
   */
  public static Stage buildStage(Long id, String name, Event event, Integer orderNumber) {
    Stage stage = new Stage();
    stage.setId(id);
    stage.setName(name);
    stage.setEvent(event);
    stage.setOrderNumber(orderNumber);
    stage.setNeutralized(false);
    return stage;
  }

  // =================== STAGE RESULT BUILDERS ===================

  /**
   * Crea un resultado de etapa básico para tests
   */
  public static StageResult buildStageResult(Long id, Vehicle vehicle, Stage stage) {
    StageResult result = new StageResult();
    result.setId(id);
    result.setVehicle(vehicle);
    result.setStage(stage);
    result.setTimestamp(LocalDateTime.now());
    result.setLatitude(6.244203);
    result.setLongitude(-75.581211);
    result.setElapsedTimeSeconds(3600); // 1 hora en segundos
    return result;
  }

  /**
   * Crea un resultado de etapa con tiempo específico
   */
  public static StageResult buildStageResult(Long id, Vehicle vehicle, Stage stage, Integer elapsedTimeSeconds) {
    StageResult result = buildStageResult(id, vehicle, stage);
    result.setElapsedTimeSeconds(elapsedTimeSeconds);
    return result;
  }

  // =================== EVENT CATEGORY BUILDERS ===================

  /**
   * Crea una relación evento-categoría para tests
   */
  public static EventCategory buildEventCategory(Long id, Event event, Category category) {
    EventCategory eventCategory = new EventCategory();
    eventCategory.setId(id);
    eventCategory.setEvent(event);
    eventCategory.setCategory(category);
    return eventCategory;
  }

  // =================== LISTS BUILDERS ===================

  /**
   * Crea una lista de usuarios para tests
   */
  public static List<User> buildUserList() {
    List<User> users = new ArrayList<>();
    users.add(buildUser(1L, "Juan", false));
    users.add(buildUser(2L, "María", true));
    users.add(buildUser(3L, "Pedro", false));
    return users;
  }

  /**
   * Crea una lista de vehículos para tests
   */
  public static List<Vehicle> buildVehicleList(User owner) {
    List<Vehicle> vehicles = new ArrayList<>();
    Category category = buildCategory(1L, "Motos");
    vehicles.add(buildVehicle(1L, owner, category));
    vehicles.add(buildVehicle(2L, owner, category));
    return vehicles;
  }

  // =================== SECURITY CONTEXT BUILDERS ===================

  /**
   * Configura el contexto de seguridad con un usuario específico
   */
  public static void setupSecurityContext(User user) {
    Authentication authentication = Mockito.mock(Authentication.class);
    Mockito.when(authentication.getPrincipal()).thenReturn(user);
    Mockito.when(authentication.isAuthenticated()).thenReturn(true);

    SecurityContext securityContext = Mockito.mock(SecurityContext.class);
    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  /**
   * Configura el contexto de seguridad con un usuario administrador
   */
  public static void setupAdminSecurityContext() {
    setupSecurityContext(buildAdminUser());
  }

  /**
   * Configura el contexto de seguridad con un usuario normal
   */
  public static void setupNormalUserSecurityContext() {
    setupSecurityContext(buildNormalUser());
  }

  /**
   * Limpia el contexto de seguridad
   */
  public static void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }
}