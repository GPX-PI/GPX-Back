package com.udea.GPX.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ParticipantDTO Tests")
class ParticipantDTOTest {

    private Validator validator;
    private ParticipantDTO participantDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        participantDTO = new ParticipantDTO();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should create object with null values")
        void testDefaultConstructor() {
            ParticipantDTO dto = new ParticipantDTO();

            assertNull(dto.getEventVehicleId());
            assertNull(dto.getUserId());
            assertNull(dto.getUserName());
            assertNull(dto.getUserPicture());
            assertNull(dto.getTeamName());
            assertNull(dto.getVehicleId());
            assertNull(dto.getVehicleName());
            assertNull(dto.getVehiclePlates());
            assertNull(dto.getVehicleSoat());
            assertNull(dto.getCategoryId());
            assertNull(dto.getCategoryName());
            assertNull(dto.getRegistrationDate());
        }

        @Test
        @DisplayName("Parameterized constructor should set all values correctly")
        void testParameterizedConstructor() {
            Long eventVehicleId = 1L;
            Long userId = 2L;
            String userName = "Juan Pérez";
            String userPicture = "https://example.com/pic.jpg";
            String teamName = "Team Alpha";
            Long vehicleId = 3L;
            String vehicleName = "Toyota Hilux";
            String vehiclePlates = "ABC123";
            String vehicleSoat = "SOAT123456789";
            Long categoryId = 4L;
            String categoryName = "Categoria Pro";
            LocalDateTime registrationDate = LocalDateTime.now();

            ParticipantDTO dto = new ParticipantDTO(eventVehicleId, userId, userName, userPicture, teamName,
                    vehicleId, vehicleName, vehiclePlates, vehicleSoat, categoryId, categoryName, registrationDate);

            assertEquals(eventVehicleId, dto.getEventVehicleId());
            assertEquals(userId, dto.getUserId());
            assertEquals(userName, dto.getUserName());
            assertEquals(userPicture, dto.getUserPicture());
            assertEquals(teamName, dto.getTeamName());
            assertEquals(vehicleId, dto.getVehicleId());
            assertEquals(vehicleName, dto.getVehicleName());
            assertEquals(vehiclePlates, dto.getVehiclePlates());
            assertEquals(vehicleSoat, dto.getVehicleSoat());
            assertEquals(categoryId, dto.getCategoryId());
            assertEquals(categoryName, dto.getCategoryName());
            assertEquals(registrationDate, dto.getRegistrationDate());
        }

