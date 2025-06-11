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

@DisplayName("UpdateStageResultDTO Tests")
class UpdateStageResultDTOTest {

    private Validator validator;
    private UpdateStageResultDTO updateStageResultDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        updateStageResultDTO = new UpdateStageResultDTO();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should create object with null values")
        void testDefaultConstructor() {
            UpdateStageResultDTO dto = new UpdateStageResultDTO();

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

            UpdateStageResultDTO dto = new UpdateStageResultDTO(stageId, vehicleId, timestamp, latitude, longitude);

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
            updateStageResultDTO.setStageId(stageId);
            assertEquals(stageId, updateStageResultDTO.getStageId());
        }

        @Test
        @DisplayName("VehicleId getter and setter should work correctly")
        void testVehicleIdGetterSetter() {
            Long vehicleId = 2L;
            updateStageResultDTO.setVehicleId(vehicleId);
            assertEquals(vehicleId, updateStageResultDTO.getVehicleId());
        }

        @Test
        @DisplayName("Timestamp getter and setter should work correctly")
        void testTimestampGetterSetter() {
            LocalDateTime timestamp = LocalDateTime.now();
            updateStageResultDTO.setTimestamp(timestamp);
            assertEquals(timestamp, updateStageResultDTO.getTimestamp());
        }

        @Test
        @DisplayName("Latitude getter and setter should work correctly")
        void testLatitudeGetterSetter() {
            Double latitude = 45.0;
            updateStageResultDTO.setLatitude(latitude);
            assertEquals(latitude, updateStageResultDTO.getLatitude());
        }

        @Test
        @DisplayName("Longitude getter and setter should work correctly")
        void testLongitudeGetterSetter() {
            Double longitude = -75.0;
            updateStageResultDTO.setLongitude(longitude);
            assertEquals(longitude, updateStageResultDTO.getLongitude());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid DTO should pass validation")
        void testValidDTO() {
            updateStageResultDTO.setStageId(1L);
            updateStageResultDTO.setVehicleId(2L);
            updateStageResultDTO.setTimestamp(LocalDateTime.now());
            updateStageResultDTO.setLatitude(45.0);
            updateStageResultDTO.setLongitude(-75.0);

            Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
            assertTrue(violations.isEmpty());
        }

        @Nested
        @DisplayName("StageId Validation")
        class StageIdValidation {

            @Test
            @DisplayName("Null stageId should fail validation")
            void testNullStageId() {
                updateStageResultDTO.setStageId(null);
                updateStageResultDTO.setVehicleId(2L);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(
                        violations.stream().anyMatch(v -> v.getMessage().equals("El ID de la etapa es obligatorio")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive stageId should fail validation")
            void testNonPositiveStageId(Long stageId) {
                updateStageResultDTO.setStageId(stageId);
                updateStageResultDTO.setVehicleId(2L);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID de la etapa debe ser un número positivo")));
            }

            @Test
            @DisplayName("Positive stageId should pass validation")
            void testPositiveStageId() {
                updateStageResultDTO.setStageId(1L);
                updateStageResultDTO.setVehicleId(2L);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());
                updateStageResultDTO.setLatitude(0.0);
                updateStageResultDTO.setLongitude(0.0);

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("VehicleId Validation")
        class VehicleIdValidation {

            @Test
            @DisplayName("Null vehicleId should fail validation")
            void testNullVehicleId() {
                updateStageResultDTO.setStageId(1L);
                updateStageResultDTO.setVehicleId(null);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(
                        violations.stream().anyMatch(v -> v.getMessage().equals("El ID del vehículo es obligatorio")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive vehicleId should fail validation")
            void testNonPositiveVehicleId(Long vehicleId) {
                updateStageResultDTO.setStageId(1L);
                updateStageResultDTO.setVehicleId(vehicleId);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
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
                updateStageResultDTO.setStageId(1L);
                updateStageResultDTO.setVehicleId(2L);
                updateStageResultDTO.setTimestamp(null);

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("La fecha y hora es obligatoria")));
            }

            @Test
            @DisplayName("Valid timestamp should pass validation")
            void testValidTimestamp() {
                updateStageResultDTO.setStageId(1L);
                updateStageResultDTO.setVehicleId(2L);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());
                updateStageResultDTO.setLatitude(0.0);
                updateStageResultDTO.setLongitude(0.0);

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
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
                updateStageResultDTO.setStageId(1L);
                updateStageResultDTO.setVehicleId(2L);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());
                updateStageResultDTO.setLatitude(latitude);
                updateStageResultDTO.setLongitude(0.0);

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
                assertTrue(violations.isEmpty());
            }

            @ParameterizedTest
            @ValueSource(doubles = { -91.0, -100.0, 91.0, 100.0 })
            @DisplayName("Invalid latitude values should fail validation")
            void testInvalidLatitude(Double latitude) {
                updateStageResultDTO.setStageId(1L);
                updateStageResultDTO.setVehicleId(2L);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());
                updateStageResultDTO.setLatitude(latitude);

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().contains("La latitud debe estar entre -90 y 90 grados")));
            }

            @Test
            @DisplayName("Null latitude should pass validation (optional field)")
            void testNullLatitude() {
                updateStageResultDTO.setStageId(1L);
                updateStageResultDTO.setVehicleId(2L);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());
                updateStageResultDTO.setLatitude(null);
                updateStageResultDTO.setLongitude(0.0);

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
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
                updateStageResultDTO.setStageId(1L);
                updateStageResultDTO.setVehicleId(2L);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());
                updateStageResultDTO.setLatitude(0.0);
                updateStageResultDTO.setLongitude(longitude);

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
                assertTrue(violations.isEmpty());
            }

