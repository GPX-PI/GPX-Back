package com.udea.GPX.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CircuitBreakerConfig Configuration Tests")
@ExtendWith(MockitoExtension.class)
class CircuitBreakerConfigTest {

    private CircuitBreakerConfig circuitBreakerConfig;

    @BeforeEach
    void setUp() {
        circuitBreakerConfig = new CircuitBreakerConfig();
    }

    @Nested
    @DisplayName("Bean Creation Tests")
    class BeanCreationTests {

        @Test
        @DisplayName("Should create circuit breaker registry successfully")
        void shouldCreateCircuitBreakerRegistrySuccessfully() {
            // Given & When
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();

            // Then
            assertNotNull(registry, "CircuitBreakerRegistry should not be null");
            assertTrue(registry instanceof CircuitBreakerRegistry,
                    "Should return a valid CircuitBreakerRegistry instance");
        }

        @Test
        @DisplayName("Should create OAuth2 circuit breaker successfully")
        void shouldCreateOAuth2CircuitBreakerSuccessfully() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();

            // When
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // Then
            assertNotNull(circuitBreaker, "OAuth2 CircuitBreaker should not be null");
            assertTrue(circuitBreaker instanceof CircuitBreaker, "Should return a valid CircuitBreaker instance");
            assertEquals("oauth2", circuitBreaker.getName(), "CircuitBreaker should be named 'oauth2'");
        }

        @Test
        @DisplayName("Should create circuit breaker with correct basic configuration")
        void shouldCreateCircuitBreakerWithCorrectConfiguration() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            var config = circuitBreaker.getCircuitBreakerConfig();

