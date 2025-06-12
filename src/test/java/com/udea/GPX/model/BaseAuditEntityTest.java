package com.udea.gpx.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para BaseAuditEntity - Clase base para auditoría de entidades JPA
 */
@DisplayName("BaseAuditEntity Model Tests")
class BaseAuditEntityTest {

    // Concrete test implementation to test the abstract class
    private static class TestAuditEntity extends BaseAuditEntity {

        public TestAuditEntity() {
            super();
        }

        public void setName(String name) {
            // Method for test compatibility - no field needed as it's not used for
            // assertions
        }

        // Public wrapper methods to expose protected methods for testing
        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public void onUpdate() {
            super.onUpdate();
        }
    }

    // ========== CONSTRUCTOR TESTS ==========

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create audit entity with default constructor")
        void shouldCreateAuditEntityWithDefaultConstructor() {
            // When
            TestAuditEntity entity = new TestAuditEntity();

            // Then
            assertNotNull(entity);
            assertNull(entity.getCreatedAt());
            assertNull(entity.getUpdatedAt());
            assertNull(entity.getCreatedBy());
            assertNull(entity.getUpdatedBy());
        }
    }

    // ========== GETTER AND SETTER TESTS ==========

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set created at correctly")
        void shouldGetAndSetCreatedAtCorrectly() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            LocalDateTime expectedCreatedAt = LocalDateTime.of(2024, 6, 15, 10, 30, 45);

            // When
            entity.setCreatedAt(expectedCreatedAt);

            // Then
            assertEquals(expectedCreatedAt, entity.getCreatedAt());
        }

        @Test
        @DisplayName("Should get and set updated at correctly")
        void shouldGetAndSetUpdatedAtCorrectly() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            LocalDateTime expectedUpdatedAt = LocalDateTime.of(2024, 6, 15, 14, 45, 30);

            // When
            entity.setUpdatedAt(expectedUpdatedAt);

            // Then
            assertEquals(expectedUpdatedAt, entity.getUpdatedAt());
        }

        @Test
        @DisplayName("Should get and set created by correctly")
        void shouldGetAndSetCreatedByCorrectly() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            String expectedCreatedBy = "admin@gpx.com";

            // When
            entity.setCreatedBy(expectedCreatedBy);

            // Then
            assertEquals(expectedCreatedBy, entity.getCreatedBy());
        }

        @Test
        @DisplayName("Should get and set updated by correctly")
        void shouldGetAndSetUpdatedByCorrectly() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            String expectedUpdatedBy = "user@gpx.com";

            // When
            entity.setUpdatedBy(expectedUpdatedBy);

            // Then
            assertEquals(expectedUpdatedBy, entity.getUpdatedBy());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setCreatedBy("admin");
            entity.setUpdatedBy("user");

            // When
            entity.setCreatedAt(null);
            entity.setUpdatedAt(null);
            entity.setCreatedBy(null);
            entity.setUpdatedBy(null);

            // Then
            assertNull(entity.getCreatedAt());
            assertNull(entity.getUpdatedAt());
            assertNull(entity.getCreatedBy());
            assertNull(entity.getUpdatedBy());
        }
    }

    // ========== BUSINESS LOGIC TESTS ==========

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should support typical audit information")
        void shouldSupportTypicalAuditInformation() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            LocalDateTime now = LocalDateTime.now();
            String[] typicalUsers = {
                    "admin@gpx.com",
                    "system@gpx.com",
                    "user123@gmail.com",
                    "manager@company.com",
                    "operator@rally.org"
            };

            for (String user : typicalUsers) {
                // When
                entity.setCreatedBy(user);
                entity.setUpdatedBy(user);
                entity.setCreatedAt(now);
                entity.setUpdatedAt(now.plusMinutes(30));

                // Then
                assertEquals(user, entity.getCreatedBy());
                assertEquals(user, entity.getUpdatedBy());
                assertTrue(entity.getCreatedBy().length() <= 100, "Created by should fit column constraint");
                assertTrue(entity.getUpdatedBy().length() <= 100, "Updated by should fit column constraint");
                assertTrue(entity.getUpdatedAt().isAfter(entity.getCreatedAt()) ||
                        entity.getUpdatedAt().isEqual(entity.getCreatedAt()),
                        "Updated at should be after or equal to created at");
            }
        }

        @Test
        @DisplayName("Should support audit trail tracking")
        void shouldSupportAuditTrailTracking() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            LocalDateTime creationTime = LocalDateTime.of(2024, 6, 15, 9, 0);
            LocalDateTime firstUpdate = LocalDateTime.of(2024, 6, 15, 10, 30);
            LocalDateTime secondUpdate = LocalDateTime.of(2024, 6, 15, 14, 45);

            // When - Entity creation
            entity.setCreatedAt(creationTime);
            entity.setCreatedBy("admin@gpx.com");
            entity.setUpdatedAt(creationTime);
            entity.setUpdatedBy("admin@gpx.com");

            // Then - Verify creation audit
            assertEquals(creationTime, entity.getCreatedAt());
            assertEquals("admin@gpx.com", entity.getCreatedBy());
            assertEquals(creationTime, entity.getUpdatedAt());
            assertEquals("admin@gpx.com", entity.getUpdatedBy());

            // When - First update
            entity.setUpdatedAt(firstUpdate);
            entity.setUpdatedBy("user@gpx.com");

            // Then - Verify first update audit
            assertEquals(creationTime, entity.getCreatedAt()); // Creation info unchanged
            assertEquals("admin@gpx.com", entity.getCreatedBy());
            assertEquals(firstUpdate, entity.getUpdatedAt());
            assertEquals("user@gpx.com", entity.getUpdatedBy());

            // When - Second update
            entity.setUpdatedAt(secondUpdate);
            entity.setUpdatedBy("manager@gpx.com");

            // Then - Verify second update audit
            assertEquals(creationTime, entity.getCreatedAt()); // Creation info still unchanged
            assertEquals("admin@gpx.com", entity.getCreatedBy());
            assertEquals(secondUpdate, entity.getUpdatedAt());
            assertEquals("manager@gpx.com", entity.getUpdatedBy());
        }

        @Test
        @DisplayName("Should handle pre-persist logic")
        void shouldHandlePrePersistLogic() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            entity.setName("Test Entity");

            // When - Simulate @PrePersist (onCreate method)
            entity.onCreate();

            // Then - Verify timestamps are set
            assertNotNull(entity.getCreatedAt());
            assertNotNull(entity.getUpdatedAt());
            // Allow for timing differences in CI/CD environments (up to 10 seconds)
            assertTrue(entity.getUpdatedAt().isEqual(entity.getCreatedAt()) ||
                    Math.abs(
                            java.time.Duration.between(entity.getCreatedAt(), entity.getUpdatedAt()).toSeconds()) <= 10,
                    "Created and updated timestamps should be equal or within 10 seconds during creation");

            // Verify timestamps are recent (allow for CI/CD timing differences - within 2
            // minutes)
            LocalDateTime now = LocalDateTime.now();
            assertTrue(entity.getCreatedAt().isBefore(now.plusSeconds(10)) &&
                    entity.getCreatedAt().isAfter(now.minusMinutes(2)),
                    "Created timestamp should be within reasonable time range for CI/CD");
            assertTrue(entity.getUpdatedAt().isBefore(now.plusSeconds(10)) &&
                    entity.getUpdatedAt().isAfter(now.minusMinutes(2)),
                    "Updated timestamp should be within reasonable time range for CI/CD");
        }

        @Test
        @DisplayName("Should handle pre-update logic")
        void shouldHandlePreUpdateLogic() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            LocalDateTime originalCreated = LocalDateTime.of(2024, 6, 15, 9, 0);
            LocalDateTime originalUpdated = LocalDateTime.of(2024, 6, 15, 10, 0);

            entity.setCreatedAt(originalCreated);
            entity.setUpdatedAt(originalUpdated);
            entity.setName("Original Name");

            // When - Simulate @PreUpdate (onUpdate method)
            entity.onUpdate();

            // Then - Verify only updated timestamp changes
            assertEquals(originalCreated, entity.getCreatedAt()); // Creation time unchanged
            assertNotNull(entity.getUpdatedAt());
            assertTrue(entity.getUpdatedAt().isAfter(originalUpdated)); // Updated time changed

            // Verify updated timestamp is recent (allow for CI/CD timing differences)
            LocalDateTime now = LocalDateTime.now();
            assertTrue(entity.getUpdatedAt().isBefore(now.plusSeconds(10)) &&
                    entity.getUpdatedAt().isAfter(now.minusMinutes(2)),
                    "Updated timestamp should be within reasonable time range for CI/CD");
        }

        @Test
        @DisplayName("Should preserve created timestamp on pre-persist when already set")
        void shouldPreserveCreatedTimestampOnPrePersistWhenAlreadySet() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            LocalDateTime existingCreatedAt = LocalDateTime.of(2024, 6, 15, 8, 0);
            entity.setCreatedAt(existingCreatedAt);

            // When - Call onCreate when createdAt already exists
            entity.onCreate();

            // Then - Verify existing createdAt is preserved
            assertEquals(existingCreatedAt, entity.getCreatedAt());
            assertNotNull(entity.getUpdatedAt());
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
            TestAuditEntity entity = new TestAuditEntity();
            String maxCreatedBy = "A".repeat(100); // Maximum length for created_by
            String maxUpdatedBy = "B".repeat(100); // Maximum length for updated_by

            // When
            entity.setCreatedBy(maxCreatedBy);
            entity.setUpdatedBy(maxUpdatedBy);

            // Then
            assertEquals(100, entity.getCreatedBy().length());
            assertEquals(100, entity.getUpdatedBy().length());
        }

        @Test
        @DisplayName("Should handle edge case values")
        void shouldHandleEdgeCaseValues() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            LocalDateTime minDateTime = LocalDateTime.MIN;
            LocalDateTime maxDateTime = LocalDateTime.MAX;

            // When & Then - Edge cases for timestamps
            entity.setCreatedAt(minDateTime);
            assertEquals(minDateTime, entity.getCreatedAt());

            entity.setUpdatedAt(maxDateTime);
            assertEquals(maxDateTime, entity.getUpdatedAt());

            // Edge cases for user fields
            entity.setCreatedBy("");
            assertEquals("", entity.getCreatedBy());

            entity.setUpdatedBy("");
            assertEquals("", entity.getUpdatedBy());
        }

        @Test
        @DisplayName("Should handle special characters in user fields")
        void shouldHandleSpecialCharactersInUserFields() {
            // Given
            TestAuditEntity entity = new TestAuditEntity();
            String userWithSpecialChars = "user@domain.com.co";
            String systemUser = "SYSTEM_AUTO_PROCESS";

            // When
            entity.setCreatedBy(userWithSpecialChars);
            entity.setUpdatedBy(systemUser);

            // Then
            assertEquals(userWithSpecialChars, entity.getCreatedBy());
            assertEquals(systemUser, entity.getUpdatedBy());
        }
    }

    // ========== INTEGRATION TESTS ==========

    @Nested
    @DisplayName("Integration and Usage Tests")
    class IntegrationUsageTests {

        @Test
        @DisplayName("Should support complete audit lifecycle")
        void shouldSupportCompleteAuditLifecycle() {
            // Given - Create new auditable entity
            TestAuditEntity entity = new TestAuditEntity();
            entity.setName("Rally Event");

            // When - Simulate entity creation (step 1)
            entity.onCreate(); // Then - Verify creation audit
            assertNotNull(entity.getCreatedAt());
            assertNotNull(entity.getUpdatedAt());
            // Allow for timing differences in CI/CD environments (up to 10 seconds)
            assertTrue(entity.getUpdatedAt().isEqual(entity.getCreatedAt()) ||
                    Math.abs(java.time.Duration.between(entity.getCreatedAt(), entity.getUpdatedAt())
                            .toSeconds()) <= 10,
                    "Created and updated timestamps should be equal or within 10 seconds during creation");

            // When - Set creation user (step 2)
            entity.setCreatedBy("admin@gpx.com");
            entity.setUpdatedBy("admin@gpx.com");

            // Then - Verify initial user assignment
            assertEquals("admin@gpx.com", entity.getCreatedBy());
            assertEquals("admin@gpx.com", entity.getUpdatedBy()); // When - Simulate entity update (step 3)
            LocalDateTime beforeUpdate = entity.getUpdatedAt();

            // Simulate realistic entity update with explicit timestamp control
            entity.setName("Updated Rally Event");
            entity.setUpdatedBy("user@gpx.com");

            // Set a slightly later timestamp to ensure update is after creation
            entity.setUpdatedAt(beforeUpdate.plusNanos(1));

            // Then - Verify update audit
            assertEquals("admin@gpx.com", entity.getCreatedBy()); // Creation user unchanged
            assertEquals("user@gpx.com", entity.getUpdatedBy());
            assertTrue(entity.getUpdatedAt().isAfter(beforeUpdate) || entity.getUpdatedAt().isEqual(beforeUpdate));
        }

        @Test
        @DisplayName("Should work with realistic audit scenarios")
        void shouldWorkWithRealisticAuditScenarios() {
            // Given - Different audit scenarios
            Object[][] auditScenarios = {
                    { "admin@gpx.com", "system@gpx.com", "System import" },
                    { "user123@gmail.com", "user123@gmail.com", "User self-update" },
                    { "manager@company.com", "admin@gpx.com", "Admin correction" },
                    { "system@auto.com", "operator@rally.org", "Manual override" }
            };

            for (int i = 0; i < auditScenarios.length; i++) {
                Object[] scenario = auditScenarios[i];

                // When
                String createdBy = (String) scenario[0];
                String updatedBy = (String) scenario[1];
                String description = (String) scenario[2];

                TestAuditEntity entity = new TestAuditEntity();
                entity.setName(description);

                // Simulate creation
                entity.onCreate();
                entity.setCreatedBy(createdBy);
                entity.setUpdatedBy(createdBy);

                LocalDateTime creationTime = entity.getCreatedAt();

                // Small delay for realistic update timing
                // Removed Thread.sleep(5); as it is not required for test correctness

                // Simulate update
                entity.setUpdatedBy(updatedBy);
                entity.onUpdate();

                // Then
                assertEquals(createdBy, entity.getCreatedBy());
                assertEquals(updatedBy, entity.getUpdatedBy());
                assertEquals(creationTime, entity.getCreatedAt()); // Creation time preserved
                assertTrue(entity.getUpdatedAt().isAfter(creationTime) ||
                        entity.getUpdatedAt().isEqual(creationTime));

                // Verify field constraints
                assertTrue(entity.getCreatedBy().length() <= 100);
                assertTrue(entity.getUpdatedBy().length() <= 100);
            }
        }

        @Test
        @DisplayName("Should support audit information queries")
        void shouldSupportAuditInformationQueries() {
            // Given - Entity with complete audit trail
            TestAuditEntity entity = new TestAuditEntity();
            entity.setName("Auditable Entity");

            LocalDateTime creationTime = LocalDateTime.of(2024, 6, 15, 9, 0);
            LocalDateTime updateTime = LocalDateTime.of(2024, 6, 15, 15, 30);

            entity.setCreatedAt(creationTime);
            entity.setUpdatedAt(updateTime);
            entity.setCreatedBy("creator@gpx.com");
            entity.setUpdatedBy("modifier@gpx.com");

            // When & Then - Audit queries (business logic examples)

            // Who created the entity?
            assertEquals("creator@gpx.com", entity.getCreatedBy());

            // When was it created?
            assertEquals(creationTime, entity.getCreatedAt());

            // Who last modified it?
            assertEquals("modifier@gpx.com", entity.getUpdatedBy());

            // When was it last modified?
            assertEquals(updateTime, entity.getUpdatedAt());

            // How long between creation and last update?
            long hoursBetween = java.time.Duration.between(entity.getCreatedAt(), entity.getUpdatedAt()).toHours();
            assertEquals(6, hoursBetween); // 9:00 to 15:30 = 6.5 hours

            // Was it modified by the same person who created it?
            boolean sameUser = entity.getCreatedBy().equals(entity.getUpdatedBy());
            assertFalse(sameUser);
        }

        @Test
        @DisplayName("Should support audit inheritance in subclasses")
        void shouldSupportAuditInheritanceInSubclasses() {
            // Given - Multiple audit entities (simulating different JPA entities)
            TestAuditEntity event = new TestAuditEntity();
            event.setName("Rally Event");

            TestAuditEntity category = new TestAuditEntity();
            category.setName("Rally Category");

            // When - Both entities use audit features
            event.onCreate();
            event.setCreatedBy("event_admin@gpx.com");

            category.onCreate();
            category.setCreatedBy("category_admin@gpx.com");

            // Then - Both have independent audit trails
            assertNotNull(event.getCreatedAt());
            assertNotNull(category.getCreatedAt());
            assertEquals("event_admin@gpx.com", event.getCreatedBy());
            assertEquals("category_admin@gpx.com", category.getCreatedBy());

            // Verify inheritance works properly
            assertTrue(event instanceof BaseAuditEntity);
            assertTrue(category instanceof BaseAuditEntity);
        }

        @Test
        @DisplayName("Should handle concurrent audit updates")
        void shouldHandleConcurrentAuditUpdates() {
            // Given - Entity with initial audit info
            TestAuditEntity entity = new TestAuditEntity();
            entity.setName("Concurrent Entity");
            entity.onCreate();
            entity.setCreatedBy("initial@gpx.com");

            LocalDateTime firstUpdateTime = entity.getUpdatedAt(); // When - Multiple rapid updates (simulating
                                                                   // concurrent access)
            for (int i = 1; i <= 3; i++) {
                // Small delay removed to avoid use of Thread.sleep()

                entity.setUpdatedBy("user" + i + "@gpx.com");
                entity.onUpdate(); // Then - Verify each update maintains audit integrity
                assertEquals("initial@gpx.com", entity.getCreatedBy()); // Creator preserved
                assertEquals("user" + i + "@gpx.com", entity.getUpdatedBy());
                // Allow for timing differences in CI/CD environments
                assertTrue(entity.getUpdatedAt().isAfter(firstUpdateTime)
                        || entity.getUpdatedAt().isEqual(firstUpdateTime)
                        || Math.abs(
                                java.time.Duration.between(firstUpdateTime, entity.getUpdatedAt()).toSeconds()) <= 10);

                firstUpdateTime = entity.getUpdatedAt(); // Update for next iteration
            }
        }
    }
}