            @ParameterizedTest
            @ValueSource(doubles = { -181.0, -200.0, 181.0, 200.0 })
            @DisplayName("Invalid longitude values should fail validation")
            void testInvalidLongitude(Double longitude) {
                updateStageResultDTO.setStageId(1L);
                updateStageResultDTO.setVehicleId(2L);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());
                updateStageResultDTO.setLongitude(longitude);

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().contains("La longitud debe estar entre -180 y 180 grados")));
            }

            @Test
            @DisplayName("Null longitude should pass validation (optional field)")
            void testNullLongitude() {
                updateStageResultDTO.setStageId(1L);
                updateStageResultDTO.setVehicleId(2L);
                updateStageResultDTO.setTimestamp(LocalDateTime.now());
                updateStageResultDTO.setLatitude(0.0);
                updateStageResultDTO.setLongitude(null);

                Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
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
            updateStageResultDTO.setStageId(1L);
            updateStageResultDTO.setVehicleId(2L);
            updateStageResultDTO.setTimestamp(LocalDateTime.now());
            updateStageResultDTO.setLatitude(null);
            updateStageResultDTO.setLongitude(null);

            Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with boundary coordinate values should pass validation")
        void testBoundaryCoordinateValues() {
            updateStageResultDTO.setStageId(1L);
            updateStageResultDTO.setVehicleId(2L);
            updateStageResultDTO.setTimestamp(LocalDateTime.now());
            updateStageResultDTO.setLatitude(-90.0); // Minimum latitude
            updateStageResultDTO.setLongitude(180.0); // Maximum longitude

            Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with minimum valid positive IDs should pass validation")
        void testMinimumValidPositiveIds() {
            updateStageResultDTO.setStageId(1L);
            updateStageResultDTO.setVehicleId(1L);
            updateStageResultDTO.setTimestamp(LocalDateTime.now());
            updateStageResultDTO.setLatitude(0.0);
            updateStageResultDTO.setLongitude(0.0);

            Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with large valid ID values should pass validation")
        void testLargeValidIds() {
            updateStageResultDTO.setStageId(Long.MAX_VALUE);
            updateStageResultDTO.setVehicleId(Long.MAX_VALUE);
            updateStageResultDTO.setTimestamp(LocalDateTime.now());
            updateStageResultDTO.setLatitude(0.0);
            updateStageResultDTO.setLongitude(0.0);

            Set<ConstraintViolation<UpdateStageResultDTO>> violations = validator.validate(updateStageResultDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Comparison with CreateStageResultDTO should show similar validation behavior")
        void testComparisonWithCreateStageResultDTO() {
            // This test ensures both DTOs behave consistently
            CreateStageResultDTO createDto = new CreateStageResultDTO(1L, 2L, LocalDateTime.now(), 45.0, -75.0);
            UpdateStageResultDTO updateDto = new UpdateStageResultDTO(1L, 2L, createDto.getTimestamp(), 45.0, -75.0);

            Set<ConstraintViolation<CreateStageResultDTO>> createViolations = validator.validate(createDto);
            Set<ConstraintViolation<UpdateStageResultDTO>> updateViolations = validator.validate(updateDto);

            // Both should pass validation
            assertTrue(createViolations.isEmpty());
            assertTrue(updateViolations.isEmpty());

            // Both should have same field values
            assertEquals(createDto.getStageId(), updateDto.getStageId());
            assertEquals(createDto.getVehicleId(), updateDto.getVehicleId());
            assertEquals(createDto.getTimestamp(), updateDto.getTimestamp());
            assertEquals(createDto.getLatitude(), updateDto.getLatitude());
            assertEquals(createDto.getLongitude(), updateDto.getLongitude());
        }
    }
}
