package com.udea.GPX.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para Category Model - Entidad JPA para categorías de vehículos
 */
@DisplayName("Category Model Tests")
class CategoryTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create category with default constructor")
        void shouldCreateCategoryWithDefaultConstructor() {
            // When
            Category category = new Category();

            // Then
            assertNotNull(category);
            assertNull(category.getId());
            assertNull(category.getName());
            assertNull(category.getDetails());
        }

        @Test
        @DisplayName("Should create category with id constructor")
        void shouldCreateCategoryWithIdConstructor() {
            // Given
            Long expectedId = 1L;

            // When
            Category category = new Category(expectedId);

            // Then
            assertNotNull(category);
            assertEquals(expectedId, category.getId());
            assertNull(category.getName());
            assertNull(category.getDetails());
        }

        @Test
        @DisplayName("Should create category with full constructor")
        void shouldCreateCategoryWithFullConstructor() {
            // Given
            Long expectedId = 1L;
            String expectedName = "Rally";
            String expectedDetails = "Categoría para vehículos de rally con modificaciones específicas";

            // When
            Category category = new Category(expectedId, expectedName, expectedDetails);

            // Then
            assertNotNull(category);
            assertEquals(expectedId, category.getId());
            assertEquals(expectedName, category.getName());
            assertEquals(expectedDetails, category.getDetails());
        }

        @Test
        @DisplayName("Should handle null values in constructors")
        void shouldHandleNullValuesInConstructors() {
            // When & Then
            assertDoesNotThrow(() -> new Category(null));
            assertDoesNotThrow(() -> new Category(null, null, null));

            Category categoryWithNulls = new Category(null, null, null);
            assertNull(categoryWithNulls.getId());
            assertNull(categoryWithNulls.getName());
            assertNull(categoryWithNulls.getDetails());
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
            Category category = new Category();
            Long expectedId = 42L;

            // When
            category.setId(expectedId);

            // Then
            assertEquals(expectedId, category.getId());
        }

        @Test
        @DisplayName("Should get and set name correctly")
        void shouldGetAndSetNameCorrectly() {
            // Given
            Category category = new Category();
            String expectedName = "Enduro";

            // When
            category.setName(expectedName);

            // Then
            assertEquals(expectedName, category.getName());
        }

        @Test
        @DisplayName("Should get and set details correctly")
        void shouldGetAndSetDetailsCorrectly() {
            // Given
            Category category = new Category();
            String expectedDetails = "Categoría para vehículos de enduro con suspensión especial";

            // When
            category.setDetails(expectedDetails);

            // Then
            assertEquals(expectedDetails, category.getDetails());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            // Given
            Category category = new Category(1L, "Test", "Test Details");

            // When
            category.setId(null);
            category.setName(null);
            category.setDetails(null);

            // Then
            assertNull(category.getId());
            assertNull(category.getName());
            assertNull(category.getDetails());
        }
    }

    // ========== BUSINESS LOGIC TESTS ==========

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should support typical category names")
        void shouldSupportTypicalCategoryNames() {
            // Given
            String[] typicalNames = { "Rally", "Enduro", "Cross Country", "Speed", "4x4", "Motocross" };

            for (String name : typicalNames) {
                // When
                Category category = new Category();
                category.setName(name);

                // Then
                assertEquals(name, category.getName());
                assertTrue(name.length() <= 20, "Name should fit column length constraint");
            }
        }

        @Test
        @DisplayName("Should support detailed descriptions")
        void shouldSupportDetailedDescriptions() {
            // Given
            Category category = new Category();
            String longDetails = "Esta categoría está diseñada para vehículos todo terreno con modificaciones específicas para competencia. Incluye requisitos de seguridad, modificaciones permitidas y restricciones técnicas. Los vehículos deben cumplir con normativas internacionales de rally.";

            // When
            category.setDetails(longDetails);

            // Then
            assertEquals(longDetails, category.getDetails());
            assertTrue(longDetails.length() <= 400, "Details should fit column length constraint");
        }

        @Test
        @DisplayName("Should represent valid category data")
        void shouldRepresentValidCategoryData() {
            // Given
            Category category = new Category();
            category.setId(5L);
            category.setName("Rally");
            category.setDetails("Categoría especializada para vehículos de rally");

            // When & Then
            assertNotNull(category.getId());
            assertNotNull(category.getName());
            assertNotNull(category.getDetails());
            assertTrue(category.getId() > 0);
            assertFalse(category.getName().trim().isEmpty());
            assertFalse(category.getDetails().trim().isEmpty());
        }
    }

    // ========== ENTITY BEHAVIOR TESTS ==========

    @Nested
    @DisplayName("Entity Behavior Tests")
    class EntityBehaviorTests {

        @Test
        @DisplayName("Should maintain data integrity after multiple operations")
        void shouldMaintainDataIntegrityAfterMultipleOperations() {
            // Given
            Category category = new Category();

            // When - Multiple operations
            category.setId(1L);
            category.setName("Initial Name");
            category.setDetails("Initial Details");

            // Modify values
            category.setId(2L);
            category.setName("Updated Name");
            category.setDetails("Updated Details");

            // Then
            assertEquals(2L, category.getId());
            assertEquals("Updated Name", category.getName());
            assertEquals("Updated Details", category.getDetails());
        }

        @Test
        @DisplayName("Should handle edge case values")
        void shouldHandleEdgeCaseValues() {
            // Given
            Category category = new Category();

            // When & Then - Edge cases
            category.setId(0L);
            assertEquals(0L, category.getId());

            category.setId(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, category.getId());

            category.setName("");
            assertEquals("", category.getName());

            category.setName(" ");
            assertEquals(" ", category.getName());

            category.setDetails("");
            assertEquals("", category.getDetails());
        }

        @Test
        @DisplayName("Should support category creation workflow")
        void shouldSupportCategoryCreationWorkflow() {
            // Given - Simulate typical category creation workflow
            Category category = new Category();

            // When - Step 1: Set basic info
            category.setName("Cross Country");
            category.setDetails("Categoría para competencias de larga distancia");

            // Then - Verify step 1
            assertNull(category.getId()); // ID not set yet (would be set by database)
            assertEquals("Cross Country", category.getName());
            assertEquals("Categoría para competencias de larga distancia", category.getDetails());

            // When - Step 2: Simulate database save (ID assignment)
            category.setId(10L);

            // Then - Verify step 2
            assertEquals(10L, category.getId());
            assertEquals("Cross Country", category.getName());
            assertEquals("Categoría para competencias de larga distancia", category.getDetails());
        }
    }

    // ========== FIELD VALIDATION TESTS ==========

    @Nested
    @DisplayName("Field Validation Tests")
    class FieldValidationTests {

        @Test
        @DisplayName("Should accept valid name lengths")
        void shouldAcceptValidNameLengths() {
            // Given
            Category category = new Category();
            String[] validNames = {
                    "A", // 1 character
                    "Rally", // 5 characters
                    "Cross Country Race", // 19 characters
                    "12345678901234567890" // 20 characters (max)
            };

            for (String name : validNames) {
                // When
                category.setName(name);

                // Then
                assertEquals(name, category.getName());
                assertTrue(name.length() <= 20, "Name length should be within database constraint");
            }
        }

        @Test
        @DisplayName("Should accept valid details lengths")
        void shouldAcceptValidDetailsLengths() {
            // Given
            Category category = new Category();
            String shortDetails = "Corta descripción";
            String maxDetails = "A".repeat(400); // 400 characters (max)

            // When & Then
            category.setDetails(shortDetails);
            assertEquals(shortDetails, category.getDetails());

            category.setDetails(maxDetails);
            assertEquals(maxDetails, category.getDetails());
            assertEquals(400, category.getDetails().length());
        }

        @Test
        @DisplayName("Should handle special characters in fields")
        void shouldHandleSpecialCharactersInFields() {
            // Given
            Category category = new Category();
            String specialName = "4x4 & Rally";
            String specialDetails = "Categoría para vehículos 4x4 & rally - incluye: suspensión, neumáticos, etc.";

            // When
            category.setName(specialName);
            category.setDetails(specialDetails);

            // Then
            assertEquals(specialName, category.getName());
            assertEquals(specialDetails, category.getDetails());
        }
    }

    // ========== INTEGRATION TESTS ==========

    @Nested
    @DisplayName("Integration and Usage Tests")
    class IntegrationUsageTests {

        @Test
        @DisplayName("Should support complete category lifecycle")
        void shouldSupportCompleteCategoryLifecycle() {
            // Given - Create new category
            Category category = new Category();

            // When - Set up category
            category.setName("Motocross");
            category.setDetails("Categoría para motocicletas de motocross con especificaciones técnicas específicas");

            // Then - Verify initial state
            assertNull(category.getId());
            assertEquals("Motocross", category.getName());
            assertNotNull(category.getDetails());

            // When - Simulate database save
            category.setId(15L);

            // Then - Verify saved state
            assertEquals(15L, category.getId());
            assertEquals("Motocross", category.getName());
            assertEquals("Categoría para motocicletas de motocross con especificaciones técnicas específicas",
                    category.getDetails());

            // When - Update category
            category.setDetails("Categoría actualizada para motocicletas de motocross - normas 2024");

            // Then - Verify updated state
            assertEquals(15L, category.getId());
            assertEquals("Motocross", category.getName());
            assertEquals("Categoría actualizada para motocicletas de motocross - normas 2024", category.getDetails());
        }

        @Test
        @DisplayName("Should work with realistic racing categories")
        void shouldWorkWithRealisticRacingCategories() {
            // Given - Common racing categories
            String[][] racingCategories = {
                    { "Rally", "Categoría para vehículos de rally con modificaciones homologadas" },
                    { "Enduro", "Categoría para motocicletas de enduro - competencias de resistencia" },
                    { "Cross Country", "Categoría para vehículos todo terreno - largas distancias" },
                    { "Speed", "Categoría para vehículos de velocidad - pistas cerradas" },
                    { "4x4", "Categoría para vehículos 4x4 - terrenos extremos" }
            };

            for (String[] categoryData : racingCategories) {
                // When
                Category category = new Category();
                category.setName(categoryData[0]);
                category.setDetails(categoryData[1]);

                // Then
                assertEquals(categoryData[0], category.getName());
                assertEquals(categoryData[1], category.getDetails());
                assertTrue(category.getName().length() <= 20);
                assertTrue(category.getDetails().length() <= 400);
            }
        }

        @Test
        @DisplayName("Should support category comparison scenarios")
        void shouldSupportCategoryComparisonScenarios() {
            // Given
            Category category1 = new Category(1L, "Rally", "Rally category");
            Category category2 = new Category(2L, "Enduro", "Enduro category");
            Category category3 = new Category(1L, "Rally", "Rally category");

            // When & Then - Different categories
            assertNotEquals(category1.getId(), category2.getId());
            assertNotEquals(category1.getName(), category2.getName());

            // When & Then - Same data categories
            assertEquals(category1.getId(), category3.getId());
            assertEquals(category1.getName(), category3.getName());
            assertEquals(category1.getDetails(), category3.getDetails());
        }
    }
}
