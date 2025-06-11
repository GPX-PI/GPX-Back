package com.udea.gpx.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para Stage Model - Entidad JPA para etapas de competencia
 */
@DisplayName("Stage Model Tests")
class StageTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create stage with default constructor")
        void shouldCreateStageWithDefaultConstructor() {
            // When
            Stage stage = new Stage();

            // Then
            assertNotNull(stage);
            assertNull(stage.getId());
            assertNull(stage.getName());
            assertEquals(0, stage.getOrderNumber()); // primitive int default
            assertFalse(stage.isNeutralized()); // primitive boolean default
            assertNull(stage.getEvent());
        }

        @Test
        @DisplayName("Should create stage with full constructor")
        void shouldCreateStageWithFullConstructor() {
            // Given
            Long expectedId = 1L;
            String expectedName = "Etapa Especial 1";
            int expectedOrderNumber = 1;
            boolean expectedNeutralized = false;
            Event expectedEvent = new Event(1L, "Rally Test", "Test Location",
                    "Test Details", LocalDate.now(), LocalDate.now().plusDays(3));

            // When
            Stage stage = new Stage(expectedId, expectedName, expectedOrderNumber,
                    expectedNeutralized, expectedEvent);

            // Then
            assertNotNull(stage);
            assertEquals(expectedId, stage.getId());
            assertEquals(expectedName, stage.getName());
            assertEquals(expectedOrderNumber, stage.getOrderNumber());
            assertEquals(expectedNeutralized, stage.isNeutralized());
            assertEquals(expectedEvent, stage.getEvent());
            assertEquals("Rally Test", stage.getEvent().getName());
        }

        @Test
        @DisplayName("Should handle null values in full constructor")
        void shouldHandleNullValuesInFullConstructor() {
            // When
            Stage stage = new Stage(null, null, 0, false, null);

            // Then
            assertNotNull(stage);
            assertNull(stage.getId());
            assertNull(stage.getName());
            assertEquals(0, stage.getOrderNumber());
            assertFalse(stage.isNeutralized());
            assertNull(stage.getEvent());
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
            Stage stage = new Stage();
            Long expectedId = 100L;

            // When
            stage.setId(expectedId);

            // Then
            assertEquals(expectedId, stage.getId());
        }

        @Test
        @DisplayName("Should get and set name correctly")
        void shouldGetAndSetNameCorrectly() {
            // Given
            Stage stage = new Stage();
            String expectedName = "Etapa Especial Montaña";

            // When
            stage.setName(expectedName);

            // Then
            assertEquals(expectedName, stage.getName());
        }

        @Test
        @DisplayName("Should get and set order number correctly")
        void shouldGetAndSetOrderNumberCorrectly() {
            // Given
            Stage stage = new Stage();
            int expectedOrderNumber = 5;

            // When
            stage.setOrderNumber(expectedOrderNumber);

            // Then
            assertEquals(expectedOrderNumber, stage.getOrderNumber());
        }

        @Test
        @DisplayName("Should get and set neutralized correctly")
        void shouldGetAndSetNeutralizedCorrectly() {
            // Given
            Stage stage = new Stage();

            // When
            stage.setNeutralized(true);

            // Then
            assertTrue(stage.isNeutralized());

            // When
            stage.setNeutralized(false);

            // Then
            assertFalse(stage.isNeutralized());
        }

        @Test
        @DisplayName("Should get and set event correctly")
        void shouldGetAndSetEventCorrectly() {
            // Given
            Stage stage = new Stage();
            Event expectedEvent = new Event();
            expectedEvent.setId(5L);
            expectedEvent.setName("Rally Nacional");
            expectedEvent.setLocation("Medellín");

            // When
            stage.setEvent(expectedEvent);

            // Then
            assertEquals(expectedEvent, stage.getEvent());
            assertEquals(5L, stage.getEvent().getId());
            assertEquals("Rally Nacional", stage.getEvent().getName());
            assertEquals("Medellín", stage.getEvent().getLocation());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            // Given
            Event event = new Event(1L, "Test Event", "Test Location", "Test Details",
                    LocalDate.now(), LocalDate.now().plusDays(1));
            Stage stage = new Stage(1L, "Test Stage", 1, false, event);

            // When
            stage.setId(null);
            stage.setName(null);
            stage.setOrderNumber(0);
            stage.setNeutralized(false);
            stage.setEvent(null);

            // Then
            assertNull(stage.getId());
            assertNull(stage.getName());
            assertEquals(0, stage.getOrderNumber());
            assertFalse(stage.isNeutralized());
            assertNull(stage.getEvent());
        }
    }

    // ========== BUSINESS LOGIC TESTS ==========

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should support typical stage names")
        void shouldSupportTypicalStageNames() {
            // Given
            String[] typicalNames = {
                    "Etapa Especial 1",
                    "Prólogo",
                    "Etapa de Enlace",
                    "Super Especial",
                    "Etapa Maratón",
                    "Final Power Stage"
            };

            for (String name : typicalNames) {
                // When
                Stage stage = new Stage();
                stage.setName(name);

                // Then
                assertEquals(name, stage.getName());
                assertNotNull(stage.getName());
                assertTrue(name.length() <= 50, "Name should fit column length constraint");
            }
        }

        @Test
        @DisplayName("Should support sequential order numbering")
        void shouldSupportSequentialOrderNumbering() {
            // Given
            Stage stage = new Stage();
            int[] validOrderNumbers = { 1, 2, 3, 4, 5, 10, 15, 99 };

            for (int orderNumber : validOrderNumbers) {
                // When
                stage.setOrderNumber(orderNumber);

                // Then
                assertEquals(orderNumber, stage.getOrderNumber());
                assertTrue(orderNumber > 0, "Order number should be positive");
            }
        }

        @Test
        @DisplayName("Should support neutralization states")
        void shouldSupportNeutralizationStates() {
            // Given
            Stage stage = new Stage();

            // When - Normal stage
            stage.setNeutralized(false);

            // Then
            assertFalse(stage.isNeutralized());

            // When - Neutralized stage (due to weather, safety, etc.)
            stage.setNeutralized(true);

            // Then
            assertTrue(stage.isNeutralized());
        }

        @Test
        @DisplayName("Should represent complete stage information")
        void shouldRepresentCompleteStageInformation() {
            // Given
            Stage stage = new Stage();
            Event event = new Event();
            event.setId(10L);
            event.setName("Rally Nacional");
            event.setLocation("Cordillera Central");

            // When
            stage.setId(5L);
            stage.setName("Etapa Especial Montaña");
            stage.setOrderNumber(3);
            stage.setNeutralized(false);
            stage.setEvent(event);

            // Then
            assertNotNull(stage.getId());
            assertNotNull(stage.getName());
            assertTrue(stage.getOrderNumber() > 0);
            assertNotNull(stage.getEvent());
            assertEquals("Etapa Especial Montaña", stage.getName());
            assertEquals(3, stage.getOrderNumber());
            assertEquals("Rally Nacional", stage.getEvent().getName());
            assertFalse(stage.isNeutralized());
        }

        @Test
        @DisplayName("Should support stage progression logic")
        void shouldSupportStageProgressionLogic() {
            // Given - Multiple stages for one event
            Event event = new Event();
            event.setName("Rally Test");

            Stage[] stages = {
                    new Stage(1L, "Prólogo", 1, false, event),
                    new Stage(2L, "Etapa 1", 2, false, event),
                    new Stage(3L, "Etapa 2", 3, true, event), // Neutralized
                    new Stage(4L, "Power Stage", 4, false, event)
            };

            // When & Then - Verify stage progression
            for (int i = 0; i < stages.length; i++) {
                assertEquals(i + 1, stages[i].getOrderNumber());
                assertEquals(event, stages[i].getEvent());

                // Special case for neutralized stage
                if (i == 2) {
                    assertTrue(stages[i].isNeutralized());
                } else {
                    assertFalse(stages[i].isNeutralized());
                }
            }
        }
    }

    // ========== RELATIONSHIP TESTS ==========

    @Nested
    @DisplayName("Relationship Tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should maintain event relationship")
        void shouldMaintainEventRelationship() {
            // Given
            Stage stage = new Stage();
            Event event1 = new Event();
            event1.setId(1L);
            event1.setName("Rally Nacional");

            Event event2 = new Event();
            event2.setId(2L);
            event2.setName("Enduro Extremo");

            // When - Set initial event
            stage.setEvent(event1);

            // Then
            assertEquals(event1, stage.getEvent());
            assertEquals("Rally Nacional", stage.getEvent().getName());

            // When - Change event
            stage.setEvent(event2);

            // Then
            assertEquals(event2, stage.getEvent());
            assertEquals("Enduro Extremo", stage.getEvent().getName());
        }

        @Test
        @DisplayName("Should handle null event relationship gracefully")
        void shouldHandleNullEventRelationshipGracefully() {
            // Given
            Stage stage = new Stage();
            Event event = new Event(1L, "Test Event", "Test Location", "Details",
                    LocalDate.now(), LocalDate.now().plusDays(2));

            // When - Set event then clear it
            stage.setEvent(event);

            // Then - Verify set
            assertNotNull(stage.getEvent());
            assertEquals("Test Event", stage.getEvent().getName());

            // When - Clear event
            stage.setEvent(null);

            // Then - Verify cleared
            assertNull(stage.getEvent());
        }

        @Test
        @DisplayName("Should support complete stage with event relationship")
        void shouldSupportCompleteStageWithEventRelationship() {
            // Given
            Event event = new Event(3L, "Copa Rally Andina", "Manizales",
                    "Competencia en la cordillera",
                    LocalDate.of(2024, 9, 15),
                    LocalDate.of(2024, 9, 17));

            // When
            Stage stage = new Stage(5L, "Etapa Especial La Línea", 2, false, event);

            // Then
            assertEquals(5L, stage.getId());
            assertEquals("Etapa Especial La Línea", stage.getName());
            assertEquals(2, stage.getOrderNumber());
            assertFalse(stage.isNeutralized());

            // Verify event relationship
            assertNotNull(stage.getEvent());
            assertEquals(3L, stage.getEvent().getId());
            assertEquals("Copa Rally Andina", stage.getEvent().getName());
            assertEquals("Manizales", stage.getEvent().getLocation());
        }

        @Test
        @DisplayName("Should support multiple stages per event")
        void shouldSupportMultipleStagesPerEvent() {
            // Given - One event with multiple stages
            Event rallyEvent = new Event();
            rallyEvent.setId(10L);
            rallyEvent.setName("Rally de los Andes");
            rallyEvent.setLocation("Cordillera Andina");

            String[] stageNames = {
                    "Prólogo Urbano",
                    "Etapa 1 - Bosque",
                    "Etapa 2 - Montaña",
                    "Super Especial Final"
            };

            // When & Then - Create stages for the event
            for (int i = 0; i < stageNames.length; i++) {
                Stage stage = new Stage((long) (i + 1), stageNames[i], i + 1, false, rallyEvent);

                assertEquals(rallyEvent, stage.getEvent());
                assertEquals(stageNames[i], stage.getName());
                assertEquals(i + 1, stage.getOrderNumber());
                assertEquals("Rally de los Andes", stage.getEvent().getName());
                assertFalse(stage.isNeutralized());
            }
        }
    }

    // ========== FIELD VALIDATION TESTS ==========

    @Nested
    @DisplayName("Field Validation Tests")
    class FieldValidationTests {

        @Test
        @DisplayName("Should handle maximum field lengths")
        void shouldHandleMaximumFieldLengths() {
            // Given
            Stage stage = new Stage();
            String maxName = "A".repeat(50); // Maximum length for name

            // When
            stage.setName(maxName);

            // Then
            assertEquals(50, stage.getName().length());
        }

        @Test
        @DisplayName("Should handle edge case values")
        void shouldHandleEdgeCaseValues() {
            // Given
            Stage stage = new Stage();

            // When & Then - Edge cases
            stage.setId(0L);
            assertEquals(0L, stage.getId());

            stage.setId(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, stage.getId());

            stage.setName("");
            assertEquals("", stage.getName());

            stage.setOrderNumber(1);
            assertEquals(1, stage.getOrderNumber());

            stage.setOrderNumber(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, stage.getOrderNumber());
        }

        @Test
        @DisplayName("Should handle special characters in stage names")
        void shouldHandleSpecialCharactersInStageNames() {
            // Given
            Stage stage = new Stage();
            String nameWithSpecialChars = "Etapa 1 - Río Magdalena (Especial)";

            // When
            stage.setName(nameWithSpecialChars);

            // Then
            assertEquals(nameWithSpecialChars, stage.getName());
        }

        @Test
        @DisplayName("Should validate order number constraints")
        void shouldValidateOrderNumberConstraints() {
            // Given
            Stage stage = new Stage();

            // When & Then - Valid order numbers
            int[] validOrders = { 1, 5, 10, 25, 50, 99 };
            for (int order : validOrders) {
                stage.setOrderNumber(order);
                assertEquals(order, stage.getOrderNumber());
                assertTrue(order >= 0, "Order number should be non-negative");
            }

            // Edge case: zero order (could be valid for prologue)
            stage.setOrderNumber(0);
            assertEquals(0, stage.getOrderNumber());
        }
    }

    // ========== INTEGRATION TESTS ==========

    @Nested
    @DisplayName("Integration and Usage Tests")
    class IntegrationUsageTests {

        @Test
        @DisplayName("Should support complete stage lifecycle")
        void shouldSupportCompleteStageLifecycle() {
            // Given - Create new stage
            Stage stage = new Stage();

            // When - Set up stage (step 1)
            stage.setName("Etapa Especial Río Negro");
            stage.setOrderNumber(4);
            stage.setNeutralized(false);

            // Then - Verify initial state
            assertNull(stage.getId());
            assertEquals("Etapa Especial Río Negro", stage.getName());
            assertEquals(4, stage.getOrderNumber());
            assertFalse(stage.isNeutralized());
            assertNull(stage.getEvent());

            // When - Add event relationship (step 2)
            Event event = new Event();
            event.setName("Rally Nacional Colombia");
            event.setLocation("Antioquia");
            event.setStartDate(LocalDate.now().plusDays(30));
            event.setEndDate(LocalDate.now().plusDays(32));

            stage.setEvent(event);

            // Then - Verify with event
            assertEquals(event, stage.getEvent());
            assertEquals("Rally Nacional Colombia", stage.getEvent().getName());

            // When - Simulate database save (step 3)
            stage.setId(25L);

            // Then - Verify complete stage
            assertEquals(25L, stage.getId());
            assertEquals("Etapa Especial Río Negro", stage.getName());
            assertEquals(4, stage.getOrderNumber());
            assertEquals("Rally Nacional Colombia", stage.getEvent().getName());
        }

        @Test
        @DisplayName("Should work with realistic stage scenarios")
        void shouldWorkWithRealisticStageScenarios() {
            // Given - Different stage types in a rally
            Object[][] stageScenarios = {
                    { "Prólogo Urbano", 1, false, "Short urban stage" },
                    { "Etapa 1 - Bosque Andino", 2, false, "Forest mountain stage" },
                    { "Etapa 2 - Cruce del Río", 3, true, "River crossing - neutralized due to floods" },
                    { "Super Especial Medellín", 4, false, "Stadium super special stage" },
                    { "Power Stage Final", 5, false, "Final power stage with bonus points" }
            };

            Event rallyEvent = new Event(1L, "Rally Colombia", "Multiple Locations",
                    "National rally championship",
                    LocalDate.now().plusDays(15),
                    LocalDate.now().plusDays(17));

            for (int i = 0; i < stageScenarios.length; i++) {
                Object[] scenario = stageScenarios[i];

                // When
                String stageName = (String) scenario[0];
                int orderNumber = (int) scenario[1];
                boolean isNeutralized = (boolean) scenario[2];

                Stage stage = new Stage((long) (i + 1), stageName, orderNumber, isNeutralized, rallyEvent);

                // Then
                assertEquals(stageName, stage.getName());
                assertEquals(orderNumber, stage.getOrderNumber());
                assertEquals(isNeutralized, stage.isNeutralized());
                assertEquals(rallyEvent, stage.getEvent());
                assertEquals("Rally Colombia", stage.getEvent().getName());

                // Verify constraints
                assertTrue(stage.getName().length() <= 50);
                assertTrue(stage.getOrderNumber() > 0 || stage.getOrderNumber() == 0); // Allow 0 for prologue
                assertNotNull(stage.getEvent());
            }
        }

        @Test
        @DisplayName("Should support stage neutralization management")
        void shouldSupportStageNeutralizationManagement() {
            // Given - Stage initially active
            Event event = new Event(1L, "Rally Test", "Test Location", "Details",
                    LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));

            Stage stage = new Stage();
            stage.setName("Etapa Peligrosa");
            stage.setOrderNumber(3);
            stage.setNeutralized(false);
            stage.setEvent(event);

            // When - Stage runs normally
            assertFalse(stage.isNeutralized());

            // Then - Neutralize due to safety concerns
            stage.setNeutralized(true);

            // Verify neutralization
            assertTrue(stage.isNeutralized());
            assertEquals("Etapa Peligrosa", stage.getName()); // Stage data unchanged
            assertEquals(3, stage.getOrderNumber());
            assertEquals(event, stage.getEvent());

            // When - Conditions improve, reactivate stage
            stage.setNeutralized(false);

            // Then - Verify reactivation
            assertFalse(stage.isNeutralized());
        }

        @Test
        @DisplayName("Should support stage order modifications")
        void shouldSupportStageOrderModifications() {
            // Given - Stage with initial order
            Stage stage = new Stage();
            stage.setName("Etapa Flexible");
            stage.setOrderNumber(5);

            Event event = new Event();
            event.setName("Rally Adaptable");
            stage.setEvent(event);

            // When - Modify order (schedule change)
            assertEquals(5, stage.getOrderNumber());

            stage.setOrderNumber(3);

            // Then - Verify order change
            assertEquals(3, stage.getOrderNumber());
            assertEquals("Etapa Flexible", stage.getName()); // Stage data unchanged
            assertEquals("Rally Adaptable", stage.getEvent().getName());

            // When - Move to end of rally
            stage.setOrderNumber(10);

            // Then - Verify final position
            assertEquals(10, stage.getOrderNumber());
        }

        @Test
        @DisplayName("Should support event transfer between stages")
        void shouldSupportEventTransferBetweenStages() {
            // Given - Stage associated with one event
            Event originalEvent = new Event(1L, "Rally Original", "Location 1", "Details 1",
                    LocalDate.now(), LocalDate.now().plusDays(2));
            Event newEvent = new Event(2L, "Rally Nuevo", "Location 2", "Details 2",
                    LocalDate.now().plusDays(10), LocalDate.now().plusDays(12));

            Stage stage = new Stage();
            stage.setName("Etapa Transferible");
            stage.setOrderNumber(2);
            stage.setEvent(originalEvent);

            // When - Transfer to new event
            assertEquals("Rally Original", stage.getEvent().getName());

            stage.setEvent(newEvent);

            // Then - Verify transfer
            assertEquals("Rally Nuevo", stage.getEvent().getName());
            assertEquals(newEvent, stage.getEvent());
            assertEquals("Etapa Transferible", stage.getName()); // Stage data unchanged
            assertEquals(2, stage.getOrderNumber());
        }
    }
}
