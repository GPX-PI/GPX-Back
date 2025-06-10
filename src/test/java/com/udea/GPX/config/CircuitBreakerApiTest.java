package com.udea.GPX.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;

/**
 * Temporary test to check available methods in CircuitBreakerConfig
 */
class CircuitBreakerApiTest {

    @Test
    void checkAvailableMethods() {
        // Create basic circuit breaker to check available methods
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        CircuitBreaker circuitBreaker = registry.circuitBreaker("test");
        var config = circuitBreaker.getCircuitBreakerConfig();

        // Print all available methods (for debugging)
        System.out.println("Available methods in CircuitBreakerConfig:");
        for (var method : config.getClass().getMethods()) {
            if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                System.out.println("- " + method.getName() + "() -> " + method.getReturnType().getSimpleName());
            }
        }
    }
}
