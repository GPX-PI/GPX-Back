package com.udea.GPX.dto;

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

@DisplayName("SimpleRegisterDTO Tests")
class SimpleRegisterDTOTest {

    private Validator validator;
    private SimpleRegisterDTO simpleRegisterDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        simpleRegisterDTO = new SimpleRegisterDTO();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should create object with null values")
        void testDefaultConstructor() {
            SimpleRegisterDTO dto = new SimpleRegisterDTO();

            assertNull(dto.getFirstName());
            assertNull(dto.getLastName());
            assertNull(dto.getEmail());
            assertNull(dto.getPassword());
        }

        @Test
        @DisplayName("Parameterized constructor should set all values correctly")
        void testParameterizedConstructor() {
            String firstName = "Juan";
            String lastName = "Pérez";
            String email = "juan.perez@example.com";
            String password = "password123";

            SimpleRegisterDTO dto = new SimpleRegisterDTO(firstName, lastName, email, password);

            assertEquals(firstName, dto.getFirstName());
            assertEquals(lastName, dto.getLastName());
            assertEquals(email, dto.getEmail());
            assertEquals(password, dto.getPassword());
        }

        @Test
        @DisplayName("Constructor with minimum valid values should work correctly")
        void testConstructorWithMinimumValidValues() {
            SimpleRegisterDTO dto = new SimpleRegisterDTO("A", "B", "a@b.com", "123456");

            assertEquals("A", dto.getFirstName());
            assertEquals("B", dto.getLastName());
            assertEquals("a@b.com", dto.getEmail());
            assertEquals("123456", dto.getPassword());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("FirstName getter and setter should work correctly")
        void testFirstNameGetterSetter() {
            String firstName = "María";
            simpleRegisterDTO.setFirstName(firstName);
            assertEquals(firstName, simpleRegisterDTO.getFirstName());
        }

        @Test
        @DisplayName("LastName getter and setter should work correctly")
        void testLastNameGetterSetter() {
            String lastName = "González";
            simpleRegisterDTO.setLastName(lastName);
            assertEquals(lastName, simpleRegisterDTO.getLastName());
        }

        @Test
        @DisplayName("Email getter and setter should work correctly")
        void testEmailGetterSetter() {
            String email = "maria.gonzalez@example.com";
            simpleRegisterDTO.setEmail(email);
            assertEquals(email, simpleRegisterDTO.getEmail());
        }

        @Test
        @DisplayName("Password getter and setter should work correctly")
        void testPasswordGetterSetter() {
            String password = "securePassword123";
            simpleRegisterDTO.setPassword(password);
            assertEquals(password, simpleRegisterDTO.getPassword());
        }

        @Test
        @DisplayName("Setting null values should work correctly")
        void testSettingNullValues() {
            simpleRegisterDTO.setFirstName(null);
            simpleRegisterDTO.setLastName(null);
            simpleRegisterDTO.setEmail(null);
            simpleRegisterDTO.setPassword(null);

            assertNull(simpleRegisterDTO.getFirstName());
            assertNull(simpleRegisterDTO.getLastName());
            assertNull(simpleRegisterDTO.getEmail());
            assertNull(simpleRegisterDTO.getPassword());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid DTO should pass validation")
        void testValidDTO() {
            simpleRegisterDTO.setFirstName("Juan");
            simpleRegisterDTO.setLastName("Pérez");
            simpleRegisterDTO.setEmail("juan.perez@example.com");
            simpleRegisterDTO.setPassword("password123");

            Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
            assertTrue(violations.isEmpty());
        }

        @Nested
        @DisplayName("FirstName Validation")
        class FirstNameValidation {

            @Test
            @DisplayName("Null firstName should fail validation")
            void testNullFirstName() {
                simpleRegisterDTO.setFirstName(null);
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El nombre es requerido")));
            }

            @Test
            @DisplayName("Empty firstName should fail validation")
            void testEmptyFirstName() {
                simpleRegisterDTO.setFirstName("");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El nombre es requerido")));
            }

            @Test
            @DisplayName("Blank firstName should fail validation")
            void testBlankFirstName() {
                simpleRegisterDTO.setFirstName("   ");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El nombre es requerido")));
            }

            @Test
            @DisplayName("FirstName exceeding 100 characters should fail validation")
            void testFirstNameTooLong() {
                String longName = "A".repeat(101);
                simpleRegisterDTO.setFirstName(longName);
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El nombre no puede exceder 100 caracteres")));
            }

            @Test
            @DisplayName("FirstName with exactly 100 characters should pass validation")
            void testFirstNameExactly100Characters() {
                String name100 = "A".repeat(100);
                simpleRegisterDTO.setFirstName(name100);
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertTrue(violations.isEmpty());
            }

            @ParameterizedTest
            @ValueSource(strings = { "A", "Juan", "María José", "José Luis" })
            @DisplayName("Valid firstName values should pass validation")
            void testValidFirstNames(String firstName) {
                simpleRegisterDTO.setFirstName(firstName);
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("LastName Validation")
        class LastNameValidation {

            @Test
            @DisplayName("Null lastName should fail validation")
            void testNullLastName() {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName(null);
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El apellido es requerido")));
            }

            @Test
            @DisplayName("Empty lastName should fail validation")
            void testEmptyLastName() {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El apellido es requerido")));
            }

            @Test
            @DisplayName("LastName exceeding 100 characters should fail validation")
            void testLastNameTooLong() {
                String longLastName = "B".repeat(101);
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName(longLastName);
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El apellido no puede exceder 100 caracteres")));
            }

            @ParameterizedTest
            @ValueSource(strings = { "B", "Pérez", "García López", "Rodríguez-Martínez" })
            @DisplayName("Valid lastName values should pass validation")
            void testValidLastNames(String lastName) {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName(lastName);
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("Email Validation")
        class EmailValidation {

            @Test
            @DisplayName("Null email should fail validation")
            void testNullEmail() {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail(null);
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El email es requerido")));
            }

            @Test
            @DisplayName("Empty email should fail validation")
            void testEmptyEmail() {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("");
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El email es requerido")));
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "invalid-email",
                    "user@",
                    "@domain.com",
                    "user@domain",
                    "user.domain.com"
            })
            @DisplayName("Invalid email formats should fail validation")
            void testInvalidEmailFormats(String invalidEmail) {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail(invalidEmail);
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El email debe tener un formato válido")));
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "user@domain.com",
                    "test@example.org",
                    "juan.perez@company.co.uk",
                    "user123@test-domain.com"
            })
            @DisplayName("Valid email formats should pass validation")
            void testValidEmailFormats(String validEmail) {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail(validEmail);
                simpleRegisterDTO.setPassword("password123");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("Password Validation")
        class PasswordValidation {

            @Test
            @DisplayName("Null password should fail validation")
            void testNullPassword() {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword(null);

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("La contraseña es requerida")));
            }

            @Test
            @DisplayName("Empty password should fail validation")
            void testEmptyPassword() {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("La contraseña es requerida")));
            }

            @ParameterizedTest
            @ValueSource(strings = { "1", "12", "123", "1234", "12345" })
            @DisplayName("Password with less than 6 characters should fail validation")
            void testPasswordTooShort(String shortPassword) {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword(shortPassword);

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().contains("La contraseña debe tener entre 6 y 100 caracteres")));
            }

            @Test
            @DisplayName("Password with more than 100 characters should fail validation")
            void testPasswordTooLong() {
                String longPassword = "A".repeat(101);
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword(longPassword);

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().contains("La contraseña debe tener entre 6 y 100 caracteres")));
            }

            @Test
            @DisplayName("Password with exactly 6 characters should pass validation")
            void testPasswordExactlyMinimumLength() {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword("123456");

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertTrue(violations.isEmpty());
            }

            @Test
            @DisplayName("Password with exactly 100 characters should pass validation")
            void testPasswordExactlyMaximumLength() {
                String password100 = "A".repeat(100);
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword(password100);

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertTrue(violations.isEmpty());
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "password123",
                    "securePassword",
                    "P@ssw0rd!",
                    "mySecretPassword"
            })
            @DisplayName("Valid passwords should pass validation")
            void testValidPasswords(String validPassword) {
                simpleRegisterDTO.setFirstName("Juan");
                simpleRegisterDTO.setLastName("Pérez");
                simpleRegisterDTO.setEmail("test@example.com");
                simpleRegisterDTO.setPassword(validPassword);

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("Combined Validation")
        class CombinedValidation {

            @Test
            @DisplayName("All fields null should produce four validation errors")
            void testAllFieldsNull() {
                simpleRegisterDTO.setFirstName(null);
                simpleRegisterDTO.setLastName(null);
                simpleRegisterDTO.setEmail(null);
                simpleRegisterDTO.setPassword(null);

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertEquals(4, violations.size());

                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El nombre es requerido")));
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El apellido es requerido")));
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El email es requerido")));
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("La contraseña es requerida")));
            }

            @Test
            @DisplayName("All fields invalid should produce multiple validation errors")
            void testAllFieldsInvalid() {
                simpleRegisterDTO.setFirstName("A".repeat(101)); // Too long
                simpleRegisterDTO.setLastName(""); // Empty
                simpleRegisterDTO.setEmail("invalid-email"); // Invalid format
                simpleRegisterDTO.setPassword("123"); // Too short

                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
                assertTrue(violations.size() >= 4);
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("DTO with minimum valid lengths should pass validation")
        void testMinimumValidLengths() {
            simpleRegisterDTO.setFirstName("A");
            simpleRegisterDTO.setLastName("B");
            simpleRegisterDTO.setEmail("a@b.co");
            simpleRegisterDTO.setPassword("123456");

            Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with maximum valid lengths should pass validation")
        void testMaximumValidLengths() {
            String name100 = "A".repeat(100);
            String password100 = "P".repeat(100);

            simpleRegisterDTO.setFirstName(name100);
            simpleRegisterDTO.setLastName(name100);
            simpleRegisterDTO.setEmail("very.long.email@very-long-domain.com");
            simpleRegisterDTO.setPassword(password100);

            Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("DTO with special characters in names should pass validation")
        void testSpecialCharactersInNames() {
            simpleRegisterDTO.setFirstName("José María");
            simpleRegisterDTO.setLastName("García-López");
            simpleRegisterDTO.setEmail("jose.maria@example.com");
            simpleRegisterDTO.setPassword("password123");

            Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(simpleRegisterDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Multiple valid DTOs should all pass validation")
        void testMultipleValidDTOs() {
            SimpleRegisterDTO[] dtos = {
                    new SimpleRegisterDTO("Juan", "Pérez", "juan@example.com", "password123"),
                    new SimpleRegisterDTO("María", "González", "maria@example.org", "securePass"),
                    new SimpleRegisterDTO("José Luis", "Rodríguez-Martín", "jose.luis@company.co.uk", "myPassword"),
                    new SimpleRegisterDTO("Ana", "López", "ana@domain.com", "strongPassword123")
            };

            for (SimpleRegisterDTO dto : dtos) {
                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty(),
                        "DTO with name=" + dto.getFirstName() + " " + dto.getLastName() + " should be valid");
            }
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("DTO should support common registration scenarios")
        void testCommonRegistrationScenarios() {
            // Scenario 1: Simple user registration
            SimpleRegisterDTO simpleUser = new SimpleRegisterDTO("Juan", "Pérez", "juan.perez@gmail.com",
                    "password123");

            // Scenario 2: User with compound names
            SimpleRegisterDTO compoundNames = new SimpleRegisterDTO("María José", "García López",
                    "maria.jose@company.com", "securePassword");

            // Scenario 3: User with special characters
            SimpleRegisterDTO specialChars = new SimpleRegisterDTO("José María", "Rodríguez-Martín",
                    "jose.maria@domain.org", "mySecretPass");

            Set<ConstraintViolation<SimpleRegisterDTO>> violations1 = validator.validate(simpleUser);
            Set<ConstraintViolation<SimpleRegisterDTO>> violations2 = validator.validate(compoundNames);
            Set<ConstraintViolation<SimpleRegisterDTO>> violations3 = validator.validate(specialChars);

            assertTrue(violations1.isEmpty());
            assertTrue(violations2.isEmpty());
            assertTrue(violations3.isEmpty());
        }

        @Test
        @DisplayName("DTO should handle international names correctly")
        void testInternationalNames() {
            SimpleRegisterDTO[] internationalUsers = {
                    new SimpleRegisterDTO("François", "Müller", "francois@example.com", "password123"),
                    new SimpleRegisterDTO("José", "García", "jose@example.com", "password123"),
                    new SimpleRegisterDTO("Ñoño", "Peña", "nono@example.com", "password123"),
                    new SimpleRegisterDTO("André", "Björk", "andre@example.com", "password123")
            };

            for (SimpleRegisterDTO dto : internationalUsers) {
                Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty(),
                        "International name " + dto.getFirstName() + " " + dto.getLastName() + " should be valid");
            }
        }

        @Test
        @DisplayName("DTO should maintain data integrity after multiple operations")
        void testDataIntegrityAfterOperations() {
            SimpleRegisterDTO dto = new SimpleRegisterDTO();

            // Initial assignment
            dto.setFirstName("Juan");
            dto.setLastName("Pérez");
            dto.setEmail("juan@example.com");
            dto.setPassword("password123");

            assertEquals("Juan", dto.getFirstName());
            assertEquals("Pérez", dto.getLastName());
            assertEquals("juan@example.com", dto.getEmail());
            assertEquals("password123", dto.getPassword());

            // Update assignment
            dto.setFirstName("Carlos");
            dto.setLastName("Rodríguez");
            dto.setEmail("carlos@example.com");
            dto.setPassword("newPassword456");

            assertEquals("Carlos", dto.getFirstName());
            assertEquals("Rodríguez", dto.getLastName());
            assertEquals("carlos@example.com", dto.getEmail());
            assertEquals("newPassword456", dto.getPassword());

            // Final validation
            Set<ConstraintViolation<SimpleRegisterDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty());
        }
    }
}
