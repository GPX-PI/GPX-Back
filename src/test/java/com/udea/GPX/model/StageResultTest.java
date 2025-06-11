package com.udea.gpx.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para StageResult Model - Entidad JPA para resultados de etapas
 */
@DisplayName("StageResult Model Tests")
class StageResultTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create stage result with default constructor")
        void shouldCreateStageResultWithDefaultConstructor() {
            // When
            StageResult stageResult = new StageResult();

            // Then
            assertNotNull(stageResult);
            assertNull(stageResult.getId());
            assertNull(stageResult.getTimestamp());
            assertEquals(0.0, stageResult.getLatitude()); // primitive double default
            assertEquals(0.0, stageResult.getLongitude()); // primitive double default
            assertNull(stageResult.getPenaltyWaypoint());
            assertNull(stageResult.getPenaltySpeed());
            assertNull(stageResult.getDiscountClaim());
            assertNull(stageResult.getElapsedTimeSeconds());
            assertNull(stageResult.getStage());
            assertNull(stageResult.getVehicle());
        }

        @Test
        @DisplayName("Should create stage result with full constructor")
        void shouldCreateStageResultWithFullConstructor() {
            // Given
            Long expectedId = 1L;
            LocalDateTime expectedTimestamp = LocalDateTime.now();
            double expectedLatitude = 6.2442;
            double expectedLongitude = -75.5812;
            Duration expectedPenaltyWaypoint = Duration.ofSeconds(30);
            Duration expectedPenaltySpeed = Duration.ofSeconds(60);
            Duration expectedDiscountClaim = Duration.ofSeconds(15);
            Integer expectedElapsedTimeSeconds = 3600;

            Event event = new Event(1L, "Rally Test", "Test Location", "Details",
                    LocalDate.now(), LocalDate.now().plusDays(2));
            Stage expectedStage = new Stage(1L, "Etapa 1", 1, false, event);

            Category category = new Category(1L, "Rally", "Rally Category");
            User user = new User();
            user.setId(1L);
            Vehicle expectedVehicle = new Vehicle(1L, "Toyota Hilux", "SOAT-001", "ABC123", category, user);

            // When
            StageResult stageResult = new StageResult(expectedId, expectedTimestamp, expectedLatitude,
                    expectedLongitude, expectedPenaltyWaypoint, expectedPenaltySpeed,
                    expectedDiscountClaim, expectedElapsedTimeSeconds,
                    expectedStage, expectedVehicle);

            // Then
            assertNotNull(stageResult);
            assertEquals(expectedId, stageResult.getId());
            assertEquals(expectedTimestamp, stageResult.getTimestamp());
            assertEquals(expectedLatitude, stageResult.getLatitude());
            assertEquals(expectedLongitude, stageResult.getLongitude());
            assertEquals(expectedPenaltyWaypoint, stageResult.getPenaltyWaypoint());
            assertEquals(expectedPenaltySpeed, stageResult.getPenaltySpeed());
            assertEquals(expectedDiscountClaim, stageResult.getDiscountClaim());
            assertEquals(expectedElapsedTimeSeconds, stageResult.getElapsedTimeSeconds());
            assertEquals(expectedStage, stageResult.getStage());
            assertEquals(expectedVehicle, stageResult.getVehicle());
        }

        @Test
        @DisplayName("Should handle null values in full constructor")
        void shouldHandleNullValuesInFullConstructor() {
            // When
            StageResult stageResult = new StageResult(null, null, 0.0, 0.0, null, null, null, null, null, null);

            // Then
            assertNotNull(stageResult);
            assertNull(stageResult.getId());
            assertNull(stageResult.getTimestamp());
            assertEquals(0.0, stageResult.getLatitude());
            assertEquals(0.0, stageResult.getLongitude());
            assertNull(stageResult.getPenaltyWaypoint());
            assertNull(stageResult.getPenaltySpeed());
            assertNull(stageResult.getDiscountClaim());
            assertNull(stageResult.getElapsedTimeSeconds());
            assertNull(stageResult.getStage());
            assertNull(stageResult.getVehicle());
        }
    }

    // ========== GETTER AND SETTER TESTS ==========

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id correctly")
        void shouldGetAndSetIdCorrectly() {
            // Given
            StageResult stageResult = new StageResult();
            Long expectedId = 100L;

            // When
            stageResult.setId(expectedId);

            // Then
            assertEquals(expectedId, stageResult.getId());
        }

        @Test
        @DisplayName("Should get and set timestamp correctly")
        void shouldGetAndSetTimestampCorrectly() {
            // Given
            StageResult stageResult = new StageResult();
            LocalDateTime expectedTimestamp = LocalDateTime.of(2024, 6, 15, 14, 30, 45);

            // When
            stageResult.setTimestamp(expectedTimestamp);

            // Then
            assertEquals(expectedTimestamp, stageResult.getTimestamp());
        }

        @Test
        @DisplayName("Should get and set coordinates correctly")
        void shouldGetAndSetCoordinatesCorrectly() {
            // Given
            StageResult stageResult = new StageResult();
            double expectedLatitude = 6.2442; // Medellín latitude
            double expectedLongitude = -75.5812; // Medellín longitude

            // When
            stageResult.setLatitude(expectedLatitude);
            stageResult.setLongitude(expectedLongitude);

            // Then
            assertEquals(expectedLatitude, stageResult.getLatitude());
            assertEquals(expectedLongitude, stageResult.getLongitude());
        }

        @Test
        @DisplayName("Should get and set penalties correctly")
        void shouldGetAndSetPenaltiesCorrectly() {
            // Given
            StageResult stageResult = new StageResult();
            Duration expectedWaypointPenalty = Duration.ofSeconds(120);
            Duration expectedSpeedPenalty = Duration.ofSeconds(180);
            Duration expectedDiscount = Duration.ofSeconds(30);

            // When
            stageResult.setPenaltyWaypoint(expectedWaypointPenalty);
            stageResult.setPenaltySpeed(expectedSpeedPenalty);
            stageResult.setDiscountClaim(expectedDiscount);

            // Then
            assertEquals(expectedWaypointPenalty, stageResult.getPenaltyWaypoint());
            assertEquals(expectedSpeedPenalty, stageResult.getPenaltySpeed());
            assertEquals(expectedDiscount, stageResult.getDiscountClaim());
        }

        @Test
        @DisplayName("Should get and set elapsed time correctly")
        void shouldGetAndSetElapsedTimeCorrectly() {
            // Given
            StageResult stageResult = new StageResult();
            Integer expectedElapsedTime = 4275; // 1 hour 11 minutes 15 seconds

            // When
            stageResult.setElapsedTimeSeconds(expectedElapsedTime);

            // Then
            assertEquals(expectedElapsedTime, stageResult.getElapsedTimeSeconds());
        }

        @Test
        @DisplayName("Should get and set stage correctly")
        void shouldGetAndSetStageCorrectly() {
            // Given
            StageResult stageResult = new StageResult();
            Event event = new Event(1L, "Rally Test", "Location", "Details",
                    LocalDate.now(), LocalDate.now().plusDays(1));
            Stage expectedStage = new Stage(5L, "Etapa Especial", 3, false, event);

            // When
            stageResult.setStage(expectedStage);

            // Then
            assertEquals(expectedStage, stageResult.getStage());
            assertEquals(5L, stageResult.getStage().getId());
            assertEquals("Etapa Especial", stageResult.getStage().getName());
            assertEquals(3, stageResult.getStage().getOrderNumber());
        }

        @Test
        @DisplayName("Should get and set vehicle correctly")
        void shouldGetAndSetVehicleCorrectly() {
            // Given
            StageResult stageResult = new StageResult();
            Category category = new Category(2L, "Enduro", "Enduro Category");
            User user = new User();
            user.setId(10L);
            user.setFirstName("Carlos");

            Vehicle expectedVehicle = new Vehicle(2L, "Honda CRF450R", "SOAT-002", "END002", category, user);

            // When
            stageResult.setVehicle(expectedVehicle);

            // Then
            assertEquals(expectedVehicle, stageResult.getVehicle());
            assertEquals(2L, stageResult.getVehicle().getId());
            assertEquals("Honda CRF450R", stageResult.getVehicle().getName());
            assertEquals("Carlos", stageResult.getVehicle().getUser().getFirstName());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            // Given
            LocalDateTime timestamp = LocalDateTime.now();
            Stage stage = new Stage(1L, "Test Stage", 1, false, new Event());
            Vehicle vehicle = new Vehicle(1L, "Test Vehicle", "SOAT-001", "TEST001",
                    new Category(), new User());

            StageResult stageResult = new StageResult(1L, timestamp, 6.0, -75.0,
                    Duration.ofSeconds(30), Duration.ofSeconds(60),
                    Duration.ofSeconds(15), 3600, stage, vehicle);

            // When
            stageResult.setId(null);
            stageResult.setTimestamp(null);
            stageResult.setLatitude(0.0);
            stageResult.setLongitude(0.0);
            stageResult.setPenaltyWaypoint(null);
            stageResult.setPenaltySpeed(null);
            stageResult.setDiscountClaim(null);
            stageResult.setElapsedTimeSeconds(null);
            stageResult.setStage(null);
            stageResult.setVehicle(null);

            // Then
            assertNull(stageResult.getId());
            assertNull(stageResult.getTimestamp());
            assertEquals(0.0, stageResult.getLatitude());
            assertEquals(0.0, stageResult.getLongitude());
            assertNull(stageResult.getPenaltyWaypoint());
            assertNull(stageResult.getPenaltySpeed());
            assertNull(stageResult.getDiscountClaim());
            assertNull(stageResult.getElapsedTimeSeconds());
            assertNull(stageResult.getStage());
            assertNull(stageResult.getVehicle());
        }
    }

    // ========== BUSINESS LOGIC TESTS ==========

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should support valid GPS coordinates")
        void shouldSupportValidGpsCoordinates() {
            // Given - Colombian coordinates
            Object[][] coordinates = {
                    { 6.2442, -75.5812 }, // Medellín
                    { 4.6097, -74.0817 }, // Bogotá
                    { 10.9639, -74.7963 }, // Cartagena
                    { 3.4516, -76.5320 }, // Cali
                    { 7.1198, -73.1227 } // Bucaramanga
            };

            for (Object[] coord : coordinates) {
                // When
                StageResult stageResult = new StageResult();
                double latitude = (double) coord[0];
                double longitude = (double) coord[1];

                stageResult.setLatitude(latitude);
                stageResult.setLongitude(longitude);

                // Then
                assertEquals(latitude, stageResult.getLatitude());
                assertEquals(longitude, stageResult.getLongitude());
                assertTrue(latitude >= -90 && latitude <= 90, "Latitude should be valid");
                assertTrue(longitude >= -180 && longitude <= 180, "Longitude should be valid");
            }
        }

        @Test
        @DisplayName("Should support penalty time calculations")
        void shouldSupportPenaltyTimeCalculations() {
            // Given
            StageResult stageResult = new StageResult();
            Duration waypointPenalty = Duration.ofSeconds(120); // 2 minutes
            Duration speedPenalty = Duration.ofSeconds(300); // 5 minutes
            Duration discount = Duration.ofSeconds(45); // 45 seconds

            // When
            stageResult.setPenaltyWaypoint(waypointPenalty);
            stageResult.setPenaltySpeed(speedPenalty);
            stageResult.setDiscountClaim(discount);

            // Then
            assertEquals(120, stageResult.getPenaltyWaypoint().getSeconds());
            assertEquals(300, stageResult.getPenaltySpeed().getSeconds());
            assertEquals(45, stageResult.getDiscountClaim().getSeconds());

            // Calculate total penalty (business logic example)
            long totalPenalty = stageResult.getPenaltyWaypoint().getSeconds() +
                    stageResult.getPenaltySpeed().getSeconds() -
                    stageResult.getDiscountClaim().getSeconds();
            assertEquals(375, totalPenalty); // 120 + 300 - 45
        }

        @Test
        @DisplayName("Should support elapsed time tracking")
        void shouldSupportElapsedTimeTracking() {
            // Given
            StageResult stageResult = new StageResult();
            Integer[] validElapsedTimes = {
                    3600, // 1 hour
                    7200, // 2 hours
                    1800, // 30 minutes
                    10800, // 3 hours
                    900 // 15 minutes
            };

            for (Integer elapsedTime : validElapsedTimes) {
                // When
                stageResult.setElapsedTimeSeconds(elapsedTime);

                // Then
                assertEquals(elapsedTime, stageResult.getElapsedTimeSeconds());
                assertTrue(elapsedTime > 0, "Elapsed time should be positive");
            }
        }

        @Test
        @DisplayName("Should represent complete stage result information")
        void shouldRepresentCompleteStageResultInformation() {
            // Given
            Event event = new Event(1L, "Rally Nacional", "Colombia", "National rally",
                    LocalDate.now(), LocalDate.now().plusDays(3));
            Stage stage = new Stage(2L, "Etapa Especial 1", 1, false, event);

            Category category = new Category(1L, "Rally", "Rally Category");
            User user = new User();
            user.setId(1L);
            user.setFirstName("Juan");
            Vehicle vehicle = new Vehicle(3L, "Toyota Hilux", "SOAT-001", "RLY001", category, user);

            StageResult stageResult = new StageResult();

            // When
            stageResult.setId(10L);
            stageResult.setTimestamp(LocalDateTime.of(2024, 6, 15, 10, 30));
            stageResult.setLatitude(6.2442);
            stageResult.setLongitude(-75.5812);
            stageResult.setPenaltyWaypoint(Duration.ofSeconds(60));
            stageResult.setPenaltySpeed(Duration.ofSeconds(120));
            stageResult.setDiscountClaim(Duration.ofSeconds(30));
            stageResult.setElapsedTimeSeconds(3900); // 1 hour 5 minutes
            stageResult.setStage(stage);
            stageResult.setVehicle(vehicle);

            // Then
            assertNotNull(stageResult.getId());
            assertNotNull(stageResult.getTimestamp());
            assertNotNull(stageResult.getStage());
            assertNotNull(stageResult.getVehicle());
            assertEquals("Etapa Especial 1", stageResult.getStage().getName());
            assertEquals("Toyota Hilux", stageResult.getVehicle().getName());
            assertEquals("Juan", stageResult.getVehicle().getUser().getFirstName());
            assertTrue(stageResult.getElapsedTimeSeconds() > 0);
        }

        @Test
        @DisplayName("Should support result comparison logic")
        void shouldSupportResultComparisonLogic() {
            // Given - Multiple results for comparison
            StageResult result1 = new StageResult();
            result1.setElapsedTimeSeconds(3600); // 1 hour

            StageResult result2 = new StageResult();
            result2.setElapsedTimeSeconds(3900); // 1 hour 5 minutes

            StageResult result3 = new StageResult();
            result3.setElapsedTimeSeconds(3300); // 55 minutes

            // When & Then - Compare times (business logic example)
            assertTrue(result1.getElapsedTimeSeconds() < result2.getElapsedTimeSeconds(),
                    "Result 1 should be faster than result 2");
            assertTrue(result3.getElapsedTimeSeconds() < result1.getElapsedTimeSeconds(),
                    "Result 3 should be fastest");
            assertTrue(result3.getElapsedTimeSeconds() < result2.getElapsedTimeSeconds(),
                    "Result 3 should be faster than result 2");
        }
    }

    // ========== RELATIONSHIP TESTS ==========

    @Nested
    @DisplayName("Relationship Tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should maintain stage relationship")
        void shouldMaintainStageRelationship() {
            // Given
            StageResult stageResult = new StageResult();
            Event event = new Event(1L, "Test Event", "Location", "Details",
                    LocalDate.now(), LocalDate.now().plusDays(1));

            Stage stage1 = new Stage(1L, "Etapa 1", 1, false, event);
            Stage stage2 = new Stage(2L, "Etapa 2", 2, false, event);

            // When - Set initial stage
            stageResult.setStage(stage1);

            // Then
            assertEquals(stage1, stageResult.getStage());
            assertEquals("Etapa 1", stageResult.getStage().getName());

            // When - Change stage
            stageResult.setStage(stage2);

            // Then
            assertEquals(stage2, stageResult.getStage());
            assertEquals("Etapa 2", stageResult.getStage().getName());
        }

        @Test
        @DisplayName("Should maintain vehicle relationship")
        void shouldMaintainVehicleRelationship() {
            // Given
            StageResult stageResult = new StageResult();
            Category category = new Category(1L, "Rally", "Rally Category");

            User user1 = new User();
            user1.setId(1L);
            user1.setFirstName("Juan");

            User user2 = new User();
            user2.setId(2L);
            user2.setFirstName("Pedro");

            Vehicle vehicle1 = new Vehicle(1L, "Toyota Hilux", "SOAT-001", "RLY001", category, user1);
            Vehicle vehicle2 = new Vehicle(2L, "Ford Ranger", "SOAT-002", "RLY002", category, user2);

            // When - Set initial vehicle
            stageResult.setVehicle(vehicle1);

            // Then
            assertEquals(vehicle1, stageResult.getVehicle());
            assertEquals("Toyota Hilux", stageResult.getVehicle().getName());
            assertEquals("Juan", stageResult.getVehicle().getUser().getFirstName());

            // When - Change vehicle
            stageResult.setVehicle(vehicle2);

            // Then
            assertEquals(vehicle2, stageResult.getVehicle());
            assertEquals("Ford Ranger", stageResult.getVehicle().getName());
            assertEquals("Pedro", stageResult.getVehicle().getUser().getFirstName());
        }

        @Test
        @DisplayName("Should handle null relationships gracefully")
        void shouldHandleNullRelationshipsGracefully() {
            // Given
            StageResult stageResult = new StageResult();
            Stage stage = new Stage(1L, "Test Stage", 1, false, new Event());
            Vehicle vehicle = new Vehicle(1L, "Test Vehicle", "SOAT-001", "TEST001",
                    new Category(), new User());

            // When - Set relationships then clear them
            stageResult.setStage(stage);
            stageResult.setVehicle(vehicle);

            // Then - Verify set
            assertNotNull(stageResult.getStage());
            assertNotNull(stageResult.getVehicle());

            // When - Clear relationships
            stageResult.setStage(null);
            stageResult.setVehicle(null);

            // Then - Verify cleared
            assertNull(stageResult.getStage());
            assertNull(stageResult.getVehicle());
        }

        @Test
        @DisplayName("Should support complete stage result with relationships")
        void shouldSupportCompleteStageResultWithRelationships() {
            // Given
            Event event = new Event(3L, "Copa Rally Andina", "Manizales",
                    "Competencia en la cordillera",
                    LocalDate.of(2024, 9, 15),
                    LocalDate.of(2024, 9, 17));
            Stage stage = new Stage(5L, "Etapa La Línea", 2, false, event);

            Category category = new Category(3L, "Cross Country", "Long distance competition");
            User user = new User();
            user.setId(15L);
            user.setFirstName("Maria");
            user.setLastName("Rodriguez");

            Vehicle vehicle = new Vehicle(8L, "Jeep Wrangler", "SOAT-CC-008", "CC008", category, user);

            // When
            StageResult stageResult = new StageResult(10L, LocalDateTime.now(), 5.0689, -75.5174,
                    Duration.ofSeconds(90), Duration.ofSeconds(180),
                    Duration.ofSeconds(45), 5400, stage, vehicle);

            // Then
            assertEquals(10L, stageResult.getId());

            // Verify stage relationship
            assertNotNull(stageResult.getStage());
            assertEquals(5L, stageResult.getStage().getId());
            assertEquals("Etapa La Línea", stageResult.getStage().getName());
            assertEquals("Copa Rally Andina", stageResult.getStage().getEvent().getName());

            // Verify vehicle relationship
            assertNotNull(stageResult.getVehicle());
            assertEquals(8L, stageResult.getVehicle().getId());
            assertEquals("Jeep Wrangler", stageResult.getVehicle().getName());
            assertEquals("Maria", stageResult.getVehicle().getUser().getFirstName());
            assertEquals("Cross Country", stageResult.getVehicle().getCategory().getName());
        }
    }

    // ========== INTEGRATION TESTS ==========

    @Nested
    @DisplayName("Integration and Usage Tests")
    class IntegrationUsageTests {

        @Test
        @DisplayName("Should support complete result lifecycle")
        void shouldSupportCompleteResultLifecycle() {
            // Given - Create new result
            StageResult stageResult = new StageResult();

            // When - Set up basic result (step 1)
            stageResult.setTimestamp(LocalDateTime.of(2024, 6, 15, 14, 30));
            stageResult.setLatitude(6.2442);
            stageResult.setLongitude(-75.5812);
            stageResult.setElapsedTimeSeconds(4200); // 1 hour 10 minutes

            // Then - Verify initial state
            assertNull(stageResult.getId());
            assertEquals(6.2442, stageResult.getLatitude());
            assertEquals(-75.5812, stageResult.getLongitude());
            assertEquals(4200, stageResult.getElapsedTimeSeconds());

            // When - Add penalties and relationships (step 2)
            stageResult.setPenaltyWaypoint(Duration.ofSeconds(120));
            stageResult.setPenaltySpeed(Duration.ofSeconds(60));
            stageResult.setDiscountClaim(Duration.ofSeconds(30));

            Event event = new Event(1L, "Rally Regional", "Antioquia", "Regional rally",
                    LocalDate.now(), LocalDate.now().plusDays(2));
            Stage stage = new Stage(3L, "Etapa Especial", 1, false, event);

            Category category = new Category(1L, "Rally", "Rally Category");
            User user = new User();
            user.setFirstName("Carlos");
            Vehicle vehicle = new Vehicle(5L, "Toyota Hilux", "SOAT-005", "RLY005", category, user);

            stageResult.setStage(stage);
            stageResult.setVehicle(vehicle);

            // Then - Verify with relationships
            assertEquals(stage, stageResult.getStage());
            assertEquals(vehicle, stageResult.getVehicle());
            assertEquals(120, stageResult.getPenaltyWaypoint().getSeconds());

            // When - Simulate database save (step 3)
            stageResult.setId(25L);

            // Then - Verify complete result
            assertEquals(25L, stageResult.getId());
            assertEquals("Etapa Especial", stageResult.getStage().getName());
            assertEquals("Toyota Hilux", stageResult.getVehicle().getName());
        }

        @Test
        @DisplayName("Should work with realistic result scenarios")
        void shouldWorkWithRealisticResultScenarios() {
            // Given - Different result scenarios
            Object[][] resultScenarios = {
                    { "Fast clean run", 3600, 0, 0, 0 }, // 1 hour, no penalties
                    { "Good run with waypoint penalty", 3900, 60, 0, 0 }, // Missed waypoint
                    { "Speed penalty run", 3300, 0, 120, 0 }, // Speeding violation
                    { "Perfect run with discount", 3800, 0, 0, 30 }, // Good sportsmanship
                    { "Challenging run", 4500, 90, 180, 15 } // Multiple penalties/discounts
            };

            Event event = new Event(1L, "Rally Test", "Test Location", "Details",
                    LocalDate.now(), LocalDate.now().plusDays(1));
            Stage stage = new Stage(1L, "Test Stage", 1, false, event);

            Category category = new Category(1L, "Rally", "Rally Category");
            User user = new User();
            user.setFirstName("Test Driver");
            Vehicle vehicle = new Vehicle(1L, "Test Vehicle", "SOAT-001", "TEST001", category, user);

            for (int i = 0; i < resultScenarios.length; i++) {
                Object[] scenario = resultScenarios[i];

                // When
                int elapsedTime = (int) scenario[1];
                int waypointPenalty = (int) scenario[2];
                int speedPenalty = (int) scenario[3];
                int discount = (int) scenario[4];

                StageResult stageResult = new StageResult();
                stageResult.setId((long) (i + 1));
                stageResult.setTimestamp(LocalDateTime.now().plusMinutes(i * 30));
                stageResult.setLatitude(6.0 + i * 0.1);
                stageResult.setLongitude(-75.0 - i * 0.1);
                stageResult.setElapsedTimeSeconds(elapsedTime);
                stageResult.setPenaltyWaypoint(waypointPenalty > 0 ? Duration.ofSeconds(waypointPenalty) : null);
                stageResult.setPenaltySpeed(speedPenalty > 0 ? Duration.ofSeconds(speedPenalty) : null);
                stageResult.setDiscountClaim(discount > 0 ? Duration.ofSeconds(discount) : null);
                stageResult.setStage(stage);
                stageResult.setVehicle(vehicle);

                // Then
                assertEquals(elapsedTime, stageResult.getElapsedTimeSeconds());
                assertEquals(stage, stageResult.getStage());
                assertEquals(vehicle, stageResult.getVehicle());

                if (waypointPenalty > 0) {
                    assertEquals(waypointPenalty, stageResult.getPenaltyWaypoint().getSeconds());
                }
                if (speedPenalty > 0) {
                    assertEquals(speedPenalty, stageResult.getPenaltySpeed().getSeconds());
                }
                if (discount > 0) {
                    assertEquals(discount, stageResult.getDiscountClaim().getSeconds());
                }
            }
        }

        @Test
        @DisplayName("Should support result corrections and updates")
        void shouldSupportResultCorrectionsAndUpdates() {
            // Given - Initial result with errors
            LocalDateTime originalTimestamp = LocalDateTime.of(2024, 6, 15, 10, 0);
            StageResult stageResult = new StageResult();
            stageResult.setTimestamp(originalTimestamp);
            stageResult.setElapsedTimeSeconds(3600);
            stageResult.setPenaltyWaypoint(Duration.ofSeconds(120));

            // When - Correct timestamp
            assertEquals(originalTimestamp, stageResult.getTimestamp());

            LocalDateTime correctedTimestamp = originalTimestamp.plusMinutes(15);
            stageResult.setTimestamp(correctedTimestamp);

            // Then - Verify timestamp correction
            assertEquals(correctedTimestamp, stageResult.getTimestamp());

            // When - Update elapsed time (timing error correction)
            assertEquals(3600, stageResult.getElapsedTimeSeconds());

            stageResult.setElapsedTimeSeconds(3540); // 1 minute faster

            // Then - Verify time correction
            assertEquals(3540, stageResult.getElapsedTimeSeconds());

            // When - Remove penalty (appeal successful)
            assertEquals(120, stageResult.getPenaltyWaypoint().getSeconds());

            stageResult.setPenaltyWaypoint(null);

            // Then - Verify penalty removal
            assertNull(stageResult.getPenaltyWaypoint());
        }

        @Test
        @DisplayName("Should support multi-stage result tracking")
        void shouldSupportMultiStageResultTracking() {
            // Given - One vehicle, multiple stages
            Event event = new Event(1L, "Rally Multi-Etapa", "Colombia", "Multi-stage rally",
                    LocalDate.now(), LocalDate.now().plusDays(3));

            Category category = new Category(1L, "Rally", "Rally Category");
            User user = new User();
            user.setFirstName("Pilot");
            Vehicle vehicle = new Vehicle(1L, "Racing Vehicle", "SOAT-001", "RACE001", category, user);

            Stage[] stages = {
                    new Stage(1L, "Prólogo", 1, false, event),
                    new Stage(2L, "Etapa 1", 2, false, event),
                    new Stage(3L, "Etapa 2", 3, false, event)
            };

            // When & Then - Create results for each stage
            for (int i = 0; i < stages.length; i++) {
                StageResult result = new StageResult();
                result.setId((long) (i + 1));
                result.setTimestamp(LocalDateTime.now().plusHours(i * 2));
                result.setLatitude(6.0 + i * 0.5);
                result.setLongitude(-75.0 - i * 0.5);
                result.setElapsedTimeSeconds(1800 + i * 600); // Increasing times
                result.setStage(stages[i]);
                result.setVehicle(vehicle);

                assertEquals(stages[i], result.getStage());
                assertEquals(vehicle, result.getVehicle());
                assertEquals(1800 + i * 600, result.getElapsedTimeSeconds());
                assertEquals("Rally Multi-Etapa", result.getStage().getEvent().getName());
                assertEquals("Pilot", result.getVehicle().getUser().getFirstName());
            }
        }
    }
}
