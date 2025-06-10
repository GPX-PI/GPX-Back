package com.udea.GPX.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para EventCategory Model - Entidad JPA para asociación evento-categoría
 */
@DisplayName("EventCategory Model Tests")
class EventCategoryTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create event category with default constructor")
        void shouldCreateEventCategoryWithDefaultConstructor() {
            // When
            EventCategory eventCategory = new EventCategory();

            // Then
            assertNotNull(eventCategory);
            assertNull(eventCategory.getId());
            assertNull(eventCategory.getEvent());
            assertNull(eventCategory.getCategory());
        }

        @Test
        @DisplayName("Should create event category with full constructor")
        void shouldCreateEventCategoryWithFullConstructor() {
            // Given
            Long expectedId = 1L;
            Event expectedEvent = new Event(1L, "Rally Test", "Test Location",
                    "Test Details", LocalDate.now(), LocalDate.now().plusDays(3));
            Category expectedCategory = new Category(1L, "Rally", "Rally Category");

            // When
            EventCategory eventCategory = new EventCategory(expectedId, expectedEvent, expectedCategory);

            // Then
            assertNotNull(eventCategory);
            assertEquals(expectedId, eventCategory.getId());
            assertEquals(expectedEvent, eventCategory.getEvent());
            assertEquals(expectedCategory, eventCategory.getCategory());
            assertEquals("Rally Test", eventCategory.getEvent().getName());
            assertEquals("Rally", eventCategory.getCategory().getName());
        }

        @Test
        @DisplayName("Should handle null values in full constructor")
        void shouldHandleNullValuesInFullConstructor() {
            // When
            EventCategory eventCategory = new EventCategory(null, null, null);

            // Then
            assertNotNull(eventCategory);
            assertNull(eventCategory.getId());
            assertNull(eventCategory.getEvent());
            assertNull(eventCategory.getCategory());
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
            EventCategory eventCategory = new EventCategory();
            Long expectedId = 100L;

            // When
            eventCategory.setId(expectedId);

            // Then
            assertEquals(expectedId, eventCategory.getId());
        }

        @Test
        @DisplayName("Should get and set event correctly")
        void shouldGetAndSetEventCorrectly() {
            // Given
            EventCategory eventCategory = new EventCategory();
            Event expectedEvent = new Event();
            expectedEvent.setId(5L);
            expectedEvent.setName("Enduro Challenge");
            expectedEvent.setLocation("Medellín");

            // When
            eventCategory.setEvent(expectedEvent);

            // Then
            assertEquals(expectedEvent, eventCategory.getEvent());
            assertEquals(5L, eventCategory.getEvent().getId());
            assertEquals("Enduro Challenge", eventCategory.getEvent().getName());
            assertEquals("Medellín", eventCategory.getEvent().getLocation());
        }

        @Test
        @DisplayName("Should get and set category correctly")
        void shouldGetAndSetCategoryCorrectly() {
            // Given
            EventCategory eventCategory = new EventCategory();
            Category expectedCategory = new Category(2L, "Motocross", "Motocross Category");

            // When
            eventCategory.setCategory(expectedCategory);

            // Then
            assertEquals(expectedCategory, eventCategory.getCategory());
            assertEquals(2L, eventCategory.getCategory().getId());
            assertEquals("Motocross", eventCategory.getCategory().getName());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            // Given
            Event event = new Event(1L, "Test Event", "Test Location", "Test Details",
                    LocalDate.now(), LocalDate.now().plusDays(1));
            Category category = new Category(1L, "Test Category", "Test Details");
            EventCategory eventCategory = new EventCategory(1L, event, category);

            // When
            eventCategory.setId(null);
            eventCategory.setEvent(null);
            eventCategory.setCategory(null);

            // Then
            assertNull(eventCategory.getId());
            assertNull(eventCategory.getEvent());
            assertNull(eventCategory.getCategory());
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
            EventCategory eventCategory = new EventCategory();
            Event event1 = new Event();
            event1.setId(1L);
            event1.setName("Rally Nacional");

            Event event2 = new Event();
            event2.setId(2L);
            event2.setName("Enduro Extremo");

            // When - Set initial event
            eventCategory.setEvent(event1);

            // Then
            assertEquals(event1, eventCategory.getEvent());
            assertEquals("Rally Nacional", eventCategory.getEvent().getName());

            // When - Change event
            eventCategory.setEvent(event2);

            // Then
            assertEquals(event2, eventCategory.getEvent());
            assertEquals("Enduro Extremo", eventCategory.getEvent().getName());
        }

        @Test
        @DisplayName("Should maintain category relationship")
        void shouldMaintainCategoryRelationship() {
            // Given
            EventCategory eventCategory = new EventCategory();
            Category category1 = new Category(1L, "Rally", "Rally Category");
            Category category2 = new Category(2L, "Enduro", "Enduro Category");

            // When - Set initial category
            eventCategory.setCategory(category1);

            // Then
            assertEquals(category1, eventCategory.getCategory());
            assertEquals("Rally", eventCategory.getCategory().getName());

            // When - Change category
            eventCategory.setCategory(category2);

            // Then
            assertEquals(category2, eventCategory.getCategory());
            assertEquals("Enduro", eventCategory.getCategory().getName());
        }

        @Test
        @DisplayName("Should handle null relationships gracefully")
        void shouldHandleNullRelationshipsGracefully() {
            // Given
            EventCategory eventCategory = new EventCategory();
            Event event = new Event(1L, "Test Event", "Test Location", "Details",
                    LocalDate.now(), LocalDate.now().plusDays(2));
            Category category = new Category(1L, "Test Category", "Category Details");

            // When - Set relationships then clear them
            eventCategory.setEvent(event);
            eventCategory.setCategory(category);

            // Then - Verify set
            assertNotNull(eventCategory.getEvent());
            assertNotNull(eventCategory.getCategory());

            // When - Clear relationships
            eventCategory.setEvent(null);
            eventCategory.setCategory(null);

            // Then - Verify cleared
            assertNull(eventCategory.getEvent());
            assertNull(eventCategory.getCategory());
        }

        @Test
        @DisplayName("Should support complete event category with relationships")
        void shouldSupportCompleteEventCategoryWithRelationships() {
            // Given
            Event event = new Event(3L, "Copa Rally Andina", "Manizales",
                    "Competencia en la cordillera",
                    LocalDate.of(2024, 9, 15),
                    LocalDate.of(2024, 9, 17));

            Category category = new Category(3L, "Cross Country", "Long distance competition");

            // When
            EventCategory eventCategory = new EventCategory(5L, event, category);

            // Then
            assertEquals(5L, eventCategory.getId());

            // Verify event relationship
            assertNotNull(eventCategory.getEvent());
            assertEquals(3L, eventCategory.getEvent().getId());
            assertEquals("Copa Rally Andina", eventCategory.getEvent().getName());
            assertEquals("Manizales", eventCategory.getEvent().getLocation());

            // Verify category relationship
            assertNotNull(eventCategory.getCategory());
            assertEquals(3L, eventCategory.getCategory().getId());
            assertEquals("Cross Country", eventCategory.getCategory().getName());
        }
    }

    // ========== BUSINESS LOGIC TESTS ==========

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should represent valid event-category associations")
        void shouldRepresentValidEventCategoryAssociations() {
            // Given - Different valid associations
            Object[][] associations = {
                    { "Rally Nacional", "Rally", "Association for rally events" },
                    { "Enduro Challenge", "Enduro", "Association for enduro events" },
                    { "Motocross Cup", "Motocross", "Association for motocross events" },
                    { "4x4 Adventure", "4x4", "Association for off-road events" },
                    { "Cross Country", "Cross Country", "Association for long distance events" }
            };

            for (Object[] association : associations) {
                // When
                String eventName = (String) association[0];
                String categoryName = (String) association[1];
                String description = (String) association[2];

                Event event = new Event();
                event.setName(eventName);
                event.setLocation("Test Location");
                event.setDetails(description);

                Category category = new Category();
                category.setName(categoryName);
                category.setDetails(description);

                EventCategory eventCategory = new EventCategory(1L, event, category);

                // Then
                assertEquals(eventName, eventCategory.getEvent().getName());
                assertEquals(categoryName, eventCategory.getCategory().getName());
                assertNotNull(eventCategory.getEvent());
                assertNotNull(eventCategory.getCategory());
            }
        }

        @Test
        @DisplayName("Should support multiple categories per event scenario")
        void shouldSupportMultipleCategoriesPerEventScenario() {
            // Given - One event with multiple possible categories
            Event rallyEvent = new Event();
            rallyEvent.setId(10L);
            rallyEvent.setName("Gran Rally Colombia");
            rallyEvent.setLocation("Multiple Cities");

            Category[] categories = {
                    new Category(1L, "Rally", "Rally Category"),
                    new Category(2L, "Cross Country", "Cross Country Category"),
                    new Category(3L, "4x4", "4x4 Category")
            };

            // When & Then - Create associations for each category
            for (int i = 0; i < categories.length; i++) {
                EventCategory eventCategory = new EventCategory((long) (i + 1), rallyEvent, categories[i]);

                assertEquals(rallyEvent, eventCategory.getEvent());
                assertEquals(categories[i], eventCategory.getCategory());
                assertEquals("Gran Rally Colombia", eventCategory.getEvent().getName());
                assertEquals(categories[i].getName(), eventCategory.getCategory().getName());
            }
        }

        @Test
        @DisplayName("Should support category validation for events")
        void shouldSupportCategoryValidationForEvents() {
            // Given
            EventCategory eventCategory = new EventCategory();

            // Test rally event with rally category
            Event rallyEvent = new Event();
            rallyEvent.setName("Rally Test");
            Category rallyCategory = new Category();
            rallyCategory.setName("Rally");

            // When
            eventCategory.setEvent(rallyEvent);
            eventCategory.setCategory(rallyCategory);

            // Then
            assertTrue(eventCategory.getEvent().getName().contains("Rally"));
            assertTrue(eventCategory.getCategory().getName().equals("Rally"));

            // Test motocross event with motocross category
            Event motocrossEvent = new Event();
            motocrossEvent.setName("Motocross Championship");
            Category motocrossCategory = new Category();
            motocrossCategory.setName("Motocross");

            // When
            eventCategory.setEvent(motocrossEvent);
            eventCategory.setCategory(motocrossCategory);

            // Then
            assertTrue(eventCategory.getEvent().getName().contains("Motocross"));
            assertTrue(eventCategory.getCategory().getName().equals("Motocross"));
        }
    }

    // ========== INTEGRATION TESTS ==========

    @Nested
    @DisplayName("Integration and Usage Tests")
    class IntegrationUsageTests {

        @Test
        @DisplayName("Should support complete association lifecycle")
        void shouldSupportCompleteAssociationLifecycle() {
            // Given - Create new association
            EventCategory eventCategory = new EventCategory();

            // When - Set up basic association (step 1)
            Event event = new Event();
            event.setName("Rally Regional");
            event.setLocation("Antioquia");

            Category category = new Category();
            category.setName("Rally");
            category.setDetails("Rally competition category");

            eventCategory.setEvent(event);
            eventCategory.setCategory(category);

            // Then - Verify initial state
            assertNull(eventCategory.getId());
            assertEquals("Rally Regional", eventCategory.getEvent().getName());
            assertEquals("Rally", eventCategory.getCategory().getName());

            // When - Simulate database save (step 2)
            eventCategory.setId(25L);

            // Then - Verify complete association
            assertEquals(25L, eventCategory.getId());
            assertEquals("Rally Regional", eventCategory.getEvent().getName());
            assertEquals("Rally", eventCategory.getCategory().getName());
            assertEquals("Antioquia", eventCategory.getEvent().getLocation());
        }

        @Test
        @DisplayName("Should support association updates")
        void shouldSupportAssociationUpdates() {
            // Given - Initial association
            Event originalEvent = new Event(1L, "Event 1", "Location 1", "Details 1",
                    LocalDate.now(), LocalDate.now().plusDays(1));
            Category originalCategory = new Category(1L, "Category 1", "Details 1");

            EventCategory eventCategory = new EventCategory(1L, originalEvent, originalCategory);

            // When - Update event
            assertEquals("Event 1", eventCategory.getEvent().getName());

            Event newEvent = new Event(2L, "Updated Event", "New Location", "New Details",
                    LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));
            eventCategory.setEvent(newEvent);

            // Then - Verify event update
            assertEquals("Updated Event", eventCategory.getEvent().getName());
            assertEquals("New Location", eventCategory.getEvent().getLocation());

            // When - Update category
            assertEquals("Category 1", eventCategory.getCategory().getName());

            Category newCategory = new Category(2L, "Updated Category", "New Category Details");
            eventCategory.setCategory(newCategory);

            // Then - Verify category update
            assertEquals("Updated Category", eventCategory.getCategory().getName());
            assertEquals("New Category Details", eventCategory.getCategory().getDetails());
        }

        @Test
        @DisplayName("Should work with realistic association scenarios")
        void shouldWorkWithRealisticAssociationScenarios() {
            // Given - Different realistic event-category combinations
            Object[][] scenarios = {
                    { "Rally Dakar Colombia", "Medellín", "Rally", "Long distance rally competition" },
                    { "Enduro Nacional", "Bogotá", "Enduro", "National enduro championship" },
                    { "Copa Motocross", "Cali", "Motocross", "Motocross cup competition" },
                    { "Aventura 4x4", "Pereira", "4x4", "Off-road adventure event" }
            };

            for (int i = 0; i < scenarios.length; i++) {
                Object[] scenario = scenarios[i];

                // When
                String eventName = (String) scenario[0];
                String eventLocation = (String) scenario[1];
                String categoryName = (String) scenario[2];
                String categoryDetails = (String) scenario[3];

                Event event = new Event();
                event.setId((long) (i + 1));
                event.setName(eventName);
                event.setLocation(eventLocation);
                event.setStartDate(LocalDate.now().plusDays(i * 10));
                event.setEndDate(LocalDate.now().plusDays(i * 10 + 2));

                Category category = new Category();
                category.setId((long) (i + 1));
                category.setName(categoryName);
                category.setDetails(categoryDetails);

                EventCategory eventCategory = new EventCategory((long) (i + 1), event, category);

                // Then
                assertEquals(eventName, eventCategory.getEvent().getName());
                assertEquals(eventLocation, eventCategory.getEvent().getLocation());
                assertEquals(categoryName, eventCategory.getCategory().getName());
                assertEquals(categoryDetails, eventCategory.getCategory().getDetails());

                // Verify relationships are properly maintained
                assertNotNull(eventCategory.getEvent());
                assertNotNull(eventCategory.getCategory());
                assertEquals((long) (i + 1), eventCategory.getId());
            }
        }

        @Test
        @DisplayName("Should support association removal and recreation")
        void shouldSupportAssociationRemovalAndRecreation() {
            // Given - Initial association
            Event event = new Event(1L, "Test Event", "Test Location", "Details",
                    LocalDate.now(), LocalDate.now().plusDays(3));
            Category category = new Category(1L, "Test Category", "Category Details");

            EventCategory eventCategory = new EventCategory(1L, event, category);

            // When - Remove relationships (simulate deletion preparation)
            assertEquals(event, eventCategory.getEvent());
            assertEquals(category, eventCategory.getCategory());

            eventCategory.setEvent(null);
            eventCategory.setCategory(null);

            // Then - Verify removal
            assertNull(eventCategory.getEvent());
            assertNull(eventCategory.getCategory());

            // When - Recreate association
            Event newEvent = new Event(2L, "New Event", "New Location", "New Details",
                    LocalDate.now().plusDays(10), LocalDate.now().plusDays(12));
            Category newCategory = new Category(2L, "New Category", "New Category Details");

            eventCategory.setEvent(newEvent);
            eventCategory.setCategory(newCategory);

            // Then - Verify recreation
            assertEquals(newEvent, eventCategory.getEvent());
            assertEquals(newCategory, eventCategory.getCategory());
            assertEquals("New Event", eventCategory.getEvent().getName());
            assertEquals("New Category", eventCategory.getCategory().getName());
        }
    }
}
