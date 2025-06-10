package com.udea.GPX.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para Event Model - Entidad JPA para eventos de competencia
 */
@DisplayName("Event Model Tests")
class EventTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create event with default constructor")
        void shouldCreateEventWithDefaultConstructor() {
            // When
            Event event = new Event();

            // Then
            assertNotNull(event);
            assertNull(event.getId());
            assertNull(event.getName());
            assertNull(event.getLocation());
            assertNull(event.getDetails());
            assertNull(event.getStartDate());
            assertNull(event.getEndDate());
            assertEquals("", event.getPicture()); // Default value
        }

        @Test
        @DisplayName("Should create event with full constructor")
        void shouldCreateEventWithFullConstructor() {
            // Given
            Long expectedId = 1L;
            String expectedName = "Rally Nacional 2024";
            String expectedLocation = "Medellín, Colombia";
            String expectedDetails = "Competencia de rally cross country en terrenos montañosos";
            LocalDate expectedStartDate = LocalDate.of(2024, 6, 15);
            LocalDate expectedEndDate = LocalDate.of(2024, 6, 17);

            // When
            Event event = new Event(expectedId, expectedName, expectedLocation,
                    expectedDetails, expectedStartDate, expectedEndDate);

            // Then
            assertNotNull(event);
            assertEquals(expectedId, event.getId());
            assertEquals(expectedName, event.getName());
            assertEquals(expectedLocation, event.getLocation());
            assertEquals(expectedDetails, event.getDetails());
            assertEquals(expectedStartDate, event.getStartDate());
            assertEquals(expectedEndDate, event.getEndDate());
            assertEquals("", event.getPicture()); // Default value set by constructor
        }

        @Test
        @DisplayName("Should handle null values in full constructor")
        void shouldHandleNullValuesInFullConstructor() {
            // When
            Event event = new Event(null, null, null, null, null, null);

            // Then
            assertNotNull(event);
            assertNull(event.getId());
            assertNull(event.getName());
            assertNull(event.getLocation());
            assertNull(event.getDetails());
            assertNull(event.getStartDate());
            assertNull(event.getEndDate());
            assertEquals("", event.getPicture());
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
            Event event = new Event();
            Long expectedId = 100L;

            // When
            event.setId(expectedId);

            // Then
            assertEquals(expectedId, event.getId());
        }

        @Test
        @DisplayName("Should get and set name correctly")
        void shouldGetAndSetNameCorrectly() {
            // Given
            Event event = new Event();
            String expectedName = "Dakar Colombia 2024";

            // When
            event.setName(expectedName);

            // Then
            assertEquals(expectedName, event.getName());
        }

        @Test
        @DisplayName("Should get and set location correctly")
        void shouldGetAndSetLocationCorrectly() {
            // Given
            Event event = new Event();
            String expectedLocation = "Cartagena, Colombia";

            // When
            event.setLocation(expectedLocation);

            // Then
            assertEquals(expectedLocation, event.getLocation());
        }

        @Test
        @DisplayName("Should get and set details correctly")
        void shouldGetAndSetDetailsCorrectly() {
            // Given
            Event event = new Event();
            String expectedDetails = "Competencia internacional de rally raid con etapas especiales";

            // When
            event.setDetails(expectedDetails);

            // Then
            assertEquals(expectedDetails, event.getDetails());
        }

        @Test
        @DisplayName("Should get and set start date correctly")
        void shouldGetAndSetStartDateCorrectly() {
            // Given
            Event event = new Event();
            LocalDate expectedStartDate = LocalDate.of(2024, 8, 10);

            // When
            event.setStartDate(expectedStartDate);

            // Then
            assertEquals(expectedStartDate, event.getStartDate());
        }

        @Test
        @DisplayName("Should get and set end date correctly")
        void shouldGetAndSetEndDateCorrectly() {
            // Given
            Event event = new Event();
            LocalDate expectedEndDate = LocalDate.of(2024, 8, 15);

            // When
            event.setEndDate(expectedEndDate);

            // Then
            assertEquals(expectedEndDate, event.getEndDate());
        }

        @Test
        @DisplayName("Should get and set picture correctly")
        void shouldGetAndSetPictureCorrectly() {
            // Given
            Event event = new Event();
            String expectedPicture = "/images/events/rally2024.jpg";

            // When
            event.setPicture(expectedPicture);

            // Then
            assertEquals(expectedPicture, event.getPicture());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            // Given
            Event event = new Event(1L, "Test Event", "Test Location", "Test Details",
                    LocalDate.now(), LocalDate.now().plusDays(3));

            // When
            event.setId(null);
            event.setName(null);
            event.setLocation(null);
            event.setDetails(null);
            event.setStartDate(null);
            event.setEndDate(null);
            event.setPicture(null);

            // Then
            assertNull(event.getId());
            assertNull(event.getName());
            assertNull(event.getLocation());
            assertNull(event.getDetails());
            assertNull(event.getStartDate());
            assertNull(event.getEndDate());
            assertNull(event.getPicture());
        }
    }

    // ========== BUSINESS LOGIC TESTS ==========

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should support typical event names")
        void shouldSupportTypicalEventNames() {
            // Given
            String[] typicalNames = {
                    "Rally Nacional Colombia",
                    "Enduro Cross Country",
                    "Motocross Championship",
                    "4x4 Desert Challenge",
                    "Extreme Rally 2024",
                    "Copa Latinoamericana"
            };

            for (String name : typicalNames) {
                // When
                Event event = new Event();
                event.setName(name);

                // Then
                assertEquals(name, event.getName());
                assertNotNull(event.getName());
                assertTrue(name.length() <= 50, "Name should fit column length constraint");
            }
        }

        @Test
        @DisplayName("Should support typical event locations")
        void shouldSupportTypicalEventLocations() {
            // Given
            Event event = new Event();
            String[] validLocations = {
                    "Medellín, Colombia",
                    "Bogotá, Colombia",
                    "Cartagena, Colombia",
                    "Cali, Valle del Cauca",
                    "Bucaramanga, Santander",
                    "Manizales, Caldas"
            };

            for (String location : validLocations) {
                // When
                event.setLocation(location);

                // Then
                assertEquals(location, event.getLocation());
                assertTrue(location.length() <= 50, "Location should fit column length constraint");
            }
        }

        @Test
        @DisplayName("Should support detailed event descriptions")
        void shouldSupportDetailedEventDescriptions() {
            // Given
            Event event = new Event();
            String validDetails = "Competencia de rally raid con etapas especiales en terrenos " +
                    "montañosos y desérticos. Incluye pruebas de navegación y velocidad " +
                    "para vehículos todo terreno y motocicletas.";

            // When
            event.setDetails(validDetails);

            // Then
            assertEquals(validDetails, event.getDetails());
            assertTrue(validDetails.length() <= 400, "Details should fit column length constraint");
        }

        @Test
        @DisplayName("Should support date range validation")
        void shouldSupportDateRangeValidation() {
            // Given
            Event event = new Event();
            LocalDate startDate = LocalDate.of(2024, 6, 15);
            LocalDate endDate = LocalDate.of(2024, 6, 17);

            // When
            event.setStartDate(startDate);
            event.setEndDate(endDate);

            // Then
            assertEquals(startDate, event.getStartDate());
            assertEquals(endDate, event.getEndDate());
            assertTrue(event.getEndDate().isAfter(event.getStartDate()) ||
                    event.getEndDate().isEqual(event.getStartDate()),
                    "End date should be after or equal to start date");
        }

        @Test
        @DisplayName("Should represent complete event information")
        void shouldRepresentCompleteEventInformation() {
            // Given
            Event event = new Event();

            // When
            event.setId(5L);
            event.setName("Copa Rally Andina");
            event.setLocation("Manizales, Caldas");
            event.setDetails("Competencia internacional de rally en la cordillera andina");
            event.setStartDate(LocalDate.of(2024, 9, 20));
            event.setEndDate(LocalDate.of(2024, 9, 22));
            event.setPicture("/images/events/rally_andina.jpg");

            // Then
            assertNotNull(event.getId());
            assertNotNull(event.getName());
            assertNotNull(event.getLocation());
            assertNotNull(event.getDetails());
            assertNotNull(event.getStartDate());
            assertNotNull(event.getEndDate());
            assertNotNull(event.getPicture());
            assertEquals("Copa Rally Andina", event.getName());
            assertEquals("Manizales, Caldas", event.getLocation());
            assertTrue(event.getStartDate().isBefore(event.getEndDate()));
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
            Event event = new Event();
            String maxName = "A".repeat(50); // Maximum length for name
            String maxLocation = "B".repeat(50); // Maximum length for location
            String maxDetails = "C".repeat(400); // Maximum length for details
            String maxPicture = "D".repeat(255); // Maximum length for picture

            // When
            event.setName(maxName);
            event.setLocation(maxLocation);
            event.setDetails(maxDetails);
            event.setPicture(maxPicture);

            // Then
            assertEquals(50, event.getName().length());
            assertEquals(50, event.getLocation().length());
            assertEquals(400, event.getDetails().length());
            assertEquals(255, event.getPicture().length());
        }

        @Test
        @DisplayName("Should handle edge case values")
        void shouldHandleEdgeCaseValues() {
            // Given
            Event event = new Event();

            // When & Then - Edge cases
            event.setId(0L);
            assertEquals(0L, event.getId());

            event.setId(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, event.getId());

            event.setName("");
            assertEquals("", event.getName());

            event.setLocation("");
            assertEquals("", event.getLocation());

            event.setDetails("");
            assertEquals("", event.getDetails());

            event.setPicture("");
            assertEquals("", event.getPicture());
        }

        @Test
        @DisplayName("Should handle special characters in text fields")
        void shouldHandleSpecialCharactersInTextFields() {
            // Given
            Event event = new Event();
            String nameWithSpecialChars = "Rally 2024 - Edición Especial";
            String locationWithSpecialChars = "Bogotá, D.C. - Colombia";
            String detailsWithSpecialChars = "Competencia con premios de $100,000 USD";

            // When
            event.setName(nameWithSpecialChars);
            event.setLocation(locationWithSpecialChars);
            event.setDetails(detailsWithSpecialChars);

            // Then
            assertEquals(nameWithSpecialChars, event.getName());
            assertEquals(locationWithSpecialChars, event.getLocation());
            assertEquals(detailsWithSpecialChars, event.getDetails());
        }
    }

    // ========== INTEGRATION TESTS ==========

    @Nested
    @DisplayName("Integration and Usage Tests")
    class IntegrationUsageTests {

        @Test
        @DisplayName("Should support complete event lifecycle")
        void shouldSupportCompleteEventLifecycle() {
            // Given - Create new event
            Event event = new Event();

            // When - Set up event (step 1)
            event.setName("Rally Extremo Colombia");
            event.setLocation("La Guajira, Colombia");
            event.setDetails("Competencia de rally raid en el desierto de La Guajira");

            // Then - Verify initial state
            assertNull(event.getId());
            assertEquals("Rally Extremo Colombia", event.getName());
            assertEquals("La Guajira, Colombia", event.getLocation());
            assertNull(event.getStartDate());
            assertNull(event.getEndDate());
            assertEquals("", event.getPicture());

            // When - Add dates and picture (step 2)
            LocalDate startDate = LocalDate.of(2024, 11, 10);
            LocalDate endDate = LocalDate.of(2024, 11, 12);

            event.setStartDate(startDate);
            event.setEndDate(endDate);
            event.setPicture("/images/events/rally_guajira.jpg");

            // Then - Verify with dates
            assertEquals(startDate, event.getStartDate());
            assertEquals(endDate, event.getEndDate());
            assertEquals("/images/events/rally_guajira.jpg", event.getPicture());

            // When - Simulate database save (step 3)
            event.setId(50L);

            // Then - Verify complete event
            assertEquals(50L, event.getId());
            assertEquals("Rally Extremo Colombia", event.getName());
            assertTrue(event.getStartDate().isBefore(event.getEndDate()));
        }

        @Test
        @DisplayName("Should work with realistic event scenarios")
        void shouldWorkWithRealisticEventScenarios() {
            // Given - Different event types
            Object[][] eventScenarios = {
                    { "Rally Nacional", "Medellín", "Competencia nacional de rally", "2024-06-15", "2024-06-17" },
                    { "Enduro Challenge", "Cali", "Prueba de resistencia en enduro", "2024-07-20", "2024-07-21" },
                    { "Motocross Cup", "Bogotá", "Copa de motocross urbano", "2024-08-05", "2024-08-05" },
                    { "4x4 Adventure", "Pereira", "Aventura todo terreno", "2024-09-10", "2024-09-12" },
                    { "Desert Rally", "Riohacha", "Rally en el desierto", "2024-10-15", "2024-10-18" }
            };

            for (Object[] scenario : eventScenarios) {
                // When
                String name = (String) scenario[0];
                String location = (String) scenario[1];
                String details = (String) scenario[2];
                LocalDate startDate = LocalDate.parse((String) scenario[3]);
                LocalDate endDate = LocalDate.parse((String) scenario[4]);

                Event event = new Event(1L, name, location, details, startDate, endDate);

                // Then
                assertEquals(name, event.getName());
                assertEquals(location, event.getLocation());
                assertEquals(details, event.getDetails());
                assertEquals(startDate, event.getStartDate());
                assertEquals(endDate, event.getEndDate());

                // Verify constraints
                assertTrue(event.getName().length() <= 50);
                assertTrue(event.getLocation().length() <= 50);
                assertTrue(event.getDetails().length() <= 400);
                assertTrue(event.getEndDate().isAfter(event.getStartDate()) ||
                        event.getEndDate().isEqual(event.getStartDate()));
            }
        }

        @Test
        @DisplayName("Should support event date modifications")
        void shouldSupportEventDateModifications() {
            // Given - Event with initial dates
            LocalDate originalStart = LocalDate.of(2024, 6, 15);
            LocalDate originalEnd = LocalDate.of(2024, 6, 17);

            Event event = new Event();
            event.setName("Rally Test");
            event.setStartDate(originalStart);
            event.setEndDate(originalEnd);

            // When - Modify dates (postponement)
            assertEquals(originalStart, event.getStartDate());
            assertEquals(originalEnd, event.getEndDate());

            LocalDate newStart = originalStart.plusDays(7);
            LocalDate newEnd = originalEnd.plusDays(7);

            event.setStartDate(newStart);
            event.setEndDate(newEnd);

            // Then - Verify date modification
            assertEquals(newStart, event.getStartDate());
            assertEquals(newEnd, event.getEndDate());
            assertTrue(event.getStartDate().isAfter(originalStart));
            assertTrue(event.getEndDate().isAfter(originalEnd));
            assertEquals("Rally Test", event.getName()); // Event data unchanged
        }

        @Test
        @DisplayName("Should support event information updates")
        void shouldSupportEventInformationUpdates() {
            // Given - Event with initial information
            Event event = new Event();
            event.setName("Rally Inicial");
            event.setLocation("Ubicación Inicial");
            event.setDetails("Detalles iniciales del evento");

            // When - Update information
            assertEquals("Rally Inicial", event.getName());

            event.setName("Rally Actualizado 2024");
            event.setLocation("Nueva Ubicación, Colombia");
            event.setDetails("Detalles actualizados con nuevas especificaciones técnicas");
            event.setPicture("/images/events/updated_rally.jpg");

            // Then - Verify updates
            assertEquals("Rally Actualizado 2024", event.getName());
            assertEquals("Nueva Ubicación, Colombia", event.getLocation());
            assertEquals("Detalles actualizados con nuevas especificaciones técnicas", event.getDetails());
            assertEquals("/images/events/updated_rally.jpg", event.getPicture());
        }
    }
}
