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

@DisplayName("LoginRequestDTO Tests")
class LoginRequestDTOTest {

    private Validator validator;
    private LoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        loginRequestDTO = new LoginRequestDTO();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor should create object with null values")
        void testDefaultConstructor() {
            LoginRequestDTO dto = new LoginRequestDTO();

            assertNull(dto.getEmail());
            assertNull(dto.getPassword());
        }

        @Test
        @DisplayName("Parameterized constructor should set all values correctly")
        void testParameterizedConstructor() {
            String email = "test@example.com";
            String password = "password123";

            LoginRequestDTO dto = new LoginRequestDTO(email, password);

            assertEquals(email, dto.getEmail());
            assertEquals(password, dto.getPassword());
        }

        @Test
        @DisplayName("Constructor with valid email and minimum password should work")
        void testConstructorWithValidEmailAndMinPassword() {
            String email = "user@domain.com";
            String password = "123456"; // Minimum 6 characters

            LoginRequestDTO dto = new LoginRequestDTO(email, password);

            assertEquals(email, dto.getEmail());
            assertEquals(password, dto.getPassword());
        }

        @Test
        @DisplayName("Constructor with empty strings should work (validation happens later)")
        void testConstructorWithEmptyStrings() {
            LoginRequestDTO dto = new LoginRequestDTO("", "");

            assertEquals("", dto.getEmail());
            assertEquals("", dto.getPassword());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Email getter and setter should work correctly")
        void testEmailGetterSetter() {
            String email = "test@example.com";
            loginRequestDTO.setEmail(email);
            assertEquals(email, loginRequestDTO.getEmail());
        }

        @Test
        @DisplayName("Password getter and setter should work correctly")
        void testPasswordGetterSetter() {
            String password = "password123";
            loginRequestDTO.setPassword(password);
            assertEquals(password, loginRequestDTO.getPassword());
        }

        @Test
        @DisplayName("Setting null values should work correctly")
        void testSettingNullValues() {
            loginRequestDTO.setEmail(null);
            loginRequestDTO.setPassword(null);

            assertNull(loginRequestDTO.getEmail());
            assertNull(loginRequestDTO.getPassword());
        }

        @Test
        @DisplayName("Setting empty strings should work correctly")
        void testSettingEmptyStrings() {
            loginRequestDTO.setEmail("");
            loginRequestDTO.setPassword("");

            assertEquals("", loginRequestDTO.getEmail());
            assertEquals("", loginRequestDTO.getPassword());
        }

        @Test
        @DisplayName("Setting and getting same values should work correctly")
        void testSettingAndGettingSameValues() {
            String email = "user@domain.org";
            String password = "securePassword123";

            loginRequestDTO.setEmail(email);
            loginRequestDTO.setPassword(password);

            assertEquals(email, loginRequestDTO.getEmail());
            assertEquals(password, loginRequestDTO.getPassword());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Valid DTO should pass validation")
        void testValidDTO() {
            loginRequestDTO.setEmail("test@example.com");
            loginRequestDTO.setPassword("password123");

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Nested
        @DisplayName("Email Validation")
        class EmailValidation {

            @Test
            @DisplayName("Null email should fail validation")
            void testNullEmail() {
                loginRequestDTO.setEmail(null);
                loginRequestDTO.setPassword("password123");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El email es requerido")));
            }

            @Test
            @DisplayName("Empty email should fail validation")
            void testEmptyEmail() {
                loginRequestDTO.setEmail("");
                loginRequestDTO.setPassword("password123");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El email es requerido")));
            }

            @Test
            @DisplayName("Blank email should fail validation")
            void testBlankEmail() {
                loginRequestDTO.setEmail("   ");
                loginRequestDTO.setPassword("password123");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("El email es requerido")));
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "invalid-email",
                    "user@",
                    "@domain.com",
                    "user@domain",
                    "user.domain.com",
                    "user@.com",
                    "user@domain.",
                    "user name@domain.com",
                    "user@domain .com"
            })
            @DisplayName("Invalid email formats should fail validation")
            void testInvalidEmailFormats(String invalidEmail) {
                loginRequestDTO.setEmail(invalidEmail);
                loginRequestDTO.setPassword("password123");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertFalse(violations.isEmpty(), "Should have validation violations for email: " + invalidEmail);

                boolean hasEmailViolation = violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
                assertTrue(hasEmailViolation, "Should have email validation violation for: " + invalidEmail);
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "user@domain.com",
                    "test@example.org",
                    "admin@company.co.uk",
                    "user123@test-domain.com",
                    "user.name@domain.com",
                    "user+tag@domain.com",
                    "user_name@domain-name.com",
                    "simple@domain.co"
            })
            @DisplayName("Valid email formats should pass validation")
            void testValidEmailFormats(String validEmail) {
                loginRequestDTO.setEmail(validEmail);
                loginRequestDTO.setPassword("password123");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("Password Validation")
        class PasswordValidation {

            @Test
            @DisplayName("Null password should fail validation")
            void testNullPassword() {
                loginRequestDTO.setEmail("test@example.com");
                loginRequestDTO.setPassword(null);

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("La contraseña es requerida")));
            }

