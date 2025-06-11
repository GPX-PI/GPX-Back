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

@DisplayName("EventVehicleRequestDTO Tests")
class EventVehicleRequestDTOTest {

    private Validator validator;
    private EventVehicleRequestDTO eventVehicleRequestDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        eventVehicleRequestDTO = new EventVehicleRequestDTO();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should create object with null values")
        void testDefaultConstructor() {
            EventVehicleRequestDTO dto = new EventVehicleRequestDTO();

            assertNull(dto.getEventId());
            assertNull(dto.getVehicleId());
        }

        @Test
        @DisplayName("Parameterized constructor should set all values correctly")
        void testParameterizedConstructor() {
            Long eventId = 1L;
            Long vehicleId = 2L;

            EventVehicleRequestDTO dto = new EventVehicleRequestDTO(eventId, vehicleId);

            assertEquals(eventId, dto.getEventId());
            assertEquals(vehicleId, dto.getVehicleId());
        }

        @Test
        @DisplayName("Constructor with minimum valid values should work correctly")
        void testConstructorWithMinimumValidValues() {
            EventVehicleRequestDTO dto = new EventVehicleRequestDTO(1L, 1L);

            assertEquals(1L, dto.getEventId());
            assertEquals(1L, dto.getVehicleId());
        }

        @Test
        @DisplayName("Constructor with large valid values should work correctly")
        void testConstructorWithLargeValidValues() {
            EventVehicleRequestDTO dto = new EventVehicleRequestDTO(Long.MAX_VALUE, Long.MAX_VALUE);

            assertEquals(Long.MAX_VALUE, dto.getEventId());
            assertEquals(Long.MAX_VALUE, dto.getVehicleId());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("EventId getter and setter should work correctly")
        void testEventIdGetterSetter() {
            Long eventId = 1L;
            eventVehicleRequestDTO.setEventId(eventId);
            assertEquals(eventId, eventVehicleRequestDTO.getEventId());
        }

        @Test
        @DisplayName("VehicleId getter and setter should work correctly")
        void testVehicleIdGetterSetter() {
            Long vehicleId = 2L;
            eventVehicleRequestDTO.setVehicleId(vehicleId);
            assertEquals(vehicleId, eventVehicleRequestDTO.getVehicleId());
        }

        @Test
        @DisplayName("Setting null values should work correctly")
        void testSettingNullValues() {
            eventVehicleRequestDTO.setEventId(null);
            eventVehicleRequestDTO.setVehicleId(null);

            assertNull(eventVehicleRequestDTO.getEventId());
            assertNull(eventVehicleRequestDTO.getVehicleId());
        }

        @Test
        @DisplayName("Setting and getting same values should work correctly")
        void testSettingAndGettingSameValues() {
            Long eventId = 100L;
            Long vehicleId = 200L;

            eventVehicleRequestDTO.setEventId(eventId);
            eventVehicleRequestDTO.setVehicleId(vehicleId);

            assertEquals(eventId, eventVehicleRequestDTO.getEventId());
            assertEquals(vehicleId, eventVehicleRequestDTO.getVehicleId());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid DTO should pass validation")
        void testValidDTO() {
            eventVehicleRequestDTO.setEventId(1L);
            eventVehicleRequestDTO.setVehicleId(2L);

            Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator.validate(eventVehicleRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with minimum valid values should pass validation")
        void testDTOWithMinimumValidValues() {
            eventVehicleRequestDTO.setEventId(1L);
            eventVehicleRequestDTO.setVehicleId(1L);

            Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator.validate(eventVehicleRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Nested
        @DisplayName("EventId Validation")
        class EventIdValidation {

            @Test
            @DisplayName("Null eventId should fail validation")
            void testNullEventId() {
                eventVehicleRequestDTO.setEventId(null);
                eventVehicleRequestDTO.setVehicleId(2L);

                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator
                        .validate(eventVehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El ID del evento es obligatorio")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive eventId should fail validation")
            void testNonPositiveEventId(Long eventId) {
                eventVehicleRequestDTO.setEventId(eventId);
                eventVehicleRequestDTO.setVehicleId(2L);

                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator
                        .validate(eventVehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID del evento debe ser un número positivo")));
            }

            @ParameterizedTest
            @ValueSource(longs = { 1L, 10L, 100L, 1000L })
            @DisplayName("Positive eventId should pass validation")
            void testPositiveEventId(Long eventId) {
                eventVehicleRequestDTO.setEventId(eventId);
                eventVehicleRequestDTO.setVehicleId(2L);

                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator
                        .validate(eventVehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }

            @Test
            @DisplayName("Maximum long value eventId should pass validation")
            void testMaximumEventId() {
                eventVehicleRequestDTO.setEventId(Long.MAX_VALUE);
                eventVehicleRequestDTO.setVehicleId(2L);

                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator
                        .validate(eventVehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("VehicleId Validation")
        class VehicleIdValidation {

            @Test
            @DisplayName("Null vehicleId should fail validation")
            void testNullVehicleId() {
                eventVehicleRequestDTO.setEventId(1L);
                eventVehicleRequestDTO.setVehicleId(null);

                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator
                        .validate(eventVehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(
                        violations.stream().anyMatch(v -> v.getMessage().equals("El ID del vehículo es obligatorio")));
            }

            @ParameterizedTest
            @ValueSource(longs = { -1L, 0L })
            @DisplayName("Non-positive vehicleId should fail validation")
            void testNonPositiveVehicleId(Long vehicleId) {
                eventVehicleRequestDTO.setEventId(1L);
                eventVehicleRequestDTO.setVehicleId(vehicleId);

                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator
                        .validate(eventVehicleRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID del vehículo debe ser un número positivo")));
            }

            @ParameterizedTest
            @ValueSource(longs = { 1L, 5L, 50L, 500L })
            @DisplayName("Positive vehicleId should pass validation")
            void testPositiveVehicleId(Long vehicleId) {
                eventVehicleRequestDTO.setEventId(1L);
                eventVehicleRequestDTO.setVehicleId(vehicleId);

                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator
                        .validate(eventVehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }

            @Test
            @DisplayName("Maximum long value vehicleId should pass validation")
            void testMaximumVehicleId() {
                eventVehicleRequestDTO.setEventId(1L);
                eventVehicleRequestDTO.setVehicleId(Long.MAX_VALUE);

                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator
                        .validate(eventVehicleRequestDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("Combined Validation")
        class CombinedValidation {

            @Test
            @DisplayName("Both IDs null should produce two validation errors")
            void testBothIdsNull() {
                eventVehicleRequestDTO.setEventId(null);
                eventVehicleRequestDTO.setVehicleId(null);

                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator
                        .validate(eventVehicleRequestDTO);
                assertEquals(2, violations.size());

                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El ID del evento es obligatorio")));
                assertTrue(
                        violations.stream().anyMatch(v -> v.getMessage().equals("El ID del vehículo es obligatorio")));
            }

            @Test
            @DisplayName("Both IDs non-positive should produce two validation errors")
            void testBothIdsNonPositive() {
                eventVehicleRequestDTO.setEventId(0L);
                eventVehicleRequestDTO.setVehicleId(-1L);

                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator
                        .validate(eventVehicleRequestDTO);
                assertEquals(2, violations.size());

                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID del evento debe ser un número positivo")));
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El ID del vehículo debe ser un número positivo")));
            }

            @Test
            @DisplayName("Same valid ID for both event and vehicle should pass validation")
            void testSameValidIdForBoth() {
                Long sameId = 100L;
                eventVehicleRequestDTO.setEventId(sameId);
                eventVehicleRequestDTO.setVehicleId(sameId);

                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator
                        .validate(eventVehicleRequestDTO);
                assertTrue(violations.isEmpty());

                assertEquals(sameId, eventVehicleRequestDTO.getEventId());
                assertEquals(sameId, eventVehicleRequestDTO.getVehicleId());
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("DTO with minimum valid positive IDs should pass validation")
        void testMinimumValidPositiveIds() {
            eventVehicleRequestDTO.setEventId(1L);
            eventVehicleRequestDTO.setVehicleId(1L);

            Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator.validate(eventVehicleRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with maximum valid IDs should pass validation")
        void testMaximumValidIds() {
            eventVehicleRequestDTO.setEventId(Long.MAX_VALUE);
            eventVehicleRequestDTO.setVehicleId(Long.MAX_VALUE);

            Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator.validate(eventVehicleRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Multiple valid DTOs should all pass validation")
        void testMultipleValidDTOs() {
            EventVehicleRequestDTO[] dtos = {
                    new EventVehicleRequestDTO(1L, 1L),
                    new EventVehicleRequestDTO(1L, 2L),
                    new EventVehicleRequestDTO(2L, 1L),
                    new EventVehicleRequestDTO(100L, 200L),
                    new EventVehicleRequestDTO(Long.MAX_VALUE, Long.MAX_VALUE)
            };

            for (EventVehicleRequestDTO dto : dtos) {
                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty(),
                        "DTO with eventId=" + dto.getEventId() + " and vehicleId=" + dto.getVehicleId()
                                + " should be valid");
            }
        }

        @Test
        @DisplayName("DTO should handle realistic event-vehicle associations")
        void testRealisticEventVehicleAssociations() {
            // Simulate registering multiple vehicles for the same event
            Long eventId = 1L;
            Long[] vehicleIds = { 1L, 2L, 3L, 4L, 5L };

            for (Long vehicleId : vehicleIds) {
                EventVehicleRequestDTO dto = new EventVehicleRequestDTO(eventId, vehicleId);
                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty());
                assertEquals(eventId, dto.getEventId());
                assertEquals(vehicleId, dto.getVehicleId());
            }
        }

        @Test
        @DisplayName("DTO should handle registering same vehicle for multiple events")
        void testSameVehicleMultipleEvents() {
            // Simulate registering the same vehicle for multiple events
            Long vehicleId = 1L;
            Long[] eventIds = { 1L, 2L, 3L, 4L, 5L };

            for (Long eventId : eventIds) {
                EventVehicleRequestDTO dto = new EventVehicleRequestDTO(eventId, vehicleId);
                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty());
                assertEquals(eventId, dto.getEventId());
                assertEquals(vehicleId, dto.getVehicleId());
            }
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("DTO should support event registration scenarios")
        void testEventRegistrationScenarios() {
            // Scenario 1: Register vehicle for championship event
            EventVehicleRequestDTO championshipRegistration = new EventVehicleRequestDTO(1L, 101L);

            // Scenario 2: Register vehicle for practice event
            EventVehicleRequestDTO practiceRegistration = new EventVehicleRequestDTO(2L, 101L);

            // Scenario 3: Register different vehicle for same championship
            EventVehicleRequestDTO anotherVehicle = new EventVehicleRequestDTO(1L, 102L);

            Set<ConstraintViolation<EventVehicleRequestDTO>> violations1 = validator.validate(championshipRegistration);
            Set<ConstraintViolation<EventVehicleRequestDTO>> violations2 = validator.validate(practiceRegistration);
            Set<ConstraintViolation<EventVehicleRequestDTO>> violations3 = validator.validate(anotherVehicle);

            assertTrue(violations1.isEmpty());
            assertTrue(violations2.isEmpty());
            assertTrue(violations3.isEmpty());

            // Verify registrations are properly set
            assertEquals(1L, championshipRegistration.getEventId());
            assertEquals(101L, championshipRegistration.getVehicleId());

            assertEquals(2L, practiceRegistration.getEventId());
            assertEquals(101L, practiceRegistration.getVehicleId());

            assertEquals(1L, anotherVehicle.getEventId());
            assertEquals(102L, anotherVehicle.getVehicleId());
        }

        @Test
        @DisplayName("DTO should handle sequential ID assignments")
        void testSequentialIdAssignments() {
            // Test sequential event and vehicle IDs
            for (long i = 1; i <= 10; i++) {
                EventVehicleRequestDTO dto = new EventVehicleRequestDTO(i, i * 10);
                Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty());
                assertEquals(i, dto.getEventId());
                assertEquals(i * 10, dto.getVehicleId());
            }
        }

        @Test
        @DisplayName("DTO should maintain data integrity after multiple operations")
        void testDataIntegrityAfterOperations() {
            EventVehicleRequestDTO dto = new EventVehicleRequestDTO();

            // Initial assignment
            dto.setEventId(1L);
            dto.setVehicleId(100L);
            assertEquals(1L, dto.getEventId());
            assertEquals(100L, dto.getVehicleId());

            // Update assignment
            dto.setEventId(2L);
            dto.setVehicleId(200L);
            assertEquals(2L, dto.getEventId());
            assertEquals(200L, dto.getVehicleId());

            // Final validation
            Set<ConstraintViolation<EventVehicleRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }
    }
}
