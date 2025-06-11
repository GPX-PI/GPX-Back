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

@DisplayName("VehicleStageTimeDTO Tests")
class VehicleStageTimeDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Parameterized constructor should set all values correctly")
        void testParameterizedConstructor() {
            Long vehicleId = 1L;
            Long stageId = 2L;
            Integer stageOrder = 3;
            Integer elapsedTimeSeconds = 1800;

            VehicleStageTimeDTO dto = new VehicleStageTimeDTO(vehicleId, stageId, stageOrder, elapsedTimeSeconds);

            assertEquals(vehicleId, dto.getVehicleId());
            assertEquals(stageId, dto.getStageId());
            assertEquals(stageOrder, dto.getStageOrder());
            assertEquals(elapsedTimeSeconds, dto.getElapsedTimeSeconds());
        }

        @Test
        @DisplayName("Constructor with minimum valid values should work correctly")
        void testConstructorWithMinimumValidValues() {
            VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 1L, 1, 0);

            assertEquals(1L, dto.getVehicleId());
            assertEquals(1L, dto.getStageId());
            assertEquals(1, dto.getStageOrder());
            assertEquals(0, dto.getElapsedTimeSeconds());
        }

        @Test
        @DisplayName("Constructor with large valid values should work correctly")
        void testConstructorWithLargeValidValues() {
            VehicleStageTimeDTO dto = new VehicleStageTimeDTO(
                    Long.MAX_VALUE,
                    Long.MAX_VALUE,
                    Integer.MAX_VALUE,
                    Integer.MAX_VALUE);

            assertEquals(Long.MAX_VALUE, dto.getVehicleId());
            assertEquals(Long.MAX_VALUE, dto.getStageId());
            assertEquals(Integer.MAX_VALUE, dto.getStageOrder());
            assertEquals(Integer.MAX_VALUE, dto.getElapsedTimeSeconds());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        private VehicleStageTimeDTO dto;

        @BeforeEach
        void setUp() {
            dto = new VehicleStageTimeDTO(1L, 2L, 3, 1800);
        }

        @Test
        @DisplayName("VehicleId getter and setter should work correctly")
        void testVehicleIdGetterSetter() {
            Long newVehicleId = 10L;
            dto.setVehicleId(newVehicleId);
            assertEquals(newVehicleId, dto.getVehicleId());
        }

        @Test
        @DisplayName("StageId getter and setter should work correctly")
        void testStageIdGetterSetter() {
            Long newStageId = 20L;
            dto.setStageId(newStageId);
            assertEquals(newStageId, dto.getStageId());
        }

        @Test
        @DisplayName("StageOrder getter and setter should work correctly")
        void testStageOrderGetterSetter() {
            Integer newStageOrder = 5;
            dto.setStageOrder(newStageOrder);
            assertEquals(newStageOrder, dto.getStageOrder());
        }

        @Test
        @DisplayName("ElapsedTimeSeconds getter and setter should work correctly")
        void testElapsedTimeSecondsGetterSetter() {
            Integer newElapsedTime = 3600;
            dto.setElapsedTimeSeconds(newElapsedTime);
            assertEquals(newElapsedTime, dto.getElapsedTimeSeconds());
        }

        @Test
        @DisplayName("Setting null values should work correctly")
        void testSettingNullValues() {
            dto.setVehicleId(null);
            dto.setStageId(null);
            dto.setStageOrder(null);
            dto.setElapsedTimeSeconds(null);

            assertNull(dto.getVehicleId());
            assertNull(dto.getStageId());
            assertNull(dto.getStageOrder());
            assertNull(dto.getElapsedTimeSeconds());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid DTO should pass validation")
        void testValidDTO() {
            VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 2L, 3, 1800);

            Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with minimum valid values should pass validation")
        void testDTOWithMinimumValidValues() {
            VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 1L, 1, 0);

            Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Nested
        @DisplayName("VehicleId Validation")
        class VehicleIdValidation {

            @Test
            @DisplayName("Null vehicleId should fail validation")
            void testNullVehicleId() {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(null, 2L, 3, 1800);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("ID del vehículo es requerido")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive vehicleId should fail validation")
            void testNonPositiveVehicleId(Long vehicleId) {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(vehicleId, 2L, 3, 1800);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertFalse(violations.isEmpty());
                assertTrue(
                        violations.stream().anyMatch(v -> v.getMessage().equals("ID del vehículo debe ser positivo")));
            }

            @ParameterizedTest
            @ValueSource(longs = { 1L, 100L, 1000L })
            @DisplayName("Positive vehicleId should pass validation")
            void testPositiveVehicleId(Long vehicleId) {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(vehicleId, 2L, 3, 1800);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("StageId Validation")
        class StageIdValidation {

            @Test
            @DisplayName("Null stageId should fail validation")
            void testNullStageId() {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, null, 3, 1800);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("ID de la etapa es requerido")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive stageId should fail validation")
            void testNonPositiveStageId(Long stageId) {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, stageId, 3, 1800);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertFalse(violations.isEmpty());
                assertTrue(
                        violations.stream().anyMatch(v -> v.getMessage().equals("ID de la etapa debe ser positivo")));
            }

            @ParameterizedTest
            @ValueSource(longs = { 1L, 50L, 500L })
            @DisplayName("Positive stageId should pass validation")
            void testPositiveStageId(Long stageId) {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, stageId, 3, 1800);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("StageOrder Validation")
        class StageOrderValidation {

            @Test
            @DisplayName("Null stageOrder should fail validation")
            void testNullStageOrder() {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 2L, null, 1800);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Orden de la etapa es requerido")));
            }

            @ParameterizedTest
            @ValueSource(ints = { -1, 0 })
            @DisplayName("Non-positive stageOrder should fail validation")
            void testNonPositiveStageOrder(Integer stageOrder) {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 2L, stageOrder, 1800);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Orden de la etapa debe ser positivo")));
            }

            @ParameterizedTest
            @ValueSource(ints = { 1, 5, 10, 100 })
            @DisplayName("Positive stageOrder should pass validation")
            void testPositiveStageOrder(Integer stageOrder) {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 2L, stageOrder, 1800);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("ElapsedTimeSeconds Validation")
        class ElapsedTimeSecondsValidation {

            @Test
            @DisplayName("Null elapsedTimeSeconds should fail validation")
            void testNullElapsedTimeSeconds() {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 2L, 3, null);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertFalse(violations.isEmpty());
                assertTrue(
                        violations.stream().anyMatch(v -> v.getMessage().equals("Tiempo transcurrido es requerido")));
            }

            @ParameterizedTest
            @ValueSource(ints = { -1, -100, -3600 })
            @DisplayName("Negative elapsedTimeSeconds should fail validation")
            void testNegativeElapsedTimeSeconds(Integer elapsedTime) {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 2L, 3, elapsedTime);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("Tiempo transcurrido debe ser mayor o igual a cero")));
            }

            @ParameterizedTest
            @ValueSource(ints = { 0, 1, 60, 3600, 7200 })
            @DisplayName("Zero or positive elapsedTimeSeconds should pass validation")
            void testZeroOrPositiveElapsedTimeSeconds(Integer elapsedTime) {
                VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 2L, 3, elapsedTime);

                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("DTO with all minimum valid values should pass validation")
        void testAllMinimumValidValues() {
            VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 1L, 1, 0);

            Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with all maximum valid values should pass validation")
        void testAllMaximumValidValues() {
            VehicleStageTimeDTO dto = new VehicleStageTimeDTO(
                    Long.MAX_VALUE,
                    Long.MAX_VALUE,
                    Integer.MAX_VALUE,
                    Integer.MAX_VALUE);

            Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO representing a quick stage completion should be valid")
        void testQuickStageCompletion() {
            // Stage completed in 1 minute
            VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 1L, 1, 60);

            Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO representing a long stage completion should be valid")
        void testLongStageCompletion() {
            // Stage completed in 24 hours (86400 seconds)
            VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 1L, 1, 86400);

            Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO representing instantaneous completion should be valid")
        void testInstantaneousCompletion() {
            // Stage completed instantly (0 seconds)
            VehicleStageTimeDTO dto = new VehicleStageTimeDTO(1L, 1L, 1, 0);

            Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Multiple DTOs for different stages should be valid")
        void testMultipleDTOsForDifferentStages() {
            VehicleStageTimeDTO stage1 = new VehicleStageTimeDTO(1L, 1L, 1, 1800);
            VehicleStageTimeDTO stage2 = new VehicleStageTimeDTO(1L, 2L, 2, 2100);
            VehicleStageTimeDTO stage3 = new VehicleStageTimeDTO(1L, 3L, 3, 1950);

            Set<ConstraintViolation<VehicleStageTimeDTO>> violations1 = validator.validate(stage1);
            Set<ConstraintViolation<VehicleStageTimeDTO>> violations2 = validator.validate(stage2);
            Set<ConstraintViolation<VehicleStageTimeDTO>> violations3 = validator.validate(stage3);

            assertTrue(violations1.isEmpty());
            assertTrue(violations2.isEmpty());
            assertTrue(violations3.isEmpty());

            // Verify each stage has correct order
            assertEquals(1, stage1.getStageOrder());
            assertEquals(2, stage2.getStageOrder());
            assertEquals(3, stage3.getStageOrder());
        }

        @Test
        @DisplayName("DTO should handle boundary time values correctly")
        void testBoundaryTimeValues() {
            // Test with exactly 1 hour (3600 seconds)
            VehicleStageTimeDTO oneHour = new VehicleStageTimeDTO(1L, 1L, 1, 3600);

            // Test with exactly 1 day minus 1 second (86399 seconds)
            VehicleStageTimeDTO almostOneDay = new VehicleStageTimeDTO(2L, 2L, 2, 86399);

            Set<ConstraintViolation<VehicleStageTimeDTO>> violations1 = validator.validate(oneHour);
            Set<ConstraintViolation<VehicleStageTimeDTO>> violations2 = validator.validate(almostOneDay);

            assertTrue(violations1.isEmpty());
            assertTrue(violations2.isEmpty());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("DTO should represent realistic racing stage times")
        void testRealisticRacingTimes() {
            // Typical rally stage times (15 minutes to 45 minutes)
            VehicleStageTimeDTO shortStage = new VehicleStageTimeDTO(1L, 1L, 1, 900); // 15 minutes
            VehicleStageTimeDTO mediumStage = new VehicleStageTimeDTO(1L, 2L, 2, 1800); // 30 minutes
            VehicleStageTimeDTO longStage = new VehicleStageTimeDTO(1L, 3L, 3, 2700); // 45 minutes

            Set<ConstraintViolation<VehicleStageTimeDTO>> violations1 = validator.validate(shortStage);
            Set<ConstraintViolation<VehicleStageTimeDTO>> violations2 = validator.validate(mediumStage);
            Set<ConstraintViolation<VehicleStageTimeDTO>> violations3 = validator.validate(longStage);

            assertTrue(violations1.isEmpty());
            assertTrue(violations2.isEmpty());
            assertTrue(violations3.isEmpty());
        }

        @Test
        @DisplayName("DTO should support progressive stage ordering")
        void testProgressiveStageOrdering() {
            VehicleStageTimeDTO[] stages = {
                    new VehicleStageTimeDTO(1L, 1L, 1, 1800),
                    new VehicleStageTimeDTO(1L, 2L, 2, 2100),
                    new VehicleStageTimeDTO(1L, 3L, 3, 1950),
                    new VehicleStageTimeDTO(1L, 4L, 4, 2300),
                    new VehicleStageTimeDTO(1L, 5L, 5, 1750)
            };

            for (VehicleStageTimeDTO stage : stages) {
                Set<ConstraintViolation<VehicleStageTimeDTO>> violations = validator.validate(stage);
                assertTrue(violations.isEmpty());
            }

            // Verify progressive ordering
            for (int i = 0; i < stages.length; i++) {
                assertEquals(i + 1, stages[i].getStageOrder());
                assertEquals(i + 1, stages[i].getStageId());
            }
        }
    }
}
