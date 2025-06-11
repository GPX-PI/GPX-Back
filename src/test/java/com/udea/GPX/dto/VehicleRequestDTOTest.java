package com.udea.gpx.dto;

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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VehicleRequestDTO Tests")
class VehicleRequestDTOTest {

    private Validator validator;
    private VehicleRequestDTO vehicleRequestDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        vehicleRequestDTO = new VehicleRequestDTO();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should create object with null values")
        void testDefaultConstructor() {
            VehicleRequestDTO dto = new VehicleRequestDTO();

            assertNull(dto.getName());
            assertNull(dto.getSoat());
            assertNull(dto.getPlates());
            assertNull(dto.getCategoryId());
        }

        @Test
        @DisplayName("Parameterized constructor should set all values correctly")
        void testParameterizedConstructor() {
            String name = "Toyota Hilux";
            String soat = "SOAT123456789";
            String plates = "ABC123";
            Long categoryId = 1L;

            VehicleRequestDTO dto = new VehicleRequestDTO(name, soat, plates, categoryId);

            assertEquals(name, dto.getName());
            assertEquals(soat, dto.getSoat());
            assertEquals(plates, dto.getPlates());
            assertEquals(categoryId, dto.getCategoryId());
        }

        @Test
        @DisplayName("Constructor with minimum valid values should work correctly")
        void testConstructorWithMinimumValidValues() {
            VehicleRequestDTO dto = new VehicleRequestDTO("A", "S", "P", 1L);

            assertEquals("A", dto.getName());
            assertEquals("S", dto.getSoat());
            assertEquals("P", dto.getPlates());
            assertEquals(1L, dto.getCategoryId());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Name getter and setter should work correctly")
        void testNameGetterSetter() {
            String name = "Ford Ranger";
            vehicleRequestDTO.setName(name);
            assertEquals(name, vehicleRequestDTO.getName());
        }

        @Test
        @DisplayName("Soat getter and setter should work correctly")
        void testSoatGetterSetter() {
            String soat = "SOAT987654321";
            vehicleRequestDTO.setSoat(soat);
            assertEquals(soat, vehicleRequestDTO.getSoat());
        }

        @Test
        @DisplayName("Plates getter and setter should work correctly")
        void testPlatesGetterSetter() {
            String plates = "XYZ789";
            vehicleRequestDTO.setPlates(plates);
            assertEquals(plates, vehicleRequestDTO.getPlates());
        }

        @Test
        @DisplayName("CategoryId getter and setter should work correctly")
        void testCategoryIdGetterSetter() {
            Long categoryId = 2L;
            vehicleRequestDTO.setCategoryId(categoryId);
            assertEquals(categoryId, vehicleRequestDTO.getCategoryId());
        }

        @Test
        @DisplayName("Setting null values should work correctly")
        void testSettingNullValues() {
            vehicleRequestDTO.setName(null);
            vehicleRequestDTO.setSoat(null);
            vehicleRequestDTO.setPlates(null);
            vehicleRequestDTO.setCategoryId(null);

            assertNull(vehicleRequestDTO.getName());
            assertNull(vehicleRequestDTO.getSoat());
            assertNull(vehicleRequestDTO.getPlates());
            assertNull(vehicleRequestDTO.getCategoryId());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid DTO should pass validation")
        void testValidDTO() {
            vehicleRequestDTO.setName("Toyota Hilux");
            vehicleRequestDTO.setSoat("SOAT123456789");
            vehicleRequestDTO.setPlates("ABC123");
            vehicleRequestDTO.setCategoryId(1L);

            Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Nested
        @DisplayName("Name Validation")
        class NameValidation {

            @Test
            @DisplayName("Null name should fail validation")
            void testNullName() {
                vehicleRequestDTO.setName(null);
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre del vehículo es obligatorio")));
            }

            @Test
            @DisplayName("Empty name should fail validation")
            void testEmptyName() {
                vehicleRequestDTO.setName("");
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre del vehículo es obligatorio")));
            }

            @Test
            @DisplayName("Blank name should fail validation")
            void testBlankName() {
                vehicleRequestDTO.setName("   ");
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre del vehículo es obligatorio")));
            }

            @Test
            @DisplayName("Name exceeding 100 characters should fail validation")
            void testNameTooLong() {
                String longName = "A".repeat(101);
                vehicleRequestDTO.setName(longName);
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(
                        v -> v.getMessage().equals("El nombre del vehículo no puede exceder 100 caracteres")));
            }

            @Test
            @DisplayName("Name with exactly 100 characters should pass validation")
            void testNameExactly100Characters() {
                String name100 = "A".repeat(100);
                vehicleRequestDTO.setName(name100);
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "Toyota Hilux",
                    "Ford Ranger",
                    "Chevrolet Colorado",
                    "A",
                    "Vehículo de Prueba",
                    "Toyota Land Cruiser 4x4"
            })
            @DisplayName("Valid vehicle names should pass validation")
            void testValidVehicleNames(String name) {
                vehicleRequestDTO.setName(name);
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("SOAT Validation")
        class SoatValidation {

            @Test
            @DisplayName("Null SOAT should fail validation")
            void testNullSoat() {
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat(null);
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El SOAT del vehículo es obligatorio")));
            }

            @Test
            @DisplayName("Empty SOAT should fail validation")
            void testEmptySoat() {
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat("");
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El SOAT del vehículo es obligatorio")));
            }

            @Test
            @DisplayName("SOAT exceeding 50 characters should fail validation")
            void testSoatTooLong() {
                String longSoat = "S".repeat(51);
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat(longSoat);
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El SOAT no puede exceder 50 caracteres")));
            }

            @Test
            @DisplayName("SOAT with exactly 50 characters should pass validation")
            void testSoatExactly50Characters() {
                String soat50 = "S".repeat(50);
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat(soat50);
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "SOAT123456789",
                    "S",
                    "SOAT-2024-001",
                    "123456789ABCDEF",
                    "SOAT_VEH_001"
            })
            @DisplayName("Valid SOAT values should pass validation")
            void testValidSoatValues(String soat) {
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat(soat);
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("Plates Validation")
        class PlatesValidation {

            @Test
            @DisplayName("Null plates should fail validation")
            void testNullPlates() {
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates(null);
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Las placas del vehículo son obligatorias")));
            }

            @Test
            @DisplayName("Empty plates should fail validation")
            void testEmptyPlates() {
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates("");
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Las placas del vehículo son obligatorias")));
            }

            @Test
            @DisplayName("Plates exceeding 20 characters should fail validation")
            void testPlatesTooLong() {
                String longPlates = "P".repeat(21);
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates(longPlates);
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Las placas no pueden exceder 20 caracteres")));
            }

            @Test
            @DisplayName("Plates with exactly 20 characters should pass validation")
            void testPlatesExactly20Characters() {
                String plates20 = "P".repeat(20);
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates(plates20);
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "ABC123",
                    "P",
                    "XYZ789",
                    "DEF456",
                    "ABC-123",
                    "TEST123"
            })
            @DisplayName("Valid plates should pass validation")
            void testValidPlates(String plates) {
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates(plates);
                vehicleRequestDTO.setCategoryId(1L);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("CategoryId Validation")
        class CategoryIdValidation {

            @Test
            @DisplayName("Null categoryId should fail validation")
            void testNullCategoryId() {
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(null);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("La categoría del vehículo es obligatoria")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive categoryId should fail validation")
            void testNonPositiveCategoryId(Long categoryId) {
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(categoryId);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID de la categoría debe ser un número positivo")));
            }

            @ParameterizedTest
            @ValueSource(longs = { 1L, 2L, 10L, 100L })
            @DisplayName("Positive categoryId should pass validation")
            void testPositiveCategoryId(Long categoryId) {
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(categoryId);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }

            @Test
            @DisplayName("Maximum long value categoryId should pass validation")
            void testMaximumCategoryId() {
                vehicleRequestDTO.setName("Toyota Hilux");
                vehicleRequestDTO.setSoat("SOAT123456789");
                vehicleRequestDTO.setPlates("ABC123");
                vehicleRequestDTO.setCategoryId(Long.MAX_VALUE);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("Combined Validation")
        class CombinedValidation {

            @Test
            @DisplayName("All fields null should produce four validation errors")
            void testAllFieldsNull() {
                vehicleRequestDTO.setName(null);
                vehicleRequestDTO.setSoat(null);
                vehicleRequestDTO.setPlates(null);
                vehicleRequestDTO.setCategoryId(null);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertEquals(4, violations.size());

                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre del vehículo es obligatorio")));
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El SOAT del vehículo es obligatorio")));
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Las placas del vehículo son obligatorias")));
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("La categoría del vehículo es obligatoria")));
            }

            @Test
            @DisplayName("All fields invalid should produce multiple validation errors")
            void testAllFieldsInvalid() {
                vehicleRequestDTO.setName("A".repeat(101)); // Too long
                vehicleRequestDTO.setSoat(""); // Empty
                vehicleRequestDTO.setPlates("P".repeat(21)); // Too long
                vehicleRequestDTO.setCategoryId(-1L); // Negative

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
                assertTrue(violations.size() >= 4);
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("DTO with minimum valid lengths should pass validation")
        void testMinimumValidLengths() {
            vehicleRequestDTO.setName("A");
            vehicleRequestDTO.setSoat("S");
            vehicleRequestDTO.setPlates("P");
            vehicleRequestDTO.setCategoryId(1L);

            Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with maximum valid lengths should pass validation")
        void testMaximumValidLengths() {
            String name100 = "A".repeat(100);
            String soat50 = "S".repeat(50);
            String plates20 = "P".repeat(20);

            vehicleRequestDTO.setName(name100);
            vehicleRequestDTO.setSoat(soat50);
            vehicleRequestDTO.setPlates(plates20);
            vehicleRequestDTO.setCategoryId(Long.MAX_VALUE);

            Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Multiple valid DTOs should all pass validation")
        void testMultipleValidDTOs() {
            VehicleRequestDTO[] dtos = {
                    new VehicleRequestDTO("Toyota Hilux", "SOAT123456789", "ABC123", 1L),
                    new VehicleRequestDTO("Ford Ranger", "SOAT987654321", "XYZ789", 2L),
                    new VehicleRequestDTO("Chevrolet Colorado", "SOAT555666777", "DEF456", 3L),
                    new VehicleRequestDTO("Nissan Frontier", "SOAT111222333", "GHI789", 4L)
            };

            for (VehicleRequestDTO dto : dtos) {
                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty(),
                        "DTO with name=" + dto.getName() + " should be valid");
            }
        }

        @Test
        @DisplayName("DTO with special characters should pass validation")
        void testSpecialCharacters() {
            vehicleRequestDTO.setName("Toyota Hilux 4x4");
            vehicleRequestDTO.setSoat("SOAT-2024-001");
            vehicleRequestDTO.setPlates("ABC-123");
            vehicleRequestDTO.setCategoryId(1L);

            Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicleRequestDTO);
            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("DTO should support common vehicle registration scenarios")
        void testVehicleRegistrationScenarios() {
            // Scenario 1: Pickup truck registration
            VehicleRequestDTO pickup = new VehicleRequestDTO("Toyota Hilux 4x4", "SOAT123456789", "ABC123", 1L);

            // Scenario 2: SUV registration
            VehicleRequestDTO suv = new VehicleRequestDTO("Ford Explorer", "SOAT987654321", "XYZ789", 2L);

            // Scenario 3: Motorcycle registration
            VehicleRequestDTO motorcycle = new VehicleRequestDTO("Honda CRF450", "SOAT555666777", "MTC001", 3L);

            Set<ConstraintViolation<VehicleRequestDTO>> violations1 = validator.validate(pickup);
            Set<ConstraintViolation<VehicleRequestDTO>> violations2 = validator.validate(suv);
            Set<ConstraintViolation<VehicleRequestDTO>> violations3 = validator.validate(motorcycle);

            assertTrue(violations1.isEmpty());
            assertTrue(violations2.isEmpty());
            assertTrue(violations3.isEmpty());
        }

        @Test
        @DisplayName("DTO should handle different plate formats")
        void testDifferentPlateFormats() {
            VehicleRequestDTO[] vehicles = {
                    new VehicleRequestDTO("Vehicle 1", "SOAT001", "ABC123", 1L), // Standard format
                    new VehicleRequestDTO("Vehicle 2", "SOAT002", "ABC-123", 1L), // With dash
                    new VehicleRequestDTO("Vehicle 3", "SOAT003", "AB123C", 1L), // Mixed format
                    new VehicleRequestDTO("Vehicle 4", "SOAT004", "123ABC", 1L), // Numbers first
                    new VehicleRequestDTO("Vehicle 5", "SOAT005", "A1B2C3", 1L) // Alternating
            };

            for (VehicleRequestDTO vehicle : vehicles) {
                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(vehicle);
                assertTrue(violations.isEmpty(),
                        "Vehicle with plates " + vehicle.getPlates() + " should be valid");
            }
        }

        @Test
        @DisplayName("DTO should maintain data integrity after multiple operations")
        void testDataIntegrityAfterOperations() {
            VehicleRequestDTO dto = new VehicleRequestDTO();

            // Initial assignment
            dto.setName("Toyota Hilux");
            dto.setSoat("SOAT123456789");
            dto.setPlates("ABC123");
            dto.setCategoryId(1L);

            assertEquals("Toyota Hilux", dto.getName());
            assertEquals("SOAT123456789", dto.getSoat());
            assertEquals("ABC123", dto.getPlates());
            assertEquals(1L, dto.getCategoryId());

            // Update assignment
            dto.setName("Ford Ranger");
            dto.setSoat("SOAT987654321");
            dto.setPlates("XYZ789");
            dto.setCategoryId(2L);

            assertEquals("Ford Ranger", dto.getName());
            assertEquals("SOAT987654321", dto.getSoat());
            assertEquals("XYZ789", dto.getPlates());
            assertEquals(2L, dto.getCategoryId());

            // Final validation
            Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO should support vehicle categories correctly")
        void testVehicleCategories() {
            // Test different category IDs representing different vehicle types
            Long[] categoryIds = { 1L, 2L, 3L, 4L, 5L }; // Different categories
            String[] vehicleTypes = {
                    "Toyota Hilux", // Category 1: Pickup
                    "Ford Explorer", // Category 2: SUV
                    "Honda CRF450", // Category 3: Motorcycle
                    "Chevrolet Spark", // Category 4: Small Car
                    "Mercedes Actros" // Category 5: Truck
            };

            for (int i = 0; i < categoryIds.length; i++) {
                VehicleRequestDTO dto = new VehicleRequestDTO(
                        vehicleTypes[i],
                        "SOAT" + (i + 1),
                        "PL" + (i + 1),
                        categoryIds[i]);

                Set<ConstraintViolation<VehicleRequestDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty(),
                        "Vehicle " + vehicleTypes[i] + " in category " + categoryIds[i] + " should be valid");
            }
        }
    }
}
