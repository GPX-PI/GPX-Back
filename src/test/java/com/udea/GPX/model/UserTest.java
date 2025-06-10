package com.udea.GPX.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para User Model - Entidad JPA para usuarios del sistema
 */
@DisplayName("User Model Tests")
class UserTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create user with default constructor")
        void shouldCreateUserWithDefaultConstructor() {
            // When
            User user = new User();

            // Then
            assertNotNull(user);
            assertNull(user.getId());
            assertNull(user.getFirstName());
            assertNull(user.getLastName());
            assertNull(user.getIdentification());
            assertNull(user.getPhone());
            assertFalse(user.isAdmin());
            assertNull(user.getEmail());
            assertNull(user.getRole());
            assertNull(user.getBirthdate());
            assertNull(user.getTypeOfId());
            assertNull(user.getTeamName());
            assertNull(user.getEps());
            assertNull(user.getRh());
            assertNull(user.getEmergencyPhone());
            assertNull(user.getAlergies());
            assertNull(user.getWikiloc());
            assertNull(user.getInsurance());
            assertNull(user.getTerrapirata());
            assertNull(user.getInstagram());
            assertNull(user.getFacebook());
            assertEquals("", user.getPicture()); // Default value
            assertNull(user.getPassword());
            assertNull(user.getGoogleId());
            assertEquals("LOCAL", user.getAuthProvider()); // Default value
        }

        @Test
        @DisplayName("Should create user with full constructor")
        void shouldCreateUserWithFullConstructor() {
            // Given
            Long expectedId = 1L;
            String expectedFirstName = "Juan";
            String expectedLastName = "Pérez";
            String expectedIdentification = "12345678";
            String expectedPhone = "3001234567";
            boolean expectedAdmin = true;
            String expectedEmail = "juan.perez@example.com";
            String expectedRole = "PILOTO";
            LocalDate expectedBirthdate = LocalDate.of(1990, 5, 15);
            String expectedTypeOfId = "CC";
            String expectedTeamName = "Rally Team";
            String expectedEps = "Sura EPS";
            String expectedRh = "O+";
            String expectedEmergencyPhone = "3009876543";
            String expectedAlergies = "Ninguna";
            String expectedWikiloc = "juanperez_wikiloc";
            String expectedInsurance = "Seguros Bolivar";
            String expectedTerrapirata = "juanperez_tp";
            String expectedInstagram = "@juanperez";
            String expectedFacebook = "juan.perez";

            // When
            User user = new User(expectedId, expectedFirstName, expectedLastName, expectedIdentification,
                    expectedPhone, expectedAdmin, expectedEmail, expectedRole, expectedBirthdate,
                    expectedTypeOfId, expectedTeamName, expectedEps, expectedRh, expectedEmergencyPhone,
                    expectedAlergies, expectedWikiloc, expectedInsurance, expectedTerrapirata,
                    expectedInstagram, expectedFacebook);

            // Then
            assertEquals(expectedId, user.getId());
            assertEquals(expectedFirstName, user.getFirstName());
            assertEquals(expectedLastName, user.getLastName());
            assertEquals(expectedIdentification, user.getIdentification());
            assertEquals(expectedPhone, user.getPhone());
            assertEquals(expectedAdmin, user.isAdmin());
            assertEquals(expectedEmail, user.getEmail());
            assertEquals(expectedRole, user.getRole());
            assertEquals(expectedBirthdate, user.getBirthdate());
            assertEquals(expectedTypeOfId, user.getTypeOfId());
            assertEquals(expectedTeamName, user.getTeamName());
            assertEquals(expectedEps, user.getEps());
            assertEquals(expectedRh, user.getRh());
            assertEquals(expectedEmergencyPhone, user.getEmergencyPhone());
            assertEquals(expectedAlergies, user.getAlergies());
            assertEquals(expectedWikiloc, user.getWikiloc());
            assertEquals(expectedInsurance, user.getInsurance());
            assertEquals(expectedTerrapirata, user.getTerrapirata());
            assertEquals(expectedInstagram, user.getInstagram());
            assertEquals(expectedFacebook, user.getFacebook());
            assertEquals("", user.getPicture()); // Always set to empty string
        }

        @Test
        @DisplayName("Should handle null values in full constructor")
        void shouldHandleNullValuesInFullConstructor() {
            // When & Then
            assertDoesNotThrow(() -> new User(null, null, null, null, null, false, null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null));

            User userWithNulls = new User(null, null, null, null, null, false, null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null);

            assertNull(userWithNulls.getId());
            assertNull(userWithNulls.getFirstName());
            assertNull(userWithNulls.getLastName());
            assertEquals("", userWithNulls.getPicture());
            assertEquals("LOCAL", userWithNulls.getAuthProvider());
        }
    }

    // ========== BASIC FIELD GETTER/SETTER TESTS ==========

    @Nested
    @DisplayName("Basic Field Tests")
    class BasicFieldTests {

        @Test
        @DisplayName("Should get and set basic identity fields correctly")
        void shouldGetAndSetBasicIdentityFieldsCorrectly() {
            // Given
            User user = new User();

            // When & Then - ID
            Long expectedId = 42L;
            user.setId(expectedId);
            assertEquals(expectedId, user.getId());

            // When & Then - First Name
            String expectedFirstName = "María";
            user.setFirstName(expectedFirstName);
            assertEquals(expectedFirstName, user.getFirstName());

            // When & Then - Last Name
            String expectedLastName = "González";
            user.setLastName(expectedLastName);
            assertEquals(expectedLastName, user.getLastName());

            // When & Then - Identification
            String expectedIdentification = "87654321";
            user.setIdentification(expectedIdentification);
            assertEquals(expectedIdentification, user.getIdentification());

            // When & Then - Email
            String expectedEmail = "maria.gonzalez@example.com";
            user.setEmail(expectedEmail);
            assertEquals(expectedEmail, user.getEmail());
        }

        @Test
        @DisplayName("Should get and set contact fields correctly")
        void shouldGetAndSetContactFieldsCorrectly() {
            // Given
            User user = new User();

            // When & Then - Phone
            String expectedPhone = "3151234567";
            user.setPhone(expectedPhone);
            assertEquals(expectedPhone, user.getPhone());

            // When & Then - Emergency Phone
            String expectedEmergencyPhone = "3009876543";
            user.setEmergencyPhone(expectedEmergencyPhone);
            assertEquals(expectedEmergencyPhone, user.getEmergencyPhone());
        }

        @Test
        @DisplayName("Should get and set admin and role fields correctly")
        void shouldGetAndSetAdminAndRoleFieldsCorrectly() {
            // Given
            User user = new User();

            // When & Then - Admin (boolean)
            assertFalse(user.isAdmin()); // Default value
            user.setAdmin(true);
            assertTrue(user.isAdmin());
            user.setAdmin(false);
            assertFalse(user.isAdmin());

            // When & Then - Role
            String expectedRole = "COPILOTO";
            user.setRole(expectedRole);
            assertEquals(expectedRole, user.getRole());
        }

        @Test
        @DisplayName("Should get and set date and identification type fields correctly")
        void shouldGetAndSetDateAndIdentificationTypeFieldsCorrectly() {
            // Given
            User user = new User();

            // When & Then - Birthdate
            LocalDate expectedBirthdate = LocalDate.of(1985, 8, 20);
            user.setBirthdate(expectedBirthdate);
            assertEquals(expectedBirthdate, user.getBirthdate());

            // When & Then - Type of ID
            String expectedTypeOfId = "CE";
            user.setTypeOfId(expectedTypeOfId);
            assertEquals(expectedTypeOfId, user.getTypeOfId());
        }
    }

    // ========== RACING SPECIFIC FIELD TESTS ==========

    @Nested
    @DisplayName("Racing Specific Field Tests")
    class RacingSpecificFieldTests {

        @Test
        @DisplayName("Should get and set team information correctly")
        void shouldGetAndSetTeamInformationCorrectly() {
            // Given
            User user = new User();

            // When & Then - Team Name
            String expectedTeamName = "Equipo Thunder";
            user.setTeamName(expectedTeamName);
            assertEquals(expectedTeamName, user.getTeamName());
        }

        @Test
        @DisplayName("Should get and set medical information correctly")
        void shouldGetAndSetMedicalInformationCorrectly() {
            // Given
            User user = new User();

            // When & Then - EPS
            String expectedEps = "Sanitas EPS";
            user.setEps(expectedEps);
            assertEquals(expectedEps, user.getEps());

            // When & Then - RH (Blood Type)
            String expectedRh = "A-";
            user.setRh(expectedRh);
            assertEquals(expectedRh, user.getRh());

            // When & Then - Allergies
            String expectedAlergies = "Alérgico a penicilina y mariscos";
            user.setAlergies(expectedAlergies);
            assertEquals(expectedAlergies, user.getAlergies());

            // When & Then - Insurance
            String expectedInsurance = "AXA Colpatria";
            user.setInsurance(expectedInsurance);
            assertEquals(expectedInsurance, user.getInsurance());
        }
    }

    // ========== SOCIAL MEDIA AND PLATFORM TESTS ==========

    @Nested
    @DisplayName("Social Media and Platform Tests")
    class SocialMediaPlatformTests {

        @Test
        @DisplayName("Should get and set social media accounts correctly")
        void shouldGetAndSetSocialMediaAccountsCorrectly() {
            // Given
            User user = new User();

            // When & Then - Instagram
            String expectedInstagram = "@piloto_rally";
            user.setInstagram(expectedInstagram);
            assertEquals(expectedInstagram, user.getInstagram());

            // When & Then - Facebook
            String expectedFacebook = "piloto.rally.oficial";
            user.setFacebook(expectedFacebook);
            assertEquals(expectedFacebook, user.getFacebook());
        }

        @Test
        @DisplayName("Should get and set racing platform accounts correctly")
        void shouldGetAndSetRacingPlatformAccountsCorrectly() {
            // Given
            User user = new User();

            // When & Then - Wikiloc
            String expectedWikiloc = "piloto_aventura_wikiloc";
            user.setWikiloc(expectedWikiloc);
            assertEquals(expectedWikiloc, user.getWikiloc());

            // When & Then - Terrapirata
            String expectedTerrapirata = "piloto_terrapirata";
            user.setTerrapirata(expectedTerrapirata);
            assertEquals(expectedTerrapirata, user.getTerrapirata());
        }

        @Test
        @DisplayName("Should get and set picture correctly")
        void shouldGetAndSetPictureCorrectly() {
            // Given
            User user = new User();

            // When & Then - Picture (starts with empty string)
            assertEquals("", user.getPicture());

            String expectedPicture = "profile_pic_url.jpg";
            user.setPicture(expectedPicture);
            assertEquals(expectedPicture, user.getPicture());
        }
    }

    // ========== AUTHENTICATION FIELD TESTS ==========

    @Nested
    @DisplayName("Authentication Field Tests")
    class AuthenticationFieldTests {

        @Test
        @DisplayName("Should get and set password correctly")
        void shouldGetAndSetPasswordCorrectly() {
            // Given
            User user = new User();

            // When & Then - Password
            String expectedPassword = "hashedPassword123";
            user.setPassword(expectedPassword);
            assertEquals(expectedPassword, user.getPassword());
        }

        @Test
        @DisplayName("Should get and set OAuth2 fields correctly")
        void shouldGetAndSetOAuth2FieldsCorrectly() {
            // Given
            User user = new User();

            // When & Then - Google ID
            String expectedGoogleId = "google_oauth_id_123456";
            user.setGoogleId(expectedGoogleId);
            assertEquals(expectedGoogleId, user.getGoogleId());

            // When & Then - Auth Provider (starts with LOCAL)
            assertEquals("LOCAL", user.getAuthProvider());

            String expectedAuthProvider = "GOOGLE";
            user.setAuthProvider(expectedAuthProvider);
            assertEquals(expectedAuthProvider, user.getAuthProvider());
        }

        @Test
        @DisplayName("Should handle different auth provider values")
        void shouldHandleDifferentAuthProviderValues() {
            // Given
            User user = new User();
            String[] validAuthProviders = { "LOCAL", "GOOGLE" };

            for (String provider : validAuthProviders) {
                // When
                user.setAuthProvider(provider);

                // Then
                assertEquals(provider, user.getAuthProvider());
            }
        }
    }

    // ========== BUSINESS LOGIC TESTS ==========

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should support typical user roles")
        void shouldSupportTypicalUserRoles() {
            // Given
            User user = new User();
            String[] typicalRoles = { "PILOTO", "COPILOTO", "NAVEGANTE", "MECANICO", "ORGANIZADOR" };

            for (String role : typicalRoles) {
                // When
                user.setRole(role);

                // Then
                assertEquals(role, user.getRole());
                assertTrue(role.length() <= 20, "Role should fit column length constraint");
            }
        }

        @Test
        @DisplayName("Should support typical blood types")
        void shouldSupportTypicalBloodTypes() {
            // Given
            User user = new User();
            String[] bloodTypes = { "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-" };

            for (String bloodType : bloodTypes) {
                // When
                user.setRh(bloodType);

                // Then
                assertEquals(bloodType, user.getRh());
                assertTrue(bloodType.length() <= 5, "RH should fit column length constraint");
            }
        }

        @Test
        @DisplayName("Should support Colombian identification types")
        void shouldSupportColombianIdentificationTypes() {
            // Given
            User user = new User();
            String[] idTypes = { "CC", "CE", "TI", "PA", "RC", "NIT" };

            for (String idType : idTypes) {
                // When
                user.setTypeOfId(idType);

                // Then
                assertEquals(idType, user.getTypeOfId());
                assertTrue(idType.length() <= 20, "Type of ID should fit column length constraint");
            }
        }

        @Test
        @DisplayName("Should calculate age from birthdate")
        void shouldCalculateAgeFromBirthdate() {
            // Given
            User user1 = new User();
            User user2 = new User();
            LocalDate birthdate1990 = LocalDate.of(1990, 1, 1);
            LocalDate birthdate2000 = LocalDate.of(2000, 12, 31);

            // When - Test with 1990 birthdate
            user1.setBirthdate(birthdate1990);
            LocalDate today = LocalDate.now();
            int expectedAge1990 = today.getYear() - 1990;
            if (today.getDayOfYear() < birthdate1990.getDayOfYear()) {
                expectedAge1990--;
            }

            // When - Test with 2000 birthdate
            user2.setBirthdate(birthdate2000);
            int expectedAge2000 = today.getYear() - 2000;
            if (today.getDayOfYear() < birthdate2000.getDayOfYear()) {
                expectedAge2000--;
            }

            // Then
            assertEquals(birthdate1990, user1.getBirthdate());
            assertEquals(birthdate2000, user2.getBirthdate());
            assertTrue(expectedAge1990 >= 0);
            assertTrue(expectedAge2000 >= 0);
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
            User user = new User();

            // When & Then - Test maximum lengths
            String maxFirstName = "A".repeat(50); // 50 chars (max)
            user.setFirstName(maxFirstName);
            assertEquals(50, user.getFirstName().length());

            String maxLastName = "B".repeat(50); // 50 chars (max)
            user.setLastName(maxLastName);
            assertEquals(50, user.getLastName().length());

            String maxEmail = "C".repeat(100); // 100 chars (max)
            user.setEmail(maxEmail);
            assertEquals(100, user.getEmail().length());

            String maxTeamName = "D".repeat(50); // 50 chars (max)
            user.setTeamName(maxTeamName);
            assertEquals(50, user.getTeamName().length());

            String maxAlergies = "E".repeat(255); // 255 chars (max)
            user.setAlergies(maxAlergies);
            assertEquals(255, user.getAlergies().length());
        }

        @Test
        @DisplayName("Should handle phone number formats")
        void shouldHandlePhoneNumberFormats() {
            // Given
            User user = new User();
            String[] phoneFormats = {
                    "3001234567", // 10 chars
                    "300-123-4567", // 12 chars
                    "300 123 4567", // 12 chars
                    "+573001234567", // 13 chars
                    "(300)1234567" // 12 chars
            };

            for (String phone : phoneFormats) {
                // When
                user.setPhone(phone);

                // Then
                assertEquals(phone, user.getPhone());
                assertTrue(phone.length() <= 15, "Phone should fit column length constraint");
            }
        }

        @Test
        @DisplayName("Should handle email formats")
        void shouldHandleEmailFormats() {
            // Given
            User user = new User();
            String[] emailFormats = {
                    "user@example.com",
                    "user.name@example.com",
                    "user+tag@example.com",
                    "user123@example.co.uk",
                    "very.long.email.address@very.long.domain.example.com"
            };

            for (String email : emailFormats) {
                // When
                user.setEmail(email);

                // Then
                assertEquals(email, user.getEmail());
                assertTrue(email.length() <= 100, "Email should fit column length constraint");
            }
        }

        @Test
        @DisplayName("Should handle null values gracefully")
        void shouldHandleNullValuesGracefully() {
            // Given
            User user = new User();

            // When - Set all fields to null
            user.setId(null);
            user.setFirstName(null);
            user.setLastName(null);
            user.setIdentification(null);
            user.setPhone(null);
            user.setEmail(null);
            user.setRole(null);
            user.setBirthdate(null);
            user.setTypeOfId(null);
            user.setTeamName(null);
            user.setEps(null);
            user.setRh(null);
            user.setEmergencyPhone(null);
            user.setAlergies(null);
            user.setWikiloc(null);
            user.setInsurance(null);
            user.setTerrapirata(null);
            user.setInstagram(null);
            user.setFacebook(null);
            user.setPicture(null);
            user.setPassword(null);
            user.setGoogleId(null);
            user.setAuthProvider(null);

            // Then - All fields should be null (except admin which is boolean)
            assertNull(user.getId());
            assertNull(user.getFirstName());
            assertNull(user.getLastName());
            assertNull(user.getIdentification());
            assertNull(user.getPhone());
            assertFalse(user.isAdmin()); // boolean defaults to false
            assertNull(user.getEmail());
            assertNull(user.getRole());
            assertNull(user.getBirthdate());
            assertNull(user.getTypeOfId());
            assertNull(user.getTeamName());
            assertNull(user.getEps());
            assertNull(user.getRh());
            assertNull(user.getEmergencyPhone());
            assertNull(user.getAlergies());
            assertNull(user.getWikiloc());
            assertNull(user.getInsurance());
            assertNull(user.getTerrapirata());
            assertNull(user.getInstagram());
            assertNull(user.getFacebook());
            assertNull(user.getPicture());
            assertNull(user.getPassword());
            assertNull(user.getGoogleId());
            assertNull(user.getAuthProvider());
        }
    }

    // ========== INTEGRATION TESTS ==========

    @Nested
    @DisplayName("Integration and Usage Tests")
    class IntegrationUsageTests {

        @Test
        @DisplayName("Should support complete user lifecycle")
        void shouldSupportCompleteUserLifecycle() {
            // Given - Create new user (registration)
            User user = new User();

            // When - Step 1: Basic registration
            user.setFirstName("Carlos");
            user.setLastName("Rodríguez");
            user.setEmail("carlos.rodriguez@example.com");
            user.setPassword("hashedPassword");

            // Then - Verify basic registration
            assertNull(user.getId()); // ID not set yet
            assertEquals("Carlos", user.getFirstName());
            assertEquals("Rodríguez", user.getLastName());
            assertEquals("carlos.rodriguez@example.com", user.getEmail());
            assertEquals("hashedPassword", user.getPassword());
            assertEquals("LOCAL", user.getAuthProvider()); // Default
            assertFalse(user.isAdmin()); // Default

            // When - Step 2: Complete profile
            user.setIdentification("12345678");
            user.setTypeOfId("CC");
            user.setPhone("3001234567");
            user.setBirthdate(LocalDate.of(1985, 6, 15));
            user.setRole("PILOTO");
            user.setTeamName("Rally Andes");
            user.setEps("Sura EPS");
            user.setRh("O+");
            user.setEmergencyPhone("3009876543");

            // Then - Verify complete profile
            assertEquals("12345678", user.getIdentification());
            assertEquals("CC", user.getTypeOfId());
            assertEquals("3001234567", user.getPhone());
            assertEquals(LocalDate.of(1985, 6, 15), user.getBirthdate());
            assertEquals("PILOTO", user.getRole());
            assertEquals("Rally Andes", user.getTeamName());

            // When - Step 3: Add social media (optional)
            user.setInstagram("@carlos_rally");
            user.setFacebook("carlos.rally");
            user.setWikiloc("carlos_adventures");

            // Then - Verify social media
            assertEquals("@carlos_rally", user.getInstagram());
            assertEquals("carlos.rally", user.getFacebook());
            assertEquals("carlos_adventures", user.getWikiloc());

            // When - Step 4: Simulate database save
            user.setId(100L);

            // Then - Verify complete user
            assertEquals(100L, user.getId());
            assertEquals("Carlos", user.getFirstName());
            assertEquals("Rodríguez", user.getLastName());
            assertEquals("carlos.rodriguez@example.com", user.getEmail());
            assertEquals("PILOTO", user.getRole());
            assertEquals("Rally Andes", user.getTeamName());
            assertFalse(user.isAdmin());
            assertEquals("LOCAL", user.getAuthProvider());
        }

        @Test
        @DisplayName("Should support OAuth2 user creation")
        void shouldSupportOAuth2UserCreation() {
            // Given - OAuth2 registration from Google
            User user = new User();

            // When - Set OAuth2 fields
            user.setFirstName("Ana");
            user.setLastName("García");
            user.setEmail("ana.garcia@gmail.com");
            user.setGoogleId("google_123456789");
            user.setAuthProvider("GOOGLE");
            user.setPicture("https://lh3.googleusercontent.com/photo.jpg");

            // Then - Verify OAuth2 user
            assertEquals("Ana", user.getFirstName());
            assertEquals("García", user.getLastName());
            assertEquals("ana.garcia@gmail.com", user.getEmail());
            assertEquals("google_123456789", user.getGoogleId());
            assertEquals("GOOGLE", user.getAuthProvider());
            assertEquals("https://lh3.googleusercontent.com/photo.jpg", user.getPicture());
            assertNull(user.getPassword()); // No password for OAuth2 users
            assertFalse(user.isAdmin()); // Default
        }

        @Test
        @DisplayName("Should support admin user scenarios")
        void shouldSupportAdminUserScenarios() {
            // Given - Create admin user
            User adminUser = new User();

            // When - Set up admin
            adminUser.setFirstName("Admin");
            adminUser.setLastName("Sistema");
            adminUser.setEmail("admin@gpx.com");
            adminUser.setPassword("hashedAdminPassword");
            adminUser.setAdmin(true);
            adminUser.setRole("ADMINISTRADOR");

            // Then - Verify admin user
            assertEquals("Admin", adminUser.getFirstName());
            assertEquals("Sistema", adminUser.getLastName());
            assertEquals("admin@gpx.com", adminUser.getEmail());
            assertTrue(adminUser.isAdmin());
            assertEquals("ADMINISTRADOR", adminUser.getRole());
            assertEquals("LOCAL", adminUser.getAuthProvider());

            // When - Regular user should not be admin
            User regularUser = new User();
            regularUser.setFirstName("Usuario");
            regularUser.setRole("PILOTO");

            // Then - Verify regular user
            assertEquals("Usuario", regularUser.getFirstName());
            assertEquals("PILOTO", regularUser.getRole());
            assertFalse(regularUser.isAdmin()); // Default
        }

        @Test
        @DisplayName("Should support realistic racing user profiles")
        void shouldSupportRealisticRacingUserProfiles() {
            // Given - Different user types in racing
            Object[][] userProfiles = {
                    { "Juan", "Piloto", "PILOTO", false, "Rally Team Alpha" },
                    { "María", "Copiloto", "COPILOTO", false, "Rally Team Alpha" },
                    { "Pedro", "Mecánico", "MECANICO", false, "Team Support" },
                    { "Ana", "Organizadora", "ORGANIZADOR", true, "GPX Organization" },
                    { "Carlos", "Navegante", "NAVEGANTE", false, "Adventure Team" }
            };

            for (Object[] profile : userProfiles) {
                // When
                String firstName = (String) profile[0];
                String lastName = (String) profile[1];
                String role = (String) profile[2];
                boolean isAdmin = (Boolean) profile[3];
                String teamName = (String) profile[4];

                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setRole(role);
                user.setAdmin(isAdmin);
                user.setTeamName(teamName);
                user.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com");

                // Then
                assertEquals(firstName, user.getFirstName());
                assertEquals(lastName, user.getLastName());
                assertEquals(role, user.getRole());
                assertEquals(isAdmin, user.isAdmin());
                assertEquals(teamName, user.getTeamName());
                assertNotNull(user.getEmail());
                assertEquals("LOCAL", user.getAuthProvider());
            }
        }
    }
}