        @Test
        @DisplayName("Constructor with minimum required values should work")
        void testConstructorWithMinimumValues() {
            ParticipantDTO dto = new ParticipantDTO(1L, 2L, "User", null, null,
                    3L, "Vehicle", "ABC123", null, 4L, "Category", null);

            assertEquals(1L, dto.getEventVehicleId());
            assertEquals(2L, dto.getUserId());
            assertEquals("User", dto.getUserName());
            assertNull(dto.getUserPicture());
            assertNull(dto.getTeamName());
            assertEquals(3L, dto.getVehicleId());
            assertEquals("Vehicle", dto.getVehicleName());
            assertEquals("ABC123", dto.getVehiclePlates());
            assertNull(dto.getVehicleSoat());
            assertEquals(4L, dto.getCategoryId());
            assertEquals("Category", dto.getCategoryName());
            assertNull(dto.getRegistrationDate());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("EventVehicleId getter and setter should work correctly")
        void testEventVehicleIdGetterSetter() {
            Long eventVehicleId = 100L;
            participantDTO.setEventVehicleId(eventVehicleId);
            assertEquals(eventVehicleId, participantDTO.getEventVehicleId());
        }

        @Test
        @DisplayName("UserId getter and setter should work correctly")
        void testUserIdGetterSetter() {
            Long userId = 200L;
            participantDTO.setUserId(userId);
            assertEquals(userId, participantDTO.getUserId());
        }

        @Test
        @DisplayName("UserName getter and setter should work correctly")
        void testUserNameGetterSetter() {
            String userName = "María González";
            participantDTO.setUserName(userName);
            assertEquals(userName, participantDTO.getUserName());
        }

        @Test
        @DisplayName("UserPicture getter and setter should work correctly")
        void testUserPictureGetterSetter() {
            String userPicture = "https://example.com/user.jpg";
            participantDTO.setUserPicture(userPicture);
            assertEquals(userPicture, participantDTO.getUserPicture());
        }

        @Test
        @DisplayName("TeamName getter and setter should work correctly")
        void testTeamNameGetterSetter() {
            String teamName = "Racing Team";
            participantDTO.setTeamName(teamName);
            assertEquals(teamName, participantDTO.getTeamName());
        }

        @Test
        @DisplayName("VehicleId getter and setter should work correctly")
        void testVehicleIdGetterSetter() {
            Long vehicleId = 300L;
            participantDTO.setVehicleId(vehicleId);
            assertEquals(vehicleId, participantDTO.getVehicleId());
        }

        @Test
        @DisplayName("VehicleName getter and setter should work correctly")
        void testVehicleNameGetterSetter() {
            String vehicleName = "Ford Ranger";
            participantDTO.setVehicleName(vehicleName);
            assertEquals(vehicleName, participantDTO.getVehicleName());
        }

        @Test
        @DisplayName("VehiclePlates getter and setter should work correctly")
        void testVehiclePlatesGetterSetter() {
            String vehiclePlates = "XYZ789";
            participantDTO.setVehiclePlates(vehiclePlates);
            assertEquals(vehiclePlates, participantDTO.getVehiclePlates());
        }

        @Test
        @DisplayName("VehicleSoat getter and setter should work correctly")
        void testVehicleSoatGetterSetter() {
            String vehicleSoat = "SOAT987654321";
            participantDTO.setVehicleSoat(vehicleSoat);
            assertEquals(vehicleSoat, participantDTO.getVehicleSoat());
        }

        @Test
        @DisplayName("CategoryId getter and setter should work correctly")
        void testCategoryIdGetterSetter() {
            Long categoryId = 400L;
            participantDTO.setCategoryId(categoryId);
            assertEquals(categoryId, participantDTO.getCategoryId());
        }

        @Test
        @DisplayName("CategoryName getter and setter should work correctly")
        void testCategoryNameGetterSetter() {
            String categoryName = "Amateur";
            participantDTO.setCategoryName(categoryName);
            assertEquals(categoryName, participantDTO.getCategoryName());
        }

        @Test
        @DisplayName("RegistrationDate getter and setter should work correctly")
        void testRegistrationDateGetterSetter() {
            LocalDateTime registrationDate = LocalDateTime.of(2024, 1, 15, 10, 30);
            participantDTO.setRegistrationDate(registrationDate);
            assertEquals(registrationDate, participantDTO.getRegistrationDate());
        }

        @Test
        @DisplayName("Setting null values should work correctly")
        void testSettingNullValues() {
            participantDTO.setEventVehicleId(null);
            participantDTO.setUserId(null);
            participantDTO.setUserName(null);
            participantDTO.setUserPicture(null);
            participantDTO.setTeamName(null);
            participantDTO.setVehicleId(null);
            participantDTO.setVehicleName(null);
            participantDTO.setVehiclePlates(null);
            participantDTO.setVehicleSoat(null);
            participantDTO.setCategoryId(null);
            participantDTO.setCategoryName(null);
            participantDTO.setRegistrationDate(null);

            assertNull(participantDTO.getEventVehicleId());
            assertNull(participantDTO.getUserId());
            assertNull(participantDTO.getUserName());
            assertNull(participantDTO.getUserPicture());
            assertNull(participantDTO.getTeamName());
            assertNull(participantDTO.getVehicleId());
            assertNull(participantDTO.getVehicleName());
            assertNull(participantDTO.getVehiclePlates());
            assertNull(participantDTO.getVehicleSoat());
            assertNull(participantDTO.getCategoryId());
            assertNull(participantDTO.getCategoryName());
            assertNull(participantDTO.getRegistrationDate());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid DTO should pass validation")
        void testValidDTO() {
            participantDTO.setEventVehicleId(1L);
            participantDTO.setUserId(2L);
            participantDTO.setUserName("Juan Pérez");
            participantDTO.setUserPicture("https://example.com/pic.jpg");
            participantDTO.setTeamName("Team Alpha");
            participantDTO.setVehicleId(3L);
            participantDTO.setVehicleName("Toyota Hilux");
            participantDTO.setVehiclePlates("ABC123");
            participantDTO.setVehicleSoat("SOAT123456789");
            participantDTO.setCategoryId(4L);
            participantDTO.setCategoryName("Categoria Pro");
            participantDTO.setRegistrationDate(LocalDateTime.now());

            Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
            assertTrue(violations.isEmpty());
        }

        @Nested
        @DisplayName("EventVehicleId Validation")
        class EventVehicleIdValidation {

            @Test
            @DisplayName("Null eventVehicleId should fail validation")
            void testNullEventVehicleId() {
                setValidParticipantData();
                participantDTO.setEventVehicleId(null);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID del evento-vehículo es obligatorio")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive eventVehicleId should fail validation")
            void testNonPositiveEventVehicleId(Long eventVehicleId) {
                setValidParticipantData();
                participantDTO.setEventVehicleId(eventVehicleId);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID del evento-vehículo debe ser positivo")));
            }

            @ParameterizedTest
            @ValueSource(longs = { 1L, 100L, 1000L, Long.MAX_VALUE })
            @DisplayName("Positive eventVehicleId should pass validation")
            void testPositiveEventVehicleId(Long eventVehicleId) {
                setValidParticipantData();
                participantDTO.setEventVehicleId(eventVehicleId);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("UserId Validation")
        class UserIdValidation {

            @Test
            @DisplayName("Null userId should fail validation")
            void testNullUserId() {
                setValidParticipantData();
                participantDTO.setUserId(null);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(
                        violations.stream().anyMatch(v -> v.getMessage().equals("El ID del usuario es obligatorio")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive userId should fail validation")
            void testNonPositiveUserId(Long userId) {
                setValidParticipantData();
                participantDTO.setUserId(userId);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID del usuario debe ser positivo")));
            }

            @ParameterizedTest
            @ValueSource(longs = { 1L, 50L, 999L, Long.MAX_VALUE })
            @DisplayName("Positive userId should pass validation")
            void testPositiveUserId(Long userId) {
                setValidParticipantData();
                participantDTO.setUserId(userId);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("UserName Validation")
        class UserNameValidation {

            @Test
            @DisplayName("Null userName should fail validation")
            void testNullUserName() {
                setValidParticipantData();
                participantDTO.setUserName(null);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre del usuario es obligatorio")));
            }

            @Test
            @DisplayName("Empty userName should fail validation")
            void testEmptyUserName() {
                setValidParticipantData();
                participantDTO.setUserName("");

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre del usuario es obligatorio")));
            }

            @Test
            @DisplayName("Blank userName should fail validation")
            void testBlankUserName() {
                setValidParticipantData();
                participantDTO.setUserName("   ");

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre del usuario es obligatorio")));
            }

            @Test
            @DisplayName("UserName exceeding 100 characters should fail validation")
            void testUserNameTooLong() {
                setValidParticipantData();
                String longUserName = "A".repeat(101);
                participantDTO.setUserName(longUserName);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre del usuario no puede exceder 100 caracteres")));
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "Juan",
                    "María José",
                    "José Luis Rodríguez-Martín",
                    "Ana Paula",
                    "Francisco Javier"
            })
            @DisplayName("Valid userNames should pass validation")
            void testValidUserNames(String userName) {
                setValidParticipantData();
                participantDTO.setUserName(userName);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("VehicleId Validation")
        class VehicleIdValidation {

            @Test
            @DisplayName("Null vehicleId should fail validation")
            void testNullVehicleId() {
                setValidParticipantData();
                participantDTO.setVehicleId(null);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(
                        violations.stream().anyMatch(v -> v.getMessage().equals("El ID del vehículo es obligatorio")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive vehicleId should fail validation")
            void testNonPositiveVehicleId(Long vehicleId) {
                setValidParticipantData();
                participantDTO.setVehicleId(vehicleId);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID del vehículo debe ser positivo")));
            }
        }

        @Nested
        @DisplayName("VehicleName Validation")
        class VehicleNameValidation {

            @Test
            @DisplayName("Null vehicleName should fail validation")
            void testNullVehicleName() {
                setValidParticipantData();
                participantDTO.setVehicleName(null);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre del vehículo es obligatorio")));
            }

            @Test
            @DisplayName("Empty vehicleName should fail validation")
            void testEmptyVehicleName() {
                setValidParticipantData();
                participantDTO.setVehicleName("");

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre del vehículo es obligatorio")));
            }

            @Test
            @DisplayName("VehicleName exceeding 100 characters should fail validation")
            void testVehicleNameTooLong() {
                setValidParticipantData();
                String longVehicleName = "V".repeat(101);
                participantDTO.setVehicleName(longVehicleName);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(
                        v -> v.getMessage().equals("El nombre del vehículo no puede exceder 100 caracteres")));
            }
        }

        @Nested
        @DisplayName("VehiclePlates Validation")
        class VehiclePlatesValidation {

            @Test
            @DisplayName("Null vehiclePlates should fail validation")
            void testNullVehiclePlates() {
                setValidParticipantData();
                participantDTO.setVehiclePlates(null);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Las placas del vehículo son obligatorias")));
            }

            @Test
            @DisplayName("Empty vehiclePlates should fail validation")
            void testEmptyVehiclePlates() {
                setValidParticipantData();
                participantDTO.setVehiclePlates("");

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Las placas del vehículo son obligatorias")));
            }

            @Test
            @DisplayName("VehiclePlates exceeding 20 characters should fail validation")
            void testVehiclePlatesTooLong() {
                setValidParticipantData();
                String longPlates = "P".repeat(21);
                participantDTO.setVehiclePlates(longPlates);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Las placas no pueden exceder 20 caracteres")));
            }
        }

        @Nested
        @DisplayName("CategoryId Validation")
        class CategoryIdValidation {

            @Test
            @DisplayName("Null categoryId should fail validation")
            void testNullCategoryId() {
                setValidParticipantData();
                participantDTO.setCategoryId(null);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID de la categoría es obligatorio")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive categoryId should fail validation")
            void testNonPositiveCategoryId(Long categoryId) {
                setValidParticipantData();
                participantDTO.setCategoryId(categoryId);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID de la categoría debe ser positivo")));
            }
        }

        @Nested
        @DisplayName("CategoryName Validation")
        class CategoryNameValidation {

            @Test
            @DisplayName("Null categoryName should fail validation")
            void testNullCategoryName() {
                setValidParticipantData();
                participantDTO.setCategoryName(null);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre de la categoría es obligatorio")));
            }

            @Test
            @DisplayName("Empty categoryName should fail validation")
            void testEmptyCategoryName() {
                setValidParticipantData();
                participantDTO.setCategoryName("");

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre de la categoría es obligatorio")));
            }

            @Test
            @DisplayName("CategoryName exceeding 100 characters should fail validation")
            void testCategoryNameTooLong() {
                setValidParticipantData();
                String longCategoryName = "C".repeat(101);
                participantDTO.setCategoryName(longCategoryName);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(
                        v -> v.getMessage().equals("El nombre de la categoría no puede exceder 100 caracteres")));
            }
        }

        @Nested
        @DisplayName("Optional Fields Validation")
        class OptionalFieldsValidation {

            @Test
            @DisplayName("Null optional fields should pass validation")
            void testNullOptionalFields() {
                setValidParticipantData();
                participantDTO.setUserPicture(null);
                participantDTO.setTeamName(null);
                participantDTO.setVehicleSoat(null);
                participantDTO.setRegistrationDate(null);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertTrue(violations.isEmpty());
            }

            @Test
            @DisplayName("TeamName exceeding 100 characters should fail validation")
            void testTeamNameTooLong() {
                setValidParticipantData();
                String longTeamName = "T".repeat(101);
                participantDTO.setTeamName(longTeamName);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre del equipo no puede exceder 100 caracteres")));
            }

            @Test
            @DisplayName("VehicleSoat exceeding 50 characters should fail validation")
            void testVehicleSoatTooLong() {
                setValidParticipantData();
                String longSoat = "S".repeat(51);
                participantDTO.setVehicleSoat(longSoat);

                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El SOAT no puede exceder 50 caracteres")));
            }
        }

        private void setValidParticipantData() {
            participantDTO.setEventVehicleId(1L);
            participantDTO.setUserId(2L);
            participantDTO.setUserName("Juan Pérez");
            participantDTO.setUserPicture("https://example.com/pic.jpg");
            participantDTO.setTeamName("Team Alpha");
            participantDTO.setVehicleId(3L);
            participantDTO.setVehicleName("Toyota Hilux");
            participantDTO.setVehiclePlates("ABC123");
            participantDTO.setVehicleSoat("SOAT123456789");
            participantDTO.setCategoryId(4L);
            participantDTO.setCategoryName("Categoria Pro");
            participantDTO.setRegistrationDate(LocalDateTime.now());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("DTO with minimum valid values should pass validation")
        void testMinimumValidValues() {
            participantDTO.setEventVehicleId(1L);
            participantDTO.setUserId(1L);
            participantDTO.setUserName("A");
            participantDTO.setVehicleId(1L);
            participantDTO.setVehicleName("V");
            participantDTO.setVehiclePlates("P");
            participantDTO.setCategoryId(1L);
            participantDTO.setCategoryName("C");

            Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with maximum valid lengths should pass validation")
        void testMaximumValidLengths() {
            participantDTO.setEventVehicleId(Long.MAX_VALUE);
            participantDTO.setUserId(Long.MAX_VALUE);
            participantDTO.setUserName("U".repeat(100));
            participantDTO.setTeamName("T".repeat(100));
            participantDTO.setVehicleId(Long.MAX_VALUE);
            participantDTO.setVehicleName("V".repeat(100));
            participantDTO.setVehiclePlates("P".repeat(20));
            participantDTO.setVehicleSoat("S".repeat(50));
            participantDTO.setCategoryId(Long.MAX_VALUE);
            participantDTO.setCategoryName("C".repeat(100));

            Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Multiple valid DTOs should all pass validation")
        void testMultipleValidDTOs() {
            ParticipantDTO[] dtos = {
                    new ParticipantDTO(1L, 1L, "Juan Pérez", "https://example.com/pic1.jpg", "Team Alpha",
                            1L, "Toyota Hilux", "ABC123", "SOAT123456789", 1L, "Pro", LocalDateTime.now()),
                    new ParticipantDTO(2L, 2L, "María González", null, null,
                            2L, "Ford Ranger", "XYZ789", null, 2L, "Amateur", null),
                    new ParticipantDTO(3L, 3L, "Carlos Rodríguez", "https://example.com/pic3.jpg", "Racing Team",
                            3L, "Chevrolet Colorado", "DEF456", "SOAT987654321", 3L, "Expert", LocalDateTime.now())
            };

            for (ParticipantDTO dto : dtos) {
                Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty(),
                        "DTO with userName=" + dto.getUserName() + " should be valid");
            }
        }

        @Test
        @DisplayName("DTO with special characters should pass validation")
        void testSpecialCharacters() {
            participantDTO.setEventVehicleId(1L);
            participantDTO.setUserId(2L);
            participantDTO.setUserName("José María O'Connor-Smith");
            participantDTO.setTeamName("Team Ñandú");
            participantDTO.setVehicleId(3L);
            participantDTO.setVehicleName("Toyota Hilux 4x4");
            participantDTO.setVehiclePlates("ABC-123");
            participantDTO.setVehicleSoat("SOAT2024123456");
            participantDTO.setCategoryId(4L);
            participantDTO.setCategoryName("Categoría Élite");

            Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(participantDTO);
            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("DTO should represent common participant scenarios")
        void testCommonParticipantScenarios() {
            // Scenario 1: Individual participant
            ParticipantDTO individual = new ParticipantDTO();
            individual.setEventVehicleId(1L);
            individual.setUserId(100L);
            individual.setUserName("Ana Sánchez");
            individual.setUserPicture("https://example.com/ana.jpg");
            individual.setTeamName(null); // No team
            individual.setVehicleId(200L);
            individual.setVehicleName("Honda CRV");
            individual.setVehiclePlates("IND001");
            individual.setVehicleSoat("SOAT2024001");
            individual.setCategoryId(1L);
            individual.setCategoryName("Individual");
            individual.setRegistrationDate(LocalDateTime.of(2024, 1, 15, 9, 0));

            // Scenario 2: Team participant
            ParticipantDTO team = new ParticipantDTO();
            team.setEventVehicleId(2L);
            team.setUserId(101L);
            team.setUserName("Luis Fernando");
            team.setUserPicture("https://example.com/luis.jpg");
            team.setTeamName("Velocidad Extrema");
            team.setVehicleId(201L);
            team.setVehicleName("Yamaha YXZ1000R");
            team.setVehiclePlates("TEAM02");
            team.setVehicleSoat("SOAT2024002");
            team.setCategoryId(2L);
            team.setCategoryName("Team Pro");
            team.setRegistrationDate(LocalDateTime.of(2024, 1, 16, 14, 30));

            Set<ConstraintViolation<ParticipantDTO>> violations1 = validator.validate(individual);
            Set<ConstraintViolation<ParticipantDTO>> violations2 = validator.validate(team);

            assertTrue(violations1.isEmpty());
            assertTrue(violations2.isEmpty());
        }

        @Test
        @DisplayName("DTO should maintain data integrity after multiple operations")
        void testDataIntegrityAfterOperations() {
            ParticipantDTO dto = new ParticipantDTO(); // Initial assignment
            dto.setEventVehicleId(1L);
            dto.setUserId(100L);
            dto.setUserName("Initial User");
            dto.setVehicleId(200L);
            dto.setVehicleName("Initial Vehicle");
            dto.setVehiclePlates("INIT01");
            dto.setVehicleSoat("SOAT123456789");
            dto.setCategoryId(1L);
            dto.setCategoryName("Initial Category");

            assertEquals(1L, dto.getEventVehicleId());
            assertEquals(100L, dto.getUserId());
            assertEquals("Initial User", dto.getUserName()); // Update assignment
            dto.setEventVehicleId(2L);
            dto.setUserId(101L);
            dto.setUserName("Updated User");
            dto.setVehicleId(201L);
            dto.setVehicleName("Updated Vehicle");
            dto.setVehiclePlates("UPD02");
            dto.setVehicleSoat("SOAT987654321");
            dto.setCategoryId(2L);
            dto.setCategoryName("Updated Category");

            assertEquals(2L, dto.getEventVehicleId());
            assertEquals(101L, dto.getUserId());
            assertEquals("Updated User", dto.getUserName()); // Final validation
            Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(dto);

            // Debug: Print violations if any
            if (!violations.isEmpty()) {
                System.out.println("Found " + violations.size() + " violations:");
                for (ConstraintViolation<ParticipantDTO> violation : violations) {
                    System.out.println("Field: " + violation.getPropertyPath());
                    System.out.println("Message: " + violation.getMessage());
                    System.out.println("Invalid value: " + violation.getInvalidValue());
                    System.out.println("---");
                }
            }

            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO should handle realistic racing event registration")
        void testRealisticRacingRegistration() {
            LocalDateTime registrationTime = LocalDateTime.of(2024, 3, 15, 8, 30);

            ParticipantDTO racingParticipant = new ParticipantDTO(
                    15L, // Event-Vehicle ID
                    250L, // User ID
                    "Roberto Carlos Mendoza", // Full name
                    "https://storage.example.com/profiles/roberto_mendoza.jpg", // Profile picture
                    "Aventureros del Asfalto", // Team name
                    75L, // Vehicle ID
                    "Polaris RZR XP 1000", // Vehicle name
                    "ADV2024", // Custom plates
                    "SOAT20240315001", // SOAT number
                    3L, // Category ID
                    "UTV Profesional", // Category name
                    registrationTime // Registration timestamp
            );

            Set<ConstraintViolation<ParticipantDTO>> violations = validator.validate(racingParticipant);
            assertTrue(violations.isEmpty());

            // Verify all data is correctly set
            assertEquals(15L, racingParticipant.getEventVehicleId());
            assertEquals(250L, racingParticipant.getUserId());
            assertEquals("Roberto Carlos Mendoza", racingParticipant.getUserName());
            assertEquals("Aventureros del Asfalto", racingParticipant.getTeamName());
            assertEquals("Polaris RZR XP 1000", racingParticipant.getVehicleName());
            assertEquals("UTV Profesional", racingParticipant.getCategoryName());
            assertEquals(registrationTime, racingParticipant.getRegistrationDate());
        }
    }
}
