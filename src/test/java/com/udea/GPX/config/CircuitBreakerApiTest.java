package com.udea.gpx.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Temporary test to check available methods in CircuitBreakerConfig
 */
class CircuitBreakerApiTest {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerApiTest.class);

    @Test
    void checkAvailableMethods() {
        // Create basic circuit breaker to check available methods
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();
        CircuitBreaker circuitBreaker = registry.circuitBreaker("test");
        var config = circuitBreaker.getCircuitBreakerConfig(); // Print all available methods (for debugging)
        logger.info("Available methods in CircuitBreakerConfig:");
        for (var method : config.getClass().getMethods()) {
            if (method.getName().startsWith("get") && method.getParameterCount() == 0) {
                logger.info("- {} -> {}", method.getName() + "()", method.getReturnType().getSimpleName());
            }
        }
    }
}
