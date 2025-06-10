package com.udea.GPX.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuración simple de Circuit Breaker para servicios externos
 * Sin over-engineering - solo lo esencial para proteger OAuth2
 */
@Configuration
public class CircuitBreakerConfig {

  private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerConfig.class);

  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry() {
    // Configuración básica y conservadora
    io.github.resilience4j.circuitbreaker.CircuitBreakerConfig config = io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
        .custom()
        .failureRateThreshold(50) // 50% de fallos para abrir circuito
        .waitDurationInOpenState(Duration.ofSeconds(30)) // Esperar 30s antes de probar
        .slidingWindowSize(10) // Evaluar últimas 10 llamadas
        .minimumNumberOfCalls(5) // Mínimo 5 llamadas antes de evaluar
        .permittedNumberOfCallsInHalfOpenState(3) // 3 llamadas de prueba
        .slowCallRateThreshold(90) // 90% de llamadas lentas es problema
        .slowCallDurationThreshold(Duration.ofSeconds(5)) // >5s es lento
        .build();

    CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);

    // Logging simple para monitoreo
    registry.circuitBreaker("oauth2").getEventPublisher()
        .onStateTransition(event -> logger.warn("🔌 Circuit Breaker OAuth2: {} -> {}",
            event.getStateTransition().getFromState(),
            event.getStateTransition().getToState()));

    return registry;
  }

  @Bean
  public CircuitBreaker oauth2CircuitBreaker(CircuitBreakerRegistry registry) {
    return registry.circuitBreaker("oauth2");
  }
}