            // Then - Test only the methods that we know exist
            assertEquals(50f, config.getFailureRateThreshold(), "Failure rate threshold should be 50%");
            assertEquals(10, config.getSlidingWindowSize(), "Sliding window size should be 10");
            assertEquals(5, config.getMinimumNumberOfCalls(), "Minimum number of calls should be 5");
            assertEquals(3, config.getPermittedNumberOfCallsInHalfOpenState(),
                    "Permitted calls in half-open should be 3");
            assertEquals(90f, config.getSlowCallRateThreshold(), "Slow call rate threshold should be 90%");
            assertEquals(Duration.ofSeconds(5), config.getSlowCallDurationThreshold(),
                    "Slow call duration should be 5 seconds");
        }

        @Test
        @DisplayName("Should register OAuth2 circuit breaker in registry")
        void shouldRegisterOAuth2CircuitBreakerInRegistry() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();

            // When
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // Then
            CircuitBreaker registeredCircuitBreaker = registry.circuitBreaker("oauth2");
            assertNotNull(registeredCircuitBreaker, "OAuth2 circuit breaker should be registered in registry");
            assertEquals(circuitBreaker, registeredCircuitBreaker, "Should return the same circuit breaker instance");
        }
    }

    @Nested
    @DisplayName("Configuration Values Tests")
    class ConfigurationValuesTests {

        @Test
        @DisplayName("Should have conservative failure rate threshold")
        void shouldHaveConservativeFailureRateThreshold() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            float failureRate = circuitBreaker.getCircuitBreakerConfig().getFailureRateThreshold();

            // Then
            assertEquals(50f, failureRate, "Should have conservative 50% failure rate threshold");
            assertTrue(failureRate >= 50f, "Should not be too aggressive with failure detection");
        }

        @Test
        @DisplayName("Should have basic configuration tests")
        void shouldHaveBasicConfigurationTests() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            var config = circuitBreaker.getCircuitBreakerConfig();

            // Then - Test basic configuration values
            assertEquals(50f, config.getFailureRateThreshold(), "Failure rate threshold should be 50%");
            assertEquals(10, config.getSlidingWindowSize(), "Sliding window size should be 10");
            assertEquals(5, config.getMinimumNumberOfCalls(), "Minimum number of calls should be 5");
            assertEquals(3, config.getPermittedNumberOfCallsInHalfOpenState(),
                    "Permitted calls in half-open should be 3");
            assertEquals(90f, config.getSlowCallRateThreshold(), "Slow call rate threshold should be 90%");
            assertEquals(Duration.ofSeconds(5), config.getSlowCallDurationThreshold(),
                    "Slow call duration should be 5 seconds");
        }

        @Test
        @DisplayName("Should have appropriate sliding window size")
        void shouldHaveAppropriateSlidingWindowSize() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            int slidingWindowSize = circuitBreaker.getCircuitBreakerConfig().getSlidingWindowSize();

            // Then
            assertEquals(10, slidingWindowSize, "Should evaluate last 10 calls");
            assertTrue(slidingWindowSize >= 5, "Should have enough samples for evaluation");
            assertTrue(slidingWindowSize <= 50, "Should not require too many samples");
        }

        @Test
        @DisplayName("Should have reasonable minimum number of calls")
        void shouldHaveReasonableMinimumNumberOfCalls() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            int minimumCalls = circuitBreaker.getCircuitBreakerConfig().getMinimumNumberOfCalls();

            // Then
            assertEquals(5, minimumCalls, "Should require minimum 5 calls before evaluation");
            assertTrue(minimumCalls > 0, "Should require some calls before evaluation");
            assertTrue(minimumCalls <= 10, "Should not require too many calls for evaluation");
        }

        @Test
        @DisplayName("Should allow reasonable number of calls in half-open state")
        void shouldAllowReasonableNumberOfCallsInHalfOpenState() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            int permittedCalls = circuitBreaker.getCircuitBreakerConfig().getPermittedNumberOfCallsInHalfOpenState();

            // Then
            assertEquals(3, permittedCalls, "Should allow 3 test calls in half-open state");
            assertTrue(permittedCalls > 0, "Should allow some test calls");
            assertTrue(permittedCalls <= 10, "Should not allow too many test calls");
        }

        @Test
        @DisplayName("Should have high slow call rate threshold")
        void shouldHaveHighSlowCallRateThreshold() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            float slowCallRate = circuitBreaker.getCircuitBreakerConfig().getSlowCallRateThreshold();

            // Then
            assertEquals(90f, slowCallRate, "Should consider 90% slow calls as problematic");
            assertTrue(slowCallRate >= 80f, "Should be tolerant of some slow calls");
        }

        @Test
        @DisplayName("Should have reasonable slow call duration threshold")
        void shouldHaveReasonableSlowCallDurationThreshold() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            Duration slowCallDuration = circuitBreaker.getCircuitBreakerConfig().getSlowCallDurationThreshold();

            // Then
            assertEquals(Duration.ofSeconds(5), slowCallDuration, "Should consider calls over 5 seconds as slow");
            assertTrue(slowCallDuration.getSeconds() >= 3, "Should allow for reasonable response times");
            assertTrue(slowCallDuration.getSeconds() <= 30, "Should not be too lenient with slow calls");
        }
    }

    @Nested
    @DisplayName("Circuit Breaker State Tests")
    class CircuitBreakerStateTests {

        @Test
        @DisplayName("Should start in closed state")
        void shouldStartInClosedState() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            CircuitBreaker.State state = circuitBreaker.getState();

            // Then
            assertEquals(CircuitBreaker.State.CLOSED, state, "Circuit breaker should start in CLOSED state");
        }

        @Test
        @DisplayName("Should have proper circuit breaker name")
        void shouldHaveProperCircuitBreakerName() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            String name = circuitBreaker.getName();

            // Then
            assertEquals("oauth2", name, "Circuit breaker should be named 'oauth2'");
        }

        @Test
        @DisplayName("Should provide access to metrics")
        void shouldProvideAccessToMetrics() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            var metrics = circuitBreaker.getMetrics();

            // Then
            assertNotNull(metrics, "Should provide access to metrics");
            assertEquals(0, metrics.getNumberOfFailedCalls(), "Should start with 0 failed calls");
            assertEquals(0, metrics.getNumberOfSuccessfulCalls(), "Should start with 0 successful calls");
        }
    }

    @Nested
    @DisplayName("Event Publisher Tests")
    class EventPublisherTests {

        @Test
        @DisplayName("Should have event publisher configured")
        void shouldHaveEventPublisherConfigured() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When
            var eventPublisher = circuitBreaker.getEventPublisher();

            // Then
            assertNotNull(eventPublisher, "Should have event publisher configured");
        }

        @Test
        @DisplayName("Should support event subscription")
        void shouldSupportEventSubscription() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When & Then
            assertDoesNotThrow(() -> {
                circuitBreaker.getEventPublisher().onStateTransition(event -> {
                    // Event handler logic would go here
                });
            }, "Should support state transition event subscription");
        }
    }

    @Nested
    @DisplayName("Registry Management Tests")
    class RegistryManagementTests {

        @Test
        @DisplayName("Should allow creating multiple circuit breakers")
        void shouldAllowCreatingMultipleCircuitBreakers() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();

            // When
            CircuitBreaker oauth2CB = registry.circuitBreaker("oauth2");
            CircuitBreaker anotherCB = registry.circuitBreaker("another-service");

            // Then
            assertNotNull(oauth2CB);
            assertNotNull(anotherCB);
            assertNotEquals(oauth2CB, anotherCB, "Should create different circuit breaker instances");
            assertEquals("oauth2", oauth2CB.getName());
            assertEquals("another-service", anotherCB.getName());
        }

        @Test
        @DisplayName("Should reuse circuit breaker instances for same name")
        void shouldReuseCircuitBreakerInstancesForSameName() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();

            // When
            CircuitBreaker circuitBreaker1 = registry.circuitBreaker("oauth2");
            CircuitBreaker circuitBreaker2 = registry.circuitBreaker("oauth2");

            // Then
            assertSame(circuitBreaker1, circuitBreaker2, "Should reuse same circuit breaker instance for same name");
        }

        @Test
        @DisplayName("Should allow getting all circuit breakers")
        void shouldAllowGettingAllCircuitBreakers() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            registry.circuitBreaker("oauth2");
            registry.circuitBreaker("test-service");

            // When
            var allCircuitBreakers = registry.getAllCircuitBreakers();

            // Then
            assertTrue(allCircuitBreakers.size() >= 2, "Should contain created circuit breakers");
            assertTrue(allCircuitBreakers.stream().anyMatch(cb -> "oauth2".equals(cb.getName())));
            assertTrue(allCircuitBreakers.stream().anyMatch(cb -> "test-service".equals(cb.getName())));
        }
    }

    @Nested
    @DisplayName("Configuration Class Tests")
    class ConfigurationClassTests {

        @Test
        @DisplayName("Should be annotated with @Configuration")
        void shouldBeAnnotatedWithConfiguration() {
            // Given & When
            boolean hasConfigurationAnnotation = CircuitBreakerConfig.class.isAnnotationPresent(
                    org.springframework.context.annotation.Configuration.class);

            // Then
            assertTrue(hasConfigurationAnnotation, "CircuitBreakerConfig should be annotated with @Configuration");
        }

        @Test
        @DisplayName("Should have circuitBreakerRegistry method annotated with @Bean")
        void shouldHaveCircuitBreakerRegistryMethodAnnotatedWithBean() throws NoSuchMethodException {
            // Given & When
            boolean hasRegistryBeanAnnotation = CircuitBreakerConfig.class
                    .getMethod("circuitBreakerRegistry")
                    .isAnnotationPresent(org.springframework.context.annotation.Bean.class);

            // Then
            assertTrue(hasRegistryBeanAnnotation, "circuitBreakerRegistry() method should be annotated with @Bean");
        }

        @Test
        @DisplayName("Should have oauth2CircuitBreaker method annotated with @Bean")
        void shouldHaveOAuth2CircuitBreakerMethodAnnotatedWithBean() throws NoSuchMethodException {
            // Given & When
            boolean hasOAuth2BeanAnnotation = CircuitBreakerConfig.class
                    .getMethod("oauth2CircuitBreaker", CircuitBreakerRegistry.class)
                    .isAnnotationPresent(org.springframework.context.annotation.Bean.class);

            // Then
            assertTrue(hasOAuth2BeanAnnotation, "oauth2CircuitBreaker() method should be annotated with @Bean");
        }
    }

    @Nested
    @DisplayName("Integration and Usage Tests")
    class IntegrationUsageTests {

        @Test
        @DisplayName("Should support OAuth2 service protection")
        void shouldSupportOAuth2ServiceProtection() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker oauth2CB = circuitBreakerConfig.oauth2CircuitBreaker(registry);

            // When & Then
            assertEquals("oauth2", oauth2CB.getName(), "Should be specifically configured for OAuth2");
            assertEquals(CircuitBreaker.State.CLOSED, oauth2CB.getState(), "Should start ready to accept calls");

            // Should support decorating OAuth2 calls
            assertDoesNotThrow(() -> {
                var supplier = CircuitBreaker.decorateSupplier(oauth2CB, () -> "OAuth2 call result");
                assertNotNull(supplier, "Should be able to decorate OAuth2 calls");
            });
        }

        @Test
        @DisplayName("Should provide conservative configuration for external services")
        void shouldProvideConservativeConfigurationForExternalServices() {
            // Given
            CircuitBreakerRegistry registry = circuitBreakerConfig.circuitBreakerRegistry();
            CircuitBreaker circuitBreaker = circuitBreakerConfig.oauth2CircuitBreaker(registry);
            var config = circuitBreaker.getCircuitBreakerConfig();

            // When & Then - Verify conservative settings
            assertEquals(50f, config.getFailureRateThreshold(), "Should be conservative with failure rate");
            assertEquals(5, config.getMinimumNumberOfCalls(), "Should require reasonable sample size");
            assertEquals(Duration.ofSeconds(5), config.getSlowCallDurationThreshold(),
                    "Should be reasonable with timeouts");
        }
    }
}
