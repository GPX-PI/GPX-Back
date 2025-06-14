package com.udea.gpx.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtProperties Configuration Tests")
class JwtPropertiesTest {

    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
    }

    @Nested
    @DisplayName("Default Values Tests")
    class DefaultValuesTests {

        @Test
        @DisplayName("Should have correct default secret")
        void shouldHaveCorrectDefaultSecret() {
            // Given & When
            String defaultSecret = jwtProperties.getSecret();

            // Then
            assertEquals("tu-clave-secreta-super-larga-y-segura-para-jwt-tokens", defaultSecret);
            assertNotNull(defaultSecret);
            assertFalse(defaultSecret.isEmpty());
        }

        @Test
        @DisplayName("Should have correct default expiration seconds")
        void shouldHaveCorrectDefaultExpirationSeconds() {
            // Given & When
            long defaultExpiration = jwtProperties.getExpirationSeconds();

            // Then
            assertEquals(36000L, defaultExpiration); // 10 hours
        }

        @Test
        @DisplayName("Should have correct default refresh expiration seconds")
        void shouldHaveCorrectDefaultRefreshExpirationSeconds() {
            // Given & When
            long defaultRefreshExpiration = jwtProperties.getRefreshExpirationSeconds();

            // Then
            assertEquals(604800L, defaultRefreshExpiration); // 7 days
        }

        @Test
        @DisplayName("Should have correct default max concurrent sessions")
        void shouldHaveCorrectDefaultMaxConcurrentSessions() {
            // Given & When
            int defaultMaxSessions = jwtProperties.getMaxConcurrentSessions();

            // Then
            assertEquals(5, defaultMaxSessions);
        }

        @Test
        @DisplayName("Should have correct default refresh token rotation setting")
        void shouldHaveCorrectDefaultRefreshTokenRotation() {
            // Given & When
            boolean defaultRotation = jwtProperties.isAllowRefreshTokenRotation();

            // Then
            assertTrue(defaultRotation);
        }

        @Test
        @DisplayName("Should have correct default blacklist setting")
        void shouldHaveCorrectDefaultBlacklistSetting() {
            // Given & When
            boolean defaultBlacklist = jwtProperties.isEnableBlacklist();

            // Then
            assertTrue(defaultBlacklist);
        }

        @Test
        @DisplayName("Should have correct default session timeout seconds")
        void shouldHaveCorrectDefaultSessionTimeoutSeconds() {
            // Given & When
            long defaultSessionTimeout = jwtProperties.getSessionTimeoutSeconds();

            // Then
            assertEquals(7200L, defaultSessionTimeout); // 2 hours
        }

        @Test
        @DisplayName("Should have correct default max session duration seconds")
        void shouldHaveCorrectDefaultMaxSessionDurationSeconds() {
            // Given & When
            long defaultMaxSessionDuration = jwtProperties.getMaxSessionDurationSeconds();

            // Then
            assertEquals(28800L, defaultMaxSessionDuration); // 8 hours
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get secret correctly")
        void shouldSetAndGetSecretCorrectly() {
            // Given
            String newSecret = "new-super-secret-key-for-testing";

            // When
            jwtProperties.setSecret(newSecret);

            // Then
            assertEquals(newSecret, jwtProperties.getSecret());
        }

        @Test
        @DisplayName("Should set and get expiration seconds correctly")
        void shouldSetAndGetExpirationSecondsCorrectly() {
            // Given
            long newExpiration = 7200L;

            // When
            jwtProperties.setExpirationSeconds(newExpiration);

            // Then
            assertEquals(newExpiration, jwtProperties.getExpirationSeconds());
        }

        @Test
        @DisplayName("Should set and get refresh expiration seconds correctly")
        void shouldSetAndGetRefreshExpirationSecondsCorrectly() {
            // Given
            long newRefreshExpiration = 1209600L; // 14 days

            // When
            jwtProperties.setRefreshExpirationSeconds(newRefreshExpiration);

            // Then
            assertEquals(newRefreshExpiration, jwtProperties.getRefreshExpirationSeconds());
        }

        @Test
        @DisplayName("Should set and get max concurrent sessions correctly")
        void shouldSetAndGetMaxConcurrentSessionsCorrectly() {
            // Given
            int newMaxSessions = 10;

            // When
            jwtProperties.setMaxConcurrentSessions(newMaxSessions);

            // Then
            assertEquals(newMaxSessions, jwtProperties.getMaxConcurrentSessions());
        }

        @Test
        @DisplayName("Should set and get refresh token rotation correctly")
        void shouldSetAndGetRefreshTokenRotationCorrectly() {
            // Given
            boolean newRotationSetting = false;

            // When
            jwtProperties.setAllowRefreshTokenRotation(newRotationSetting);

            // Then
            assertEquals(newRotationSetting, jwtProperties.isAllowRefreshTokenRotation());
        }

        @Test
        @DisplayName("Should set and get blacklist setting correctly")
        void shouldSetAndGetBlacklistSettingCorrectly() {
            // Given
            boolean newBlacklistSetting = false;

            // When
            jwtProperties.setEnableBlacklist(newBlacklistSetting);

            // Then
            assertEquals(newBlacklistSetting, jwtProperties.isEnableBlacklist());
        }

        @Test
        @DisplayName("Should set and get session timeout seconds correctly")
        void shouldSetAndGetSessionTimeoutSecondsCorrectly() {
            // Given
            long newSessionTimeout = 14400L; // 4 hours

            // When
            jwtProperties.setSessionTimeoutSeconds(newSessionTimeout);

            // Then
            assertEquals(newSessionTimeout, jwtProperties.getSessionTimeoutSeconds());
        }

        @Test
        @DisplayName("Should set and get max session duration seconds correctly")
        void shouldSetAndGetMaxSessionDurationSecondsCorrectly() {
            // Given
            long newMaxSessionDuration = 43200L; // 12 hours

            // When
            jwtProperties.setMaxSessionDurationSeconds(newMaxSessionDuration);

            // Then
            assertEquals(newMaxSessionDuration, jwtProperties.getMaxSessionDurationSeconds());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null secret gracefully")
        void shouldHandleNullSecretGracefully() {
            // Given & When
            jwtProperties.setSecret(null);

            // Then
            assertNull(jwtProperties.getSecret());
        }

        @Test
        @DisplayName("Should handle empty secret")
        void shouldHandleEmptySecret() {
            // Given
            String emptySecret = "";

            // When
            jwtProperties.setSecret(emptySecret);

            // Then
            assertEquals(emptySecret, jwtProperties.getSecret());
        }

        @Test
        @DisplayName("Should handle very long secret")
        void shouldHandleVeryLongSecret() {
            // Given
            String longSecret = "a".repeat(1000);

            // When
            jwtProperties.setSecret(longSecret);

            // Then
            assertEquals(longSecret, jwtProperties.getSecret());
            assertEquals(1000, jwtProperties.getSecret().length());
        }

        @Test
        @DisplayName("Should handle zero expiration seconds")
        void shouldHandleZeroExpirationSeconds() {
            // Given
            long zeroExpiration = 0L;

            // When
            jwtProperties.setExpirationSeconds(zeroExpiration);

            // Then
            assertEquals(zeroExpiration, jwtProperties.getExpirationSeconds());
        }

        @Test
        @DisplayName("Should handle negative expiration seconds")
        void shouldHandleNegativeExpirationSeconds() {
            // Given
            long negativeExpiration = -3600L;

            // When
            jwtProperties.setExpirationSeconds(negativeExpiration);

            // Then
            assertEquals(negativeExpiration, jwtProperties.getExpirationSeconds());
        }

        @Test
        @DisplayName("Should handle very large expiration seconds")
        void shouldHandleVeryLargeExpirationSeconds() {
            // Given
            long largeExpiration = Long.MAX_VALUE;

            // When
            jwtProperties.setExpirationSeconds(largeExpiration);

            // Then
            assertEquals(largeExpiration, jwtProperties.getExpirationSeconds());
        }

        @Test
        @DisplayName("Should handle zero max concurrent sessions")
        void shouldHandleZeroMaxConcurrentSessions() {
            // Given
            int zeroSessions = 0;

            // When
            jwtProperties.setMaxConcurrentSessions(zeroSessions);

            // Then
            assertEquals(zeroSessions, jwtProperties.getMaxConcurrentSessions());
        }

        @Test
        @DisplayName("Should handle negative max concurrent sessions")
        void shouldHandleNegativeMaxConcurrentSessions() {
            // Given
            int negativeSessions = -5;

            // When
            jwtProperties.setMaxConcurrentSessions(negativeSessions);

            // Then
            assertEquals(negativeSessions, jwtProperties.getMaxConcurrentSessions());
        }

        @Test
        @DisplayName("Should handle very large max concurrent sessions")
        void shouldHandleVeryLargeMaxConcurrentSessions() {
            // Given
            int largeSessions = Integer.MAX_VALUE;

            // When
            jwtProperties.setMaxConcurrentSessions(largeSessions);

            // Then
            assertEquals(largeSessions, jwtProperties.getMaxConcurrentSessions());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should have refresh expiration longer than access token expiration")
        void shouldHaveRefreshExpirationLongerThanAccessTokenExpiration() {
            // Given & When
            long accessTokenExpiration = jwtProperties.getExpirationSeconds();
            long refreshTokenExpiration = jwtProperties.getRefreshExpirationSeconds();

            // Then
            assertTrue(refreshTokenExpiration > accessTokenExpiration,
                    "Refresh token expiration should be longer than access token expiration");
        }

        @Test
        @DisplayName("Should have session timeout less than or equal to max session duration")
        void shouldHaveSessionTimeoutLessOrEqualToMaxSessionDuration() {
            // Given & When
            long sessionTimeout = jwtProperties.getSessionTimeoutSeconds();
            long maxSessionDuration = jwtProperties.getMaxSessionDurationSeconds();

            // Then
            assertTrue(sessionTimeout <= maxSessionDuration,
                    "Session timeout should be less than or equal to max session duration");
        }

        @Test
        @DisplayName("Should allow reasonable concurrent sessions by default")
        void shouldAllowReasonableConcurrentSessionsByDefault() {
            // Given & When
            int maxSessions = jwtProperties.getMaxConcurrentSessions();

            // Then
            assertTrue(maxSessions > 0, "Should allow at least one concurrent session");
            assertTrue(maxSessions <= 100, "Should not allow excessive concurrent sessions by default");
        }

        @Test
        @DisplayName("Should have security features enabled by default")
        void shouldHaveSecurityFeaturesEnabledByDefault() {
            // Given & When & Then
            assertTrue(jwtProperties.isAllowRefreshTokenRotation(),
                    "Refresh token rotation should be enabled for security");
            assertTrue(jwtProperties.isEnableBlacklist(),
                    "Token blacklist should be enabled for security");
        }

        @Test
        @DisplayName("Should support disabling security features")
        void shouldSupportDisablingSecurityFeatures() {
            // Given
            jwtProperties.setAllowRefreshTokenRotation(false);
            jwtProperties.setEnableBlacklist(false);

            // When & Then
            assertFalse(jwtProperties.isAllowRefreshTokenRotation());
            assertFalse(jwtProperties.isEnableBlacklist());
        }

        @Test
        @DisplayName("Should support custom JWT configuration scenarios")
        void shouldSupportCustomJwtConfigurationScenarios() {
            // Given - Short-lived tokens for high security
            jwtProperties.setExpirationSeconds(900L); // 15 minutes
            jwtProperties.setRefreshExpirationSeconds(3600L); // 1 hour
            jwtProperties.setMaxConcurrentSessions(3);
            jwtProperties.setSessionTimeoutSeconds(1800L); // 30 minutes
            jwtProperties.setMaxSessionDurationSeconds(7200L); // 2 hours

            // When & Then
            assertEquals(900L, jwtProperties.getExpirationSeconds());
            assertEquals(3600L, jwtProperties.getRefreshExpirationSeconds());
            assertEquals(3, jwtProperties.getMaxConcurrentSessions());
            assertEquals(1800L, jwtProperties.getSessionTimeoutSeconds());
            assertEquals(7200L, jwtProperties.getMaxSessionDurationSeconds());

            // Verify relationships still make sense
            assertTrue(jwtProperties.getRefreshExpirationSeconds() > jwtProperties.getExpirationSeconds());
            assertTrue(jwtProperties.getMaxSessionDurationSeconds() >= jwtProperties.getSessionTimeoutSeconds());
        }
    }

    @Nested
    @DisplayName("Configuration Properties Tests")
    class ConfigurationPropertiesTests {

        @Test
        @DisplayName("Should be annotated with @Configuration")
        void shouldBeAnnotatedWithConfiguration() {
            // Given & When
            boolean hasConfigurationAnnotation = JwtProperties.class
                    .isAnnotationPresent(org.springframework.context.annotation.Configuration.class);

            // Then
            assertTrue(hasConfigurationAnnotation, "JwtProperties should be annotated with @Configuration");
        }

        @Test
        @DisplayName("Should be annotated with @ConfigurationProperties")
        void shouldBeAnnotatedWithConfigurationProperties() {
            // Given & When
            boolean hasConfigPropsAnnotation = JwtProperties.class
                    .isAnnotationPresent(org.springframework.boot.context.properties.ConfigurationProperties.class);

            // Then
            assertTrue(hasConfigPropsAnnotation, "JwtProperties should be annotated with @ConfigurationProperties");
        }

        @Test
        @DisplayName("Should have correct ConfigurationProperties prefix")
        void shouldHaveCorrectConfigurationPropertiesPrefix() {
            // Given & When
            org.springframework.boot.context.properties.ConfigurationProperties annotation = JwtProperties.class
                    .getAnnotation(org.springframework.boot.context.properties.ConfigurationProperties.class);

            // Then
            assertNotNull(annotation);
            assertEquals("jwt", annotation.prefix(), "ConfigurationProperties prefix should be 'jwt'");
        }
    }
}