            @Test
            @DisplayName("Empty password should fail validation")
            void testEmptyPassword() {
                loginRequestDTO.setEmail("test@example.com");
                loginRequestDTO.setPassword("");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("La contraseña es requerida")));
            }

            @Test
            @DisplayName("Blank password should fail validation")
            void testBlankPassword() {
                loginRequestDTO.setEmail("test@example.com");
                loginRequestDTO.setPassword("   ");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("La contraseña es requerida")));
            }

            @ParameterizedTest
            @ValueSource(strings = { "1", "12", "123", "1234", "12345" })
            @DisplayName("Password with less than 6 characters should fail validation")
            void testPasswordTooShort(String shortPassword) {
                loginRequestDTO.setEmail("test@example.com");
                loginRequestDTO.setPassword(shortPassword);

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertFalse(violations.isEmpty());
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("La contraseña debe tener al menos 6 caracteres")));
            }

            @ParameterizedTest
            @ValueSource(strings = {
                    "123456",
                    "password",
                    "password123",
                    "securePassword",
                    "veryLongPasswordWithManyCharacters123456789"
            })
            @DisplayName("Password with 6 or more characters should pass validation")
            void testValidPasswordLength(String validPassword) {
                loginRequestDTO.setEmail("test@example.com");
                loginRequestDTO.setPassword(validPassword);

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertTrue(violations.isEmpty());
            }

            @Test
            @DisplayName("Password with exactly 6 characters should pass validation")
            void testPasswordExactlyMinimumLength() {
                loginRequestDTO.setEmail("test@example.com");
                loginRequestDTO.setPassword("123456");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertTrue(violations.isEmpty());
            }

            @Test
            @DisplayName("Password with special characters should pass validation")
            void testPasswordWithSpecialCharacters() {
                loginRequestDTO.setEmail("test@example.com");
                loginRequestDTO.setPassword("password!@#$%^&*()");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertTrue(violations.isEmpty());
            }

            @Test
            @DisplayName("Password with spaces should pass validation if length is sufficient")
            void testPasswordWithSpaces() {
                loginRequestDTO.setEmail("test@example.com");
                loginRequestDTO.setPassword("pass word");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertTrue(violations.isEmpty());
            }
        }

        @Nested
        @DisplayName("Combined Validation")
        class CombinedValidation {
            @Test
            @DisplayName("Both fields null should produce multiple validation errors")
            void testBothFieldsNull() {
                loginRequestDTO.setEmail(null);
                loginRequestDTO.setPassword(null);

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                // Custom @Sanitized annotations create additional validations beyond @NotBlank
                assertTrue(violations.size() >= 2, "Should have at least 2 validation errors");

                boolean hasEmailError = violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
                boolean hasPasswordError = violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("password"));

                assertTrue(hasEmailError, "Should have email validation error");
                assertTrue(hasPasswordError, "Should have password validation error");
            }

            @Test
            @DisplayName("Both fields empty should produce multiple validation errors")
            void testBothFieldsEmpty() {
                loginRequestDTO.setEmail("");
                loginRequestDTO.setPassword("");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                // Custom @Sanitized annotations create additional validations beyond @NotBlank
                assertTrue(violations.size() >= 2, "Should have at least 2 validation errors");

                boolean hasEmailError = violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
                boolean hasPasswordError = violations.stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("password"));

                assertTrue(hasEmailError, "Should have email validation error");
                assertTrue(hasPasswordError, "Should have password validation error");
            }

            @Test
            @DisplayName("Invalid email and short password should produce multiple errors")
            void testInvalidEmailAndShortPassword() {
                loginRequestDTO.setEmail("invalid-email");
                loginRequestDTO.setPassword("123");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertTrue(violations.size() >= 2);

                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("El email debe tener un formato válido")));
                assertTrue(violations.stream()
                        .anyMatch(v -> v.getMessage().equals("La contraseña debe tener al menos 6 caracteres")));
            }

            @Test
            @DisplayName("Valid email and valid password should pass all validations")
            void testValidEmailAndPassword() {
                loginRequestDTO.setEmail("user@example.com");
                loginRequestDTO.setPassword("securePassword123");

                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
                assertTrue(violations.isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Very long valid email should pass validation")
        void testVeryLongValidEmail() {
            String longEmail = "very.long.email.address.with.many.dots@very-long-domain-name.organization.com";
            loginRequestDTO.setEmail(longEmail);
            loginRequestDTO.setPassword("password123");

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Very long valid password should pass validation")
        void testVeryLongValidPassword() {
            String longPassword = "thisIsAVeryLongPasswordWithManyCharactersThatShouldStillPassValidation123456789";
            loginRequestDTO.setEmail("test@example.com");
            loginRequestDTO.setPassword(longPassword);

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("International domain email should pass validation")
        void testInternationalDomainEmail() {
            loginRequestDTO.setEmail("user@example.co.uk");
            loginRequestDTO.setPassword("password123");

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Numeric email username should pass validation")
        void testNumericEmailUsername() {
            loginRequestDTO.setEmail("123456@example.com");
            loginRequestDTO.setPassword("password123");

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Password with only numbers should pass validation if long enough")
        void testNumericPassword() {
            loginRequestDTO.setEmail("test@example.com");
            loginRequestDTO.setPassword("123456789");

            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(loginRequestDTO);
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Multiple valid DTOs should all pass validation")
        void testMultipleValidDTOs() {
            LoginRequestDTO[] dtos = {
                    new LoginRequestDTO("user1@domain.com", "password123"),
                    new LoginRequestDTO("admin@company.org", "securePass"),
                    new LoginRequestDTO("test@example.co.uk", "123456"),
                    new LoginRequestDTO("user.name@domain.com", "myPassword"),
                    new LoginRequestDTO("user+tag@domain.com", "strongPassword123")
            };

            for (LoginRequestDTO dto : dtos) {
                Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
                assertTrue(violations.isEmpty(),
                        "DTO with email=" + dto.getEmail() + " should be valid");
            }
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("DTO should support common login scenarios")
        void testCommonLoginScenarios() {
            // Scenario 1: Regular user login
            LoginRequestDTO regularUser = new LoginRequestDTO("user@example.com", "userPassword");

            // Scenario 2: Admin login
            LoginRequestDTO adminUser = new LoginRequestDTO("admin@company.com", "adminSecurePassword123");

            // Scenario 3: User with complex email
            LoginRequestDTO complexEmail = new LoginRequestDTO("user.name+tag@company-domain.co.uk", "password123");

            Set<ConstraintViolation<LoginRequestDTO>> violations1 = validator.validate(regularUser);
            Set<ConstraintViolation<LoginRequestDTO>> violations2 = validator.validate(adminUser);
            Set<ConstraintViolation<LoginRequestDTO>> violations3 = validator.validate(complexEmail);

            assertTrue(violations1.isEmpty());
            assertTrue(violations2.isEmpty());
            assertTrue(violations3.isEmpty());
        }

        @Test
        @DisplayName("DTO should maintain data integrity after multiple operations")
        void testDataIntegrityAfterOperations() {
            LoginRequestDTO dto = new LoginRequestDTO();

            // Use simpler email format that should pass the custom sanitizer
            dto.setEmail("test@domain.com");
            dto.setPassword("testPassword");
            assertEquals("test@domain.com", dto.getEmail());
            assertEquals("testPassword", dto.getPassword());

            // Update assignment with simple valid values
            dto.setEmail("user@example.com");
            dto.setPassword("userPassword123");
            assertEquals("user@example.com", dto.getEmail());
            assertEquals("userPassword123", dto.getPassword());

            // Final validation
            Set<ConstraintViolation<LoginRequestDTO>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                System.out.println("Validation violations found:");
                for (ConstraintViolation<LoginRequestDTO> violation : violations) {
                    System.out.println(
                            "Property: " + violation.getPropertyPath() + ", Message: " + violation.getMessage());
                }
            }
            assertTrue(violations.isEmpty(),
                    "DTO should be valid after updates. Found " + violations.size() + " violations");
        }

        @Test
        @DisplayName("DTO should handle case sensitivity correctly")
        void testCaseSensitivity() {
            // Emails are typically case-insensitive in validation but case-sensitive in
            // storage
            LoginRequestDTO upperCaseEmail = new LoginRequestDTO("USER@EXAMPLE.COM", "password123");
            LoginRequestDTO lowerCaseEmail = new LoginRequestDTO("user@example.com", "password123");
            LoginRequestDTO mixedCaseEmail = new LoginRequestDTO("User@Example.Com", "password123");

            Set<ConstraintViolation<LoginRequestDTO>> violations1 = validator.validate(upperCaseEmail);
            Set<ConstraintViolation<LoginRequestDTO>> violations2 = validator.validate(lowerCaseEmail);
            Set<ConstraintViolation<LoginRequestDTO>> violations3 = validator.validate(mixedCaseEmail);

            assertTrue(violations1.isEmpty());
            assertTrue(violations2.isEmpty());
            assertTrue(violations3.isEmpty());

            // Verify values are preserved as entered
            assertEquals("USER@EXAMPLE.COM", upperCaseEmail.getEmail());
            assertEquals("user@example.com", lowerCaseEmail.getEmail());
            assertEquals("User@Example.Com", mixedCaseEmail.getEmail());
        }
    }
}
