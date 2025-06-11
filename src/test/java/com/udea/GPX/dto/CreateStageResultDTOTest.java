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

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateStageResultDTO Tests")
class CreateStageResultDTOTest {

    private Validator validator;
    private CreateStageResultDTO createStageResultDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        createStageResultDTO = new CreateStageResultDTO();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should create object with null values")
        void testDefaultConstructor() {
            CreateStageResultDTO dto = new CreateStageResultDTO();

            assertNull(dto.getStageId());
            assertNull(dto.getVehicleId());
            assertNull(dto.getTimestamp());
            assertNull(dto.getLatitude());
            assertNull(dto.getLongitude());
        }

        @Test
        @DisplayName("Parameterized constructor should set all values correctly")
        void testParameterizedConstructor() {
            Long stageId = 1L;
            Long vehicleId = 2L;
            LocalDateTime timestamp = LocalDateTime.now();
            Double latitude = 45.0;
            Double longitude = -75.0;

            CreateStageResultDTO dto = new CreateStageResultDTO(stageId, vehicleId, timestamp, latitude, longitude);

            assertEquals(stageId, dto.getStageId());
            assertEquals(vehicleId, dto.getVehicleId());
            assertEquals(timestamp, dto.getTimestamp());
            assertEquals(latitude, dto.getLatitude());
            assertEquals(longitude, dto.getLongitude());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("StageId getter and setter should work correctly")
        void testStageIdGetterSetter() {
            Long stageId = 1L;
            createStageResultDTO.setStageId(stageId);
            assertEquals(stageId, createStageResultDTO.getStageId());
        }

        @Test
        @DisplayName("VehicleId getter and setter should work correctly")
        void testVehicleIdGetterSetter() {
            Long vehicleId = 2L;
            createStageResultDTO.setVehicleId(vehicleId);
            assertEquals(vehicleId, createStageResultDTO.getVehicleId());
        }

        @Test
        @DisplayName("Timestamp getter and setter should work correctly")
        void testTimestampGetterSetter() {
            LocalDateTime timestamp = LocalDateTime.now();
            createStageResultDTO.setTimestamp(timestamp);
            assertEquals(timestamp, createStageResultDTO.getTimestamp());
        }

        @Test
        @DisplayName("Latitude getter and setter should work correctly")
        void testLatitudeGetterSetter() {
            Double latitude = 45.0;
            createStageResultDTO.setLatitude(latitude);
            assertEquals(latitude, createStageResultDTO.getLatitude());
        }

        @Test
        @DisplayName("Longitude getter and setter should work correctly")
        void testLongitudeGetterSetter() {
            Double longitude = -75.0;
            createStageResultDTO.setLongitude(longitude);
            assertEquals(longitude, createStageResultDTO.getLongitude());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid DTO should pass validation")
        void testValidDTO() {
            createStageResultDTO.setStageId(1L);
            createStageResultDTO.setVehicleId(2L);
            createStageResultDTO.setTimestamp(LocalDateTime.now());
            createStageResultDTO.setLatitude(45.0);
            createStageResultDTO.setLongitude(-75.0);

            Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
            assertTrue(violations.isEmpty());
        }

        @Nested
        @DisplayName("StageId Validation")
        class StageIdValidation {

            @Test
            @DisplayName("Null stageId should fail validation")
            void testNullStageId() {
                createStageResultDTO.setStageId(null);
                createStageResultDTO.setVehicleId(2L);
                createStageResultDTO.setTimestamp(LocalDateTime.now());

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(
                        violations.stream().anyMatch(v -> v.getMessage().equals("El ID de la etapa es obligatorio")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive stageId should fail validation")
            void testNonPositiveStageId(Long stageId) {
                createStageResultDTO.setStageId(stageId);
                createStageResultDTO.setVehicleId(2L);
                createStageResultDTO.setTimestamp(LocalDateTime.now());

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID de la etapa debe ser un número positivo")));
            }

            @Test
            @DisplayName("Positive stageId should pass validation")
            void testPositiveStageId() {
                createStageResultDTO.setStageId(1L);
                createStageResultDTO.setVehicleId(2L);
                createStageResultDTO.setTimestamp(LocalDateTime.now());
                createStageResultDTO.setLatitude(0.0);
                createStageResultDTO.setLongitude(0.0);

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("VehicleId Validation")
        class VehicleIdValidation {

            @Test
            @DisplayName("Null vehicleId should fail validation")
            void testNullVehicleId() {
                createStageResultDTO.setStageId(1L);
                createStageResultDTO.setVehicleId(null);
                createStageResultDTO.setTimestamp(LocalDateTime.now());

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(
                        violations.stream().anyMatch(v -> v.getMessage().equals("El ID del vehículo es obligatorio")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive vehicleId should fail validation")
            void testNonPositiveVehicleId(Long vehicleId) {
                createStageResultDTO.setStageId(1L);
                createStageResultDTO.setVehicleId(vehicleId);
                createStageResultDTO.setTimestamp(LocalDateTime.now());

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID del vehículo debe ser un número positivo")));
            }
        }

        @Nested
        @DisplayName("Timestamp Validation")
        class TimestampValidation {

            @Test
            @DisplayName("Null timestamp should fail validation")
            void testNullTimestamp() {
                createStageResultDTO.setStageId(1L);
                createStageResultDTO.setVehicleId(2L);
                createStageResultDTO.setTimestamp(null);

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("La fecha y hora es obligatoria")));
            }

            @Test
            @DisplayName("Valid timestamp should pass validation")
            void testValidTimestamp() {
                createStageResultDTO.setStageId(1L);
                createStageResultDTO.setVehicleId(2L);
                createStageResultDTO.setTimestamp(LocalDateTime.now());
                createStageResultDTO.setLatitude(0.0);
                createStageResultDTO.setLongitude(0.0);

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("Latitude Validation")
        class LatitudeValidation {

            @ParameterizedTest
            @ValueSource(doubles = { -90.0, -45.0, 0.0, 45.0, 90.0 })
            @DisplayName("Valid latitude values should pass validation")
            void testValidLatitude(Double latitude) {
                createStageResultDTO.setStageId(1L);
                createStageResultDTO.setVehicleId(2L);
                createStageResultDTO.setTimestamp(LocalDateTime.now());
                createStageResultDTO.setLatitude(latitude);
                createStageResultDTO.setLongitude(0.0);

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertTrue(violations.isEmpty());
            }

            @ParameterizedTest
            @ValueSource(doubles = { -91.0, -100.0, 91.0, 100.0 })
            @DisplayName("Invalid latitude values should fail validation")
            void testInvalidLatitude(Double latitude) {
                createStageResultDTO.setStageId(1L);
                createStageResultDTO.setVehicleId(2L);
                createStageResultDTO.setTimestamp(LocalDateTime.now());
                createStageResultDTO.setLatitude(latitude);

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().contains("La latitud debe estar entre -90 y 90 grados")));
            }

            @Test
            @DisplayName("Null latitude should pass validation (optional field)")
            void testNullLatitude() {
                createStageResultDTO.setStageId(1L);
                createStageResultDTO.setVehicleId(2L);
                createStageResultDTO.setTimestamp(LocalDateTime.now());
                createStageResultDTO.setLatitude(null);
                createStageResultDTO.setLongitude(0.0);

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("Longitude Validation")
        class LongitudeValidation {

            @ParameterizedTest
            @ValueSource(doubles = { -180.0, -90.0, 0.0, 90.0, 180.0 })
            @DisplayName("Valid longitude values should pass validation")
            void testValidLongitude(Double longitude) {
                createStageResultDTO.setStageId(1L);
                createStageResultDTO.setVehicleId(2L);
                createStageResultDTO.setTimestamp(LocalDateTime.now());
                createStageResultDTO.setLatitude(0.0);
                createStageResultDTO.setLongitude(longitude);

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertTrue(violations.isEmpty());
            }

            @ParameterizedTest
            @ValueSource(doubles = { -181.0, -200.0, 181.0, 200.0 })
            @DisplayName("Invalid longitude values should fail validation")
            void testInvalidLongitude(Double longitude) {
                createStageResultDTO.setStageId(1L);
                createStageResultDTO.setVehicleId(2L);
                createStageResultDTO.setTimestamp(LocalDateTime.now());
                createStageResultDTO.setLongitude(longitude);

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().contains("La longitud debe estar entre -180 y 180 grados")));
            }

            @Test
            @DisplayName("Null longitude should pass validation (optional field)")
            void testNullLongitude() {
                createStageResultDTO.setStageId(1L);
                createStageResultDTO.setVehicleId(2L);
                createStageResultDTO.setTimestamp(LocalDateTime.now());
                createStageResultDTO.setLatitude(0.0);
                createStageResultDTO.setLongitude(null);

                Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
                assertTrue(violations.isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("DTO with all null optional fields should pass validation")
        void testDTOWithNullOptionalFields() {
            createStageResultDTO.setStageId(1L);
            createStageResultDTO.setVehicleId(2L);
            createStageResultDTO.setTimestamp(LocalDateTime.now());
            createStageResultDTO.setLatitude(null);
            createStageResultDTO.setLongitude(null);

            Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with boundary coordinate values should pass validation")
        void testBoundaryCoordinateValues() {
            createStageResultDTO.setStageId(1L);
            createStageResultDTO.setVehicleId(2L);
            createStageResultDTO.setTimestamp(LocalDateTime.now());
            createStageResultDTO.setLatitude(90.0); // Maximum latitude
            createStageResultDTO.setLongitude(-180.0); // Minimum longitude

            Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with minimum valid positive IDs should pass validation")
        void testMinimumValidPositiveIds() {
            createStageResultDTO.setStageId(1L);
            createStageResultDTO.setVehicleId(1L);
            createStageResultDTO.setTimestamp(LocalDateTime.now());
            createStageResultDTO.setLatitude(0.0);
            createStageResultDTO.setLongitude(0.0);

            Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with large valid ID values should pass validation")
        void testLargeValidIds() {
            createStageResultDTO.setStageId(Long.MAX_VALUE);
            createStageResultDTO.setVehicleId(Long.MAX_VALUE);
            createStageResultDTO.setTimestamp(LocalDateTime.now());
            createStageResultDTO.setLatitude(0.0);
            createStageResultDTO.setLongitude(0.0);

            Set<ConstraintViolation<CreateStageResultDTO>> violations = validator.validate(createStageResultDTO);
            assertTrue(violations.isEmpty());
        }
    }
}
