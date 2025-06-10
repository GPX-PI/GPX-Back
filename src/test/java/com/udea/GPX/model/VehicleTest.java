package com.udea.GPX.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para Vehicle Model - Entidad JPA para vehículos de competencia
 */
@DisplayName("Vehicle Model Tests")
class VehicleTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create vehicle with default constructor")
        void shouldCreateVehicleWithDefaultConstructor() {
            // When
            Vehicle vehicle = new Vehicle();

            // Then
            assertNotNull(vehicle);
            assertNull(vehicle.getId());
            assertNull(vehicle.getName());
            assertNull(vehicle.getSoat());
            assertNull(vehicle.getPlates());
            assertNull(vehicle.getCategory());
            assertNull(vehicle.getUser());
        }

        @Test
        @DisplayName("Should create vehicle with full constructor")
        void shouldCreateVehicleWithFullConstructor() {
            // Given
            Long expectedId = 1L;
            String expectedName = "Toyota Hilux Rally";
            String expectedSoat = "SOAT-2024-001";
            String expectedPlates = "ABC123";
            Category expectedCategory = new Category(1L, "Rally", "Rally Category");
            User expectedUser = new User();
            expectedUser.setId(1L);

            // When
            Vehicle vehicle = new Vehicle(expectedId, expectedName, expectedSoat, expectedPlates, expectedCategory,
                    expectedUser);

            // Then
            assertNotNull(vehicle);
            assertEquals(expectedId, vehicle.getId());
            assertEquals(expectedName, vehicle.getName());
            assertEquals(expectedSoat, vehicle.getSoat());
            assertEquals(expectedPlates, vehicle.getPlates());
            assertEquals(expectedCategory, vehicle.getCategory());
            assertEquals(expectedUser, vehicle.getUser());
        }

        @Test
        @DisplayName("Should handle null values in full constructor")
        void shouldHandleNullValuesInFullConstructor() {
            // When & Then
            assertDoesNotThrow(() -> new Vehicle(null, null, null, null, null, null));

            Vehicle vehicleWithNulls = new Vehicle(null, null, null, null, null, null);
            assertNull(vehicleWithNulls.getId());
            assertNull(vehicleWithNulls.getName());
            assertNull(vehicleWithNulls.getSoat());
            assertNull(vehicleWithNulls.getPlates());
            assertNull(vehicleWithNulls.getCategory());
            assertNull(vehicleWithNulls.getUser());
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
            Vehicle vehicle = new Vehicle();
            Long expectedId = 42L;

            // When
            vehicle.setId(expectedId);

            // Then
            assertEquals(expectedId, vehicle.getId());
        }

        @Test
        @DisplayName("Should get and set name correctly")
        void shouldGetAndSetNameCorrectly() {
            // Given
            Vehicle vehicle = new Vehicle();
            String expectedName = "Honda CRF450R";

            // When
            vehicle.setName(expectedName);

            // Then
            assertEquals(expectedName, vehicle.getName());
        }

        @Test
        @DisplayName("Should get and set soat correctly")
        void shouldGetAndSetSoatCorrectly() {
            // Given
            Vehicle vehicle = new Vehicle();
            String expectedSoat = "SOAT-2024-12345";

            // When
            vehicle.setSoat(expectedSoat);

            // Then
            assertEquals(expectedSoat, vehicle.getSoat());
        }

        @Test
        @DisplayName("Should get and set plates correctly")
        void shouldGetAndSetPlatesCorrectly() {
            // Given
            Vehicle vehicle = new Vehicle();
            String expectedPlates = "XYZ789";

            // When
            vehicle.setPlates(expectedPlates);

            // Then
            assertEquals(expectedPlates, vehicle.getPlates());
        }

        @Test
        @DisplayName("Should get and set category correctly")
        void shouldGetAndSetCategoryCorrectly() {
            // Given
            Vehicle vehicle = new Vehicle();
            Category expectedCategory = new Category(2L, "Enduro", "Enduro Category");

            // When
            vehicle.setCategory(expectedCategory);

            // Then
            assertEquals(expectedCategory, vehicle.getCategory());
            assertEquals(2L, vehicle.getCategory().getId());
            assertEquals("Enduro", vehicle.getCategory().getName());
        }

        @Test
        @DisplayName("Should get and set user correctly")
        void shouldGetAndSetUserCorrectly() {
            // Given
            Vehicle vehicle = new Vehicle();
            User expectedUser = new User();
            expectedUser.setId(5L);
            expectedUser.setFirstName("Juan");

            // When
            vehicle.setUser(expectedUser);

            // Then
            assertEquals(expectedUser, vehicle.getUser());
            assertEquals(5L, vehicle.getUser().getId());
            assertEquals("Juan", vehicle.getUser().getFirstName());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            // Given
            Vehicle vehicle = new Vehicle(1L, "Test Vehicle", "SOAT", "ABC123", new Category(), new User());

            // When
            vehicle.setId(null);
            vehicle.setName(null);
            vehicle.setSoat(null);
            vehicle.setPlates(null);
            vehicle.setCategory(null);
            vehicle.setUser(null);

            // Then
            assertNull(vehicle.getId());
            assertNull(vehicle.getName());
            assertNull(vehicle.getSoat());
            assertNull(vehicle.getPlates());
            assertNull(vehicle.getCategory());
            assertNull(vehicle.getUser());
        }
    }

    // ========== BUSINESS LOGIC TESTS ==========

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should support typical vehicle names")
        void shouldSupportTypicalVehicleNames() {
            // Given
            String[] typicalNames = {
                    "Toyota Hilux",
                    "Honda CRF450R",
                    "Yamaha YZ250F",
                    "Suzuki RMZ450",
                    "KTM 450 EXC",
                    "Ford Ranger Raptor",
                    "Chevrolet Colorado"
            };

            for (String name : typicalNames) {
                // When
                Vehicle vehicle = new Vehicle();
                vehicle.setName(name);

                // Then
                assertEquals(name, vehicle.getName());
                assertNotNull(vehicle.getName());
            }
        }

        @Test
        @DisplayName("Should support valid SOAT formats")
        void shouldSupportValidSoatFormats() {
            // Given
            Vehicle vehicle = new Vehicle();
            String[] validSoats = {
                    "SOAT-2024-001",
                    "SOAT2024001",
                    "SO-24-001-RALLY",
                    "12345678901234567890", // Edge case: long SOAT
                    ""
            };

            for (String soat : validSoats) {
                // When
                vehicle.setSoat(soat);

                // Then
                assertEquals(soat, vehicle.getSoat());
                assertTrue(soat.length() <= 100, "SOAT should fit column length constraint");
            }
        }

        @Test
        @DisplayName("Should support valid plate formats")
        void shouldSupportValidPlateFormats() {
            // Given
            Vehicle vehicle = new Vehicle();
            String[] validPlates = {
                    "ABC123",
                    "XYZ789",
                    "DEF456",
                    "GHI789",
                    "JKL012",
                    "1234567890" // Edge case: 10 characters (max)
            };

            for (String plates : validPlates) {
                // When
                vehicle.setPlates(plates);

                // Then
                assertEquals(plates, vehicle.getPlates());
                assertTrue(plates.length() <= 10, "Plates should fit column length constraint");
            }
        }

        @Test
        @DisplayName("Should represent complete vehicle information")
        void shouldRepresentCompleteVehicleInformation() {
            // Given
            Vehicle vehicle = new Vehicle();
            Category category = new Category(1L, "Rally", "Rally Category");
            User user = new User();
            user.setId(1L);
            user.setFirstName("Carlos");

            // When
            vehicle.setId(10L);
            vehicle.setName("Toyota Hilux Rally");
            vehicle.setSoat("SOAT-2024-RALLY-001");
            vehicle.setPlates("RLY001");
            vehicle.setCategory(category);
            vehicle.setUser(user);

            // Then
            assertNotNull(vehicle.getId());
            assertNotNull(vehicle.getName());
            assertNotNull(vehicle.getSoat());
            assertNotNull(vehicle.getPlates());
            assertNotNull(vehicle.getCategory());
            assertNotNull(vehicle.getUser());
            assertEquals("Rally", vehicle.getCategory().getName());
            assertEquals("Carlos", vehicle.getUser().getFirstName());
        }
    }

    // ========== RELATIONSHIP TESTS ==========

    @Nested
    @DisplayName("Relationship Tests")
    class RelationshipTests {

        @Test
        @DisplayName("Should maintain category relationship")
        void shouldMaintainCategoryRelationship() {
            // Given
            Vehicle vehicle = new Vehicle();
            Category rallyCategory = new Category(1L, "Rally", "Rally Category");
            Category enduroCategory = new Category(2L, "Enduro", "Enduro Category");

            // When - Set initial category
            vehicle.setCategory(rallyCategory);

            // Then
            assertEquals(rallyCategory, vehicle.getCategory());
            assertEquals("Rally", vehicle.getCategory().getName());

            // When - Change category
            vehicle.setCategory(enduroCategory);

            // Then
            assertEquals(enduroCategory, vehicle.getCategory());
            assertEquals("Enduro", vehicle.getCategory().getName());
        }

        @Test
        @DisplayName("Should maintain user relationship")
        void shouldMaintainUserRelationship() {
            // Given
            Vehicle vehicle = new Vehicle();
            User user1 = new User();
            user1.setId(1L);
            user1.setFirstName("Juan");

            User user2 = new User();
            user2.setId(2L);
            user2.setFirstName("Pedro");

            // When - Set initial user
            vehicle.setUser(user1);

            // Then
            assertEquals(user1, vehicle.getUser());
            assertEquals("Juan", vehicle.getUser().getFirstName());

            // When - Change user
            vehicle.setUser(user2);

            // Then
            assertEquals(user2, vehicle.getUser());
            assertEquals("Pedro", vehicle.getUser().getFirstName());
        }

        @Test
        @DisplayName("Should handle null relationships gracefully")
        void shouldHandleNullRelationshipsGracefully() {
            // Given
            Vehicle vehicle = new Vehicle();
            Category category = new Category(1L, "Rally", "Rally Category");
            User user = new User();
            user.setId(1L);

            // When - Set relationships then clear them
            vehicle.setCategory(category);
            vehicle.setUser(user);

            // Then - Verify set
            assertNotNull(vehicle.getCategory());
            assertNotNull(vehicle.getUser());

            // When - Clear relationships
            vehicle.setCategory(null);
            vehicle.setUser(null);

            // Then - Verify cleared
            assertNull(vehicle.getCategory());
            assertNull(vehicle.getUser());
        }

        @Test
        @DisplayName("Should support complete vehicle with relationships")
        void shouldSupportCompleteVehicleWithRelationships() {
            // Given
            Category category = new Category(3L, "4x4", "4x4 Off-road Category");
            User owner = new User();
            owner.setId(10L);
            owner.setFirstName("Maria");
            owner.setLastName("Rodriguez");

            // When
            Vehicle vehicle = new Vehicle(
                    5L,
                    "Jeep Wrangler Sport",
                    "SOAT-2024-4X4-005",
                    "4X4005",
                    category,
                    owner);

            // Then
            assertEquals(5L, vehicle.getId());
            assertEquals("Jeep Wrangler Sport", vehicle.getName());
            assertEquals("SOAT-2024-4X4-005", vehicle.getSoat());
            assertEquals("4X4005", vehicle.getPlates());

            // Verify category relationship
            assertNotNull(vehicle.getCategory());
            assertEquals(3L, vehicle.getCategory().getId());
            assertEquals("4x4", vehicle.getCategory().getName());

            // Verify user relationship
            assertNotNull(vehicle.getUser());
            assertEquals(10L, vehicle.getUser().getId());
            assertEquals("Maria", vehicle.getUser().getFirstName());
            assertEquals("Rodriguez", vehicle.getUser().getLastName());
        }
    }

    // ========== FIELD VALIDATION TESTS ==========

    @Nested
    @DisplayName("Field Validation Tests")
    class FieldValidationTests {

        @Test
        @DisplayName("Should handle edge case field lengths")
        void shouldHandleEdgeCaseFieldLengths() {
            // Given
            Vehicle vehicle = new Vehicle();
            String maxSoat = "A".repeat(100); // 100 characters (max)
            String maxPlates = "1234567890"; // 10 characters (max)

            // When
            vehicle.setSoat(maxSoat);
            vehicle.setPlates(maxPlates);

            // Then
            assertEquals(maxSoat, vehicle.getSoat());
            assertEquals(100, vehicle.getSoat().length());
            assertEquals(maxPlates, vehicle.getPlates());
            assertEquals(10, vehicle.getPlates().length());
        }

        @Test
        @DisplayName("Should handle special characters in fields")
        void shouldHandleSpecialCharactersInFields() {
            // Given
            Vehicle vehicle = new Vehicle();
            String nameWithSpecialChars = "Honda CRF-450R Rally/Cross";
            String soatWithSpecialChars = "SOAT-2024/001-GPX";
            String platesWithSpecialChars = "ABC-123";

            // When
            vehicle.setName(nameWithSpecialChars);
            vehicle.setSoat(soatWithSpecialChars);
            vehicle.setPlates(platesWithSpecialChars);

            // Then
            assertEquals(nameWithSpecialChars, vehicle.getName());
            assertEquals(soatWithSpecialChars, vehicle.getSoat());
            assertEquals(platesWithSpecialChars, vehicle.getPlates());
        }

        @Test
        @DisplayName("Should handle empty string values")
        void shouldHandleEmptyStringValues() {
            // Given
            Vehicle vehicle = new Vehicle();

            // When
            vehicle.setName("");
            vehicle.setSoat("");
            vehicle.setPlates("");

            // Then
            assertEquals("", vehicle.getName());
            assertEquals("", vehicle.getSoat());
            assertEquals("", vehicle.getPlates());
        }
    }

    // ========== INTEGRATION TESTS ==========

    @Nested
    @DisplayName("Integration and Usage Tests")
    class IntegrationUsageTests {

        @Test
        @DisplayName("Should support complete vehicle lifecycle")
        void shouldSupportCompleteVehicleLifecycle() {
            // Given - Create new vehicle
            Vehicle vehicle = new Vehicle();

            // When - Set up vehicle (step 1)
            vehicle.setName("Kawasaki KX450F");
            vehicle.setSoat("SOAT-2024-MX-010");
            vehicle.setPlates("MX010");

            // Then - Verify initial state
            assertNull(vehicle.getId());
            assertEquals("Kawasaki KX450F", vehicle.getName());
            assertEquals("SOAT-2024-MX-010", vehicle.getSoat());
            assertEquals("MX010", vehicle.getPlates());
            assertNull(vehicle.getCategory());
            assertNull(vehicle.getUser());

            // When - Add relationships (step 2)
            Category motocrossCategory = new Category(4L, "Motocross", "Motocross Category");
            User rider = new User();
            rider.setId(15L);
            rider.setFirstName("Diego");

            vehicle.setCategory(motocrossCategory);
            vehicle.setUser(rider);

            // Then - Verify with relationships
            assertEquals("Kawasaki KX450F", vehicle.getName());
            assertEquals(motocrossCategory, vehicle.getCategory());
            assertEquals(rider, vehicle.getUser());

            // When - Simulate database save (step 3)
            vehicle.setId(25L);

            // Then - Verify complete vehicle
            assertEquals(25L, vehicle.getId());
            assertEquals("Kawasaki KX450F", vehicle.getName());
            assertEquals("SOAT-2024-MX-010", vehicle.getSoat());
            assertEquals("MX010", vehicle.getPlates());
            assertEquals("Motocross", vehicle.getCategory().getName());
            assertEquals("Diego", vehicle.getUser().getFirstName());
        }

        @Test
        @DisplayName("Should work with realistic vehicle scenarios")
        void shouldWorkWithRealisticVehicleScenarios() {
            // Given - Different vehicle types
            Object[][] vehicleScenarios = {
                    { "Toyota Hilux Rally", "SOAT-2024-RLY-001", "RLY001", "Rally" },
                    { "Honda CRF450R", "SOAT-2024-END-002", "END002", "Enduro" },
                    { "Yamaha YZ250F", "SOAT-2024-MX-003", "MX003", "Motocross" },
                    { "Jeep Wrangler", "SOAT-2024-4X4-004", "4X4004", "4x4" },
                    { "KTM 450 EXC", "SOAT-2024-CC-005", "CC005", "Cross Country" }
            };

            for (Object[] scenario : vehicleScenarios) {
                // When
                String name = (String) scenario[0];
                String soat = (String) scenario[1];
                String plates = (String) scenario[2];
                String categoryName = (String) scenario[3];

                Category category = new Category(1L, categoryName, categoryName + " Category");
                User owner = new User();
                owner.setId(1L);
                owner.setFirstName("Test User");

                Vehicle vehicle = new Vehicle(1L, name, soat, plates, category, owner);

                // Then
                assertEquals(name, vehicle.getName());
                assertEquals(soat, vehicle.getSoat());
                assertEquals(plates, vehicle.getPlates());
                assertEquals(categoryName, vehicle.getCategory().getName());
                assertEquals("Test User", vehicle.getUser().getFirstName());

                // Verify constraints
                assertTrue(vehicle.getSoat().length() <= 100);
                assertTrue(vehicle.getPlates().length() <= 10);
            }
        }

        @Test
        @DisplayName("Should support vehicle ownership transfer")
        void shouldSupportVehicleOwnershipTransfer() {
            // Given - Vehicle with initial owner
            User originalOwner = new User();
            originalOwner.setId(1L);
            originalOwner.setFirstName("Carlos");

            User newOwner = new User();
            newOwner.setId(2L);
            newOwner.setFirstName("Ana");

            Vehicle vehicle = new Vehicle();
            vehicle.setId(10L);
            vehicle.setName("Suzuki RMZ450");
            vehicle.setUser(originalOwner);

            // When - Transfer ownership
            assertEquals("Carlos", vehicle.getUser().getFirstName());

            vehicle.setUser(newOwner);

            // Then - Verify transfer
            assertEquals("Ana", vehicle.getUser().getFirstName());
            assertEquals(2L, vehicle.getUser().getId());
            assertEquals("Suzuki RMZ450", vehicle.getName()); // Vehicle data unchanged
        }

        @Test
        @DisplayName("Should support vehicle category changes")
        void shouldSupportVehicleCategoryChanges() {
            // Given - Vehicle in one category
            Category originalCategory = new Category(1L, "Rally", "Rally Category");
            Category newCategory = new Category(2L, "Cross Country", "Cross Country Category");

            Vehicle vehicle = new Vehicle();
            vehicle.setName("Ford Ranger");
            vehicle.setCategory(originalCategory);

            // When - Change category (vehicle modification)
            assertEquals("Rally", vehicle.getCategory().getName());

            vehicle.setCategory(newCategory);

            // Then - Verify category change
            assertEquals("Cross Country", vehicle.getCategory().getName());
            assertEquals(2L, vehicle.getCategory().getId());
            assertEquals("Ford Ranger", vehicle.getName()); // Vehicle data unchanged
        }
    }
}
