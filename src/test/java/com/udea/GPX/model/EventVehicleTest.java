package com.udea.GPX.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para EventVehicle Model - Entidad JPA para asociación evento-vehículo
 */
@DisplayName("EventVehicle Model Tests")
class EventVehicleTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create event vehicle with default constructor")
        void shouldCreateEventVehicleWithDefaultConstructor() {
            // When
            EventVehicle eventVehicle = new EventVehicle();

            // Then
            assertNotNull(eventVehicle);
            assertNull(eventVehicle.getId());
            assertNull(eventVehicle.getEvent());
            assertNull(eventVehicle.getVehicleId());
        }

        @Test
        @DisplayName("Should create event vehicle with full constructor")
        void shouldCreateEventVehicleWithFullConstructor() {
            // Given
            Long expectedId = 1L;
            Event expectedEvent = new Event(1L, "Rally Test", "Test Location",
                    "Test Details", LocalDate.now(), LocalDate.now().plusDays(3));

            Category category = new Category(1L, "Rally", "Rally Category");
            User user = new User();
            user.setId(1L);
            user.setFirstName("Test User");

            Vehicle expectedVehicle = new Vehicle(1L, "Toyota Hilux", "SOAT-001", "ABC123", category, user);

            // When
            EventVehicle eventVehicle = new EventVehicle(expectedId, expectedEvent, expectedVehicle);

            // Then
            assertNotNull(eventVehicle);
            assertEquals(expectedId, eventVehicle.getId());
            assertEquals(expectedEvent, eventVehicle.getEvent());
            assertEquals(expectedVehicle, eventVehicle.getVehicleId());
            assertEquals("Rally Test", eventVehicle.getEvent().getName());
            assertEquals("Toyota Hilux", eventVehicle.getVehicleId().getName());
        }

        @Test
        @DisplayName("Should handle null values in full constructor")
        void shouldHandleNullValuesInFullConstructor() {
            // When
            EventVehicle eventVehicle = new EventVehicle(null, null, null);

            // Then
            assertNotNull(eventVehicle);
            assertNull(eventVehicle.getId());
            assertNull(eventVehicle.getEvent());
            assertNull(eventVehicle.getVehicleId());
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
            EventVehicle eventVehicle = new EventVehicle();
            Long expectedId = 100L;

            // When
            eventVehicle.setId(expectedId);

            // Then
            assertEquals(expectedId, eventVehicle.getId());
        }

        @Test
        @DisplayName("Should get and set event correctly")
        void shouldGetAndSetEventCorrectly() {
            // Given
            EventVehicle eventVehicle = new EventVehicle();
            Event expectedEvent = new Event();
            expectedEvent.setId(5L);
            expectedEvent.setName("Enduro Challenge");
            expectedEvent.setLocation("Medellín");

            // When
            eventVehicle.setEvent(expectedEvent);

            // Then
            assertEquals(expectedEvent, eventVehicle.getEvent());
            assertEquals(5L, eventVehicle.getEvent().getId());
            assertEquals("Enduro Challenge", eventVehicle.getEvent().getName());
            assertEquals("Medellín", eventVehicle.getEvent().getLocation());
        }

        @Test
        @DisplayName("Should get and set vehicle correctly")
        void shouldGetAndSetVehicleCorrectly() {
            // Given
            EventVehicle eventVehicle = new EventVehicle();

            Category category = new Category(2L, "Enduro", "Enduro Category");
            User user = new User();
            user.setId(10L);
            user.setFirstName("Carlos");

            Vehicle expectedVehicle = new Vehicle(2L, "Honda CRF450R", "SOAT-002", "END002", category, user);

            // When
            eventVehicle.setVehicleId(expectedVehicle);

            // Then
            assertEquals(expectedVehicle, eventVehicle.getVehicleId());
            assertEquals(2L, eventVehicle.getVehicleId().getId());
            assertEquals("Honda CRF450R", eventVehicle.getVehicleId().getName());
            assertEquals("Carlos", eventVehicle.getVehicleId().getUser().getFirstName());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            // Given
            Event event = new Event(1L, "Test Event", "Test Location", "Test Details",
                    LocalDate.now(), LocalDate.now().plusDays(1));
            Vehicle vehicle = new Vehicle(1L, "Test Vehicle", "SOAT-001", "TEST001",
                    new Category(), new User());
            EventVehicle eventVehicle = new EventVehicle(1L, event, vehicle);

            // When
            eventVehicle.setId(null);
            eventVehicle.setEvent(null);
            eventVehicle.setVehicleId(null);

            // Then
            assertNull(eventVehicle.getId());
            assertNull(eventVehicle.getEvent());
            assertNull(eventVehicle.getVehicleId());
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
            EventVehicle eventVehicle = new EventVehicle();
            Event event1 = new Event();
            event1.setId(1L);
            event1.setName("Rally Nacional");

            Event event2 = new Event();
            event2.setId(2L);
            event2.setName("Enduro Extremo");

            // When - Set initial event
            eventVehicle.setEvent(event1);

            // Then
            assertEquals(event1, eventVehicle.getEvent());
            assertEquals("Rally Nacional", eventVehicle.getEvent().getName());

            // When - Change event
            eventVehicle.setEvent(event2);

            // Then
            assertEquals(event2, eventVehicle.getEvent());
            assertEquals("Enduro Extremo", eventVehicle.getEvent().getName());
        }

        @Test
        @DisplayName("Should maintain vehicle relationship")
        void shouldMaintainVehicleRelationship() {
            // Given
            EventVehicle eventVehicle = new EventVehicle();

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
            eventVehicle.setVehicleId(vehicle1);

            // Then
            assertEquals(vehicle1, eventVehicle.getVehicleId());
            assertEquals("Toyota Hilux", eventVehicle.getVehicleId().getName());
            assertEquals("Juan", eventVehicle.getVehicleId().getUser().getFirstName());

            // When - Change vehicle
            eventVehicle.setVehicleId(vehicle2);

            // Then
            assertEquals(vehicle2, eventVehicle.getVehicleId());
            assertEquals("Ford Ranger", eventVehicle.getVehicleId().getName());
            assertEquals("Pedro", eventVehicle.getVehicleId().getUser().getFirstName());
        }

        @Test
        @DisplayName("Should handle null relationships gracefully")
        void shouldHandleNullRelationshipsGracefully() {
            // Given
            EventVehicle eventVehicle = new EventVehicle();
            Event event = new Event(1L, "Test Event", "Test Location", "Details",
                    LocalDate.now(), LocalDate.now().plusDays(2));
            Vehicle vehicle = new Vehicle(1L, "Test Vehicle", "SOAT-001", "TEST001",
                    new Category(), new User());

            // When - Set relationships then clear them
            eventVehicle.setEvent(event);
            eventVehicle.setVehicleId(vehicle);

            // Then - Verify set
            assertNotNull(eventVehicle.getEvent());
            assertNotNull(eventVehicle.getVehicleId());

            // When - Clear relationships
            eventVehicle.setEvent(null);
            eventVehicle.setVehicleId(null);

            // Then - Verify cleared
            assertNull(eventVehicle.getEvent());
            assertNull(eventVehicle.getVehicleId());
        }

        @Test
        @DisplayName("Should support complete event vehicle with relationships")
        void shouldSupportCompleteEventVehicleWithRelationships() {
            // Given
            Event event = new Event(3L, "Copa Rally Andina", "Manizales",
                    "Competencia en la cordillera",
                    LocalDate.of(2024, 9, 15),
                    LocalDate.of(2024, 9, 17));

            Category category = new Category(3L, "Cross Country", "Long distance competition");
            User user = new User();
            user.setId(15L);
            user.setFirstName("Maria");
            user.setLastName("Rodriguez");

            Vehicle vehicle = new Vehicle(8L, "Jeep Wrangler", "SOAT-CC-008", "CC008", category, user);

            // When
            EventVehicle eventVehicle = new EventVehicle(5L, event, vehicle);

            // Then
            assertEquals(5L, eventVehicle.getId());

            // Verify event relationship
            assertNotNull(eventVehicle.getEvent());
            assertEquals(3L, eventVehicle.getEvent().getId());
            assertEquals("Copa Rally Andina", eventVehicle.getEvent().getName());
            assertEquals("Manizales", eventVehicle.getEvent().getLocation());

            // Verify vehicle relationship
            assertNotNull(eventVehicle.getVehicleId());
            assertEquals(8L, eventVehicle.getVehicleId().getId());
            assertEquals("Jeep Wrangler", eventVehicle.getVehicleId().getName());
            assertEquals("Maria", eventVehicle.getVehicleId().getUser().getFirstName());
            assertEquals("Cross Country", eventVehicle.getVehicleId().getCategory().getName());
        }
    }

    // ========== BUSINESS LOGIC TESTS ==========

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should represent valid event-vehicle registrations")
        void shouldRepresentValidEventVehicleRegistrations() {
            // Given - Different valid registrations
            Object[][] registrations = {
                    { "Rally Nacional", "Toyota Hilux", "Rally", "Juan" },
                    { "Enduro Challenge", "Honda CRF450R", "Enduro", "Carlos" },
                    { "Motocross Cup", "Yamaha YZ250F", "Motocross", "Pedro" },
                    { "4x4 Adventure", "Jeep Wrangler", "4x4", "Ana" },
                    { "Cross Country", "Ford Ranger", "Cross Country", "Luis" }
            };

            for (Object[] registration : registrations) {
                // When
                String eventName = (String) registration[0];
                String vehicleName = (String) registration[1];
                String categoryName = (String) registration[2];
                String ownerName = (String) registration[3];

                Event event = new Event();
                event.setName(eventName);
                event.setLocation("Test Location");

                Category category = new Category();
                category.setName(categoryName);

                User user = new User();
                user.setFirstName(ownerName);

                Vehicle vehicle = new Vehicle();
                vehicle.setName(vehicleName);
                vehicle.setCategory(category);
                vehicle.setUser(user);

                EventVehicle eventVehicle = new EventVehicle(1L, event, vehicle);

                // Then
                assertEquals(eventName, eventVehicle.getEvent().getName());
                assertEquals(vehicleName, eventVehicle.getVehicleId().getName());
                assertEquals(categoryName, eventVehicle.getVehicleId().getCategory().getName());
                assertEquals(ownerName, eventVehicle.getVehicleId().getUser().getFirstName());
            }
        }

        @Test
        @DisplayName("Should support vehicle registration validation")
        void shouldSupportVehicleRegistrationValidation() {
            // Given
            EventVehicle eventVehicle = new EventVehicle();

            // Test rally event with appropriate vehicle
            Event rallyEvent = new Event();
            rallyEvent.setName("Rally Test");
            rallyEvent.setLocation("Test Location");

            Category rallyCategory = new Category();
            rallyCategory.setName("Rally");

            User user = new User();
            user.setFirstName("Test User");

            Vehicle rallyVehicle = new Vehicle();
            rallyVehicle.setName("Toyota Hilux Rally");
            rallyVehicle.setCategory(rallyCategory);
            rallyVehicle.setUser(user);

            // When
            eventVehicle.setEvent(rallyEvent);
            eventVehicle.setVehicleId(rallyVehicle);

            // Then
            assertTrue(eventVehicle.getEvent().getName().contains("Rally"));
            assertTrue(eventVehicle.getVehicleId().getName().contains("Rally") ||
                    eventVehicle.getVehicleId().getCategory().getName().equals("Rally"));
            assertNotNull(eventVehicle.getVehicleId().getUser());
        }

        @Test
        @DisplayName("Should support participant tracking")
        void shouldSupportParticipantTracking() {
            // Given
            Event event = new Event();
            event.setId(10L);
            event.setName("Gran Rally Colombia");

            Category category = new Category(1L, "Rally", "Rally Category");

            User[] participants = {
                    createUser(1L, "Carlos", "Mendez"),
                    createUser(2L, "Ana", "Rodriguez"),
                    createUser(3L, "Pedro", "Silva")
            };

            Vehicle[] vehicles = {
                    new Vehicle(1L, "Toyota Hilux", "SOAT-001", "RLY001", category, participants[0]),
                    new Vehicle(2L, "Ford Ranger", "SOAT-002", "RLY002", category, participants[1]),
                    new Vehicle(3L, "Chevrolet Colorado", "SOAT-003", "RLY003", category, participants[2])
            };

            // When & Then - Create registrations for each participant
            for (int i = 0; i < participants.length; i++) {
                EventVehicle eventVehicle = new EventVehicle((long) (i + 1), event, vehicles[i]);

                assertEquals(event, eventVehicle.getEvent());
                assertEquals(vehicles[i], eventVehicle.getVehicleId());
                assertEquals(participants[i].getFirstName(),
                        eventVehicle.getVehicleId().getUser().getFirstName());
                assertEquals("Gran Rally Colombia", eventVehicle.getEvent().getName());
            }
        }

        private User createUser(Long id, String firstName, String lastName) {
            User user = new User();
            user.setId(id);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            return user;
        }

        @Test
        @DisplayName("Should support event capacity management")
        void shouldSupportEventCapacityManagement() {
            // Given - Event with multiple vehicle registrations
            Event event = new Event();
            event.setId(20L);
            event.setName("Enduro Challenge");
            event.setLocation("Medellín");

            Category enduroCategory = new Category(2L, "Enduro", "Enduro Category");

            // When - Register multiple vehicles
            EventVehicle[] registrations = new EventVehicle[5];
            for (int i = 0; i < 5; i++) {
                User user = new User();
                user.setId((long) (i + 1));
                user.setFirstName("Rider" + (i + 1));

                Vehicle vehicle = new Vehicle();
                vehicle.setId((long) (i + 1));
                vehicle.setName("Bike" + (i + 1));
                vehicle.setCategory(enduroCategory);
                vehicle.setUser(user);

                registrations[i] = new EventVehicle((long) (i + 1), event, vehicle);
            }

            // Then - Verify all registrations
            for (int i = 0; i < registrations.length; i++) {
                assertEquals(event, registrations[i].getEvent());
                assertEquals("Enduro Challenge", registrations[i].getEvent().getName());
                assertEquals("Bike" + (i + 1), registrations[i].getVehicleId().getName());
                assertEquals("Rider" + (i + 1), registrations[i].getVehicleId().getUser().getFirstName());
                assertEquals(enduroCategory, registrations[i].getVehicleId().getCategory());
            }
        }
    }

    // ========== INTEGRATION TESTS ==========

    @Nested
    @DisplayName("Integration and Usage Tests")
    class IntegrationUsageTests {

        @Test
        @DisplayName("Should support complete registration lifecycle")
        void shouldSupportCompleteRegistrationLifecycle() {
            // Given - Create new registration
            EventVehicle eventVehicle = new EventVehicle();

            // When - Set up basic registration (step 1)
            Event event = new Event();
            event.setName("Rally Regional");
            event.setLocation("Antioquia");
            event.setStartDate(LocalDate.now().plusDays(30));
            event.setEndDate(LocalDate.now().plusDays(32));

            Category category = new Category();
            category.setName("Rally");
            category.setDetails("Rally competition category");

            User user = new User();
            user.setFirstName("Miguel");
            user.setLastName("Torres");

            Vehicle vehicle = new Vehicle();
            vehicle.setName("Mitsubishi Montero");
            vehicle.setSoat("SOAT-2024-RLY-010");
            vehicle.setPlates("RLY010");
            vehicle.setCategory(category);
            vehicle.setUser(user);

            eventVehicle.setEvent(event);
            eventVehicle.setVehicleId(vehicle);

            // Then - Verify initial state
            assertNull(eventVehicle.getId());
            assertEquals("Rally Regional", eventVehicle.getEvent().getName());
            assertEquals("Mitsubishi Montero", eventVehicle.getVehicleId().getName());
            assertEquals("Miguel", eventVehicle.getVehicleId().getUser().getFirstName());

            // When - Simulate database save (step 2)
            eventVehicle.setId(25L);

            // Then - Verify complete registration
            assertEquals(25L, eventVehicle.getId());
            assertEquals("Rally Regional", eventVehicle.getEvent().getName());
            assertEquals("Mitsubishi Montero", eventVehicle.getVehicleId().getName());
            assertEquals("Antioquia", eventVehicle.getEvent().getLocation());
        }

        @Test
        @DisplayName("Should support registration updates")
        void shouldSupportRegistrationUpdates() {
            // Given - Initial registration
            Event originalEvent = new Event(1L, "Event 1", "Location 1", "Details 1",
                    LocalDate.now(), LocalDate.now().plusDays(1));
            Vehicle originalVehicle = new Vehicle(1L, "Vehicle 1", "SOAT-001", "VEH001",
                    new Category(), new User());

            EventVehicle eventVehicle = new EventVehicle(1L, originalEvent, originalVehicle);

            // When - Update event
            assertEquals("Event 1", eventVehicle.getEvent().getName());

            Event newEvent = new Event(2L, "Updated Event", "New Location", "New Details",
                    LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));
            eventVehicle.setEvent(newEvent);

            // Then - Verify event update
            assertEquals("Updated Event", eventVehicle.getEvent().getName());
            assertEquals("New Location", eventVehicle.getEvent().getLocation());

            // When - Update vehicle
            assertEquals("Vehicle 1", eventVehicle.getVehicleId().getName());

            Category newCategory = new Category(2L, "New Category", "Category Details");
            User newUser = new User();
            newUser.setFirstName("New User");

            Vehicle newVehicle = new Vehicle(2L, "Updated Vehicle", "SOAT-002", "VEH002",
                    newCategory, newUser);
            eventVehicle.setVehicleId(newVehicle);

            // Then - Verify vehicle update
            assertEquals("Updated Vehicle", eventVehicle.getVehicleId().getName());
            assertEquals("New User", eventVehicle.getVehicleId().getUser().getFirstName());
        }

        @Test
        @DisplayName("Should work with realistic registration scenarios")
        void shouldWorkWithRealisticRegistrationScenarios() {
            // Given - Different realistic event-vehicle registrations
            Object[][] scenarios = {
                    { "Rally Dakar Colombia", "Medellín", "Toyota Hilux Rally", "Rally", "Carlos", "Mendez" },
                    { "Enduro Nacional", "Bogotá", "Honda CRF450R", "Enduro", "Ana", "Rodriguez" },
                    { "Copa Motocross", "Cali", "Yamaha YZ250F", "Motocross", "Pedro", "Silva" },
                    { "Aventura 4x4", "Pereira", "Jeep Wrangler", "4x4", "Maria", "Torres" }
            };

            for (int i = 0; i < scenarios.length; i++) {
                Object[] scenario = scenarios[i];

                // When
                String eventName = (String) scenario[0];
                String eventLocation = (String) scenario[1];
                String vehicleName = (String) scenario[2];
                String categoryName = (String) scenario[3];
                String firstName = (String) scenario[4];
                String lastName = (String) scenario[5];

                Event event = new Event();
                event.setId((long) (i + 1));
                event.setName(eventName);
                event.setLocation(eventLocation);
                event.setStartDate(LocalDate.now().plusDays(i * 15));
                event.setEndDate(LocalDate.now().plusDays(i * 15 + 3));

                Category category = new Category();
                category.setId((long) (i + 1));
                category.setName(categoryName);

                User user = new User();
                user.setId((long) (i + 1));
                user.setFirstName(firstName);
                user.setLastName(lastName);

                Vehicle vehicle = new Vehicle();
                vehicle.setId((long) (i + 1));
                vehicle.setName(vehicleName);
                vehicle.setSoat("SOAT-" + String.format("%03d", i + 1));
                vehicle.setPlates("VEH" + String.format("%03d", i + 1));
                vehicle.setCategory(category);
                vehicle.setUser(user);

                EventVehicle eventVehicle = new EventVehicle((long) (i + 1), event, vehicle);

                // Then
                assertEquals(eventName, eventVehicle.getEvent().getName());
                assertEquals(eventLocation, eventVehicle.getEvent().getLocation());
                assertEquals(vehicleName, eventVehicle.getVehicleId().getName());
                assertEquals(categoryName, eventVehicle.getVehicleId().getCategory().getName());
                assertEquals(firstName, eventVehicle.getVehicleId().getUser().getFirstName());
                assertEquals(lastName, eventVehicle.getVehicleId().getUser().getLastName());

                // Verify relationships are properly maintained
                assertNotNull(eventVehicle.getEvent());
                assertNotNull(eventVehicle.getVehicleId());
                assertEquals((long) (i + 1), eventVehicle.getId());
            }
        }

        @Test
        @DisplayName("Should support registration cancellation and recreation")
        void shouldSupportRegistrationCancellationAndRecreation() {
            // Given - Initial registration
            Event event = new Event(1L, "Test Event", "Test Location", "Details",
                    LocalDate.now().plusDays(30), LocalDate.now().plusDays(32));
            Vehicle vehicle = new Vehicle(1L, "Test Vehicle", "SOAT-001", "TEST001",
                    new Category(), new User());

            EventVehicle eventVehicle = new EventVehicle(1L, event, vehicle);

            // When - Cancel registration (simulate cancellation preparation)
            assertEquals(event, eventVehicle.getEvent());
            assertEquals(vehicle, eventVehicle.getVehicleId());

            eventVehicle.setEvent(null);
            eventVehicle.setVehicleId(null);

            // Then - Verify cancellation
            assertNull(eventVehicle.getEvent());
            assertNull(eventVehicle.getVehicleId());

            // When - Recreate registration (new participant)
            Event newEvent = new Event(2L, "New Event", "New Location", "New Details",
                    LocalDate.now().plusDays(60), LocalDate.now().plusDays(62));

            Category newCategory = new Category(2L, "New Category", "Category Details");
            User newUser = new User();
            newUser.setFirstName("New User");

            Vehicle newVehicle = new Vehicle(2L, "New Vehicle", "SOAT-002", "NEW002",
                    newCategory, newUser);

            eventVehicle.setEvent(newEvent);
            eventVehicle.setVehicleId(newVehicle);

            // Then - Verify recreation
            assertEquals(newEvent, eventVehicle.getEvent());
            assertEquals(newVehicle, eventVehicle.getVehicleId());
            assertEquals("New Event", eventVehicle.getEvent().getName());
            assertEquals("New Vehicle", eventVehicle.getVehicleId().getName());
            assertEquals("New User", eventVehicle.getVehicleId().getUser().getFirstName());
        }
    }
}
