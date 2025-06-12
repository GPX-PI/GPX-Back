package com.udea.gpx.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración para procesamiento asíncrono
 */
@Configuration
@EnableAsync
public class AsyncConfig {

  private static final Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

  /**
   * Executor para operaciones de archivos (upload, procesamiento)
   */
  @Bean(name = "fileProcessingExecutor")
  public Executor fileProcessingExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(5);
    executor.setQueueCapacity(50);
    executor.setThreadNamePrefix("FileProcessing-");
    executor.setRejectedExecutionHandler((r, ex) -> {
      // Log de rechazo - se podría agregar logging aquí
      throw new RuntimeException("File processing task was rejected: " + r.toString());
    });
    executor.initialize();
    return executor;
  }

  /**
   * Executor para operaciones de email y notificaciones
   */
  @Bean(name = "notificationExecutor")
  public Executor notificationExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(3);
    executor.setQueueCapacity(25);
    executor.setThreadNamePrefix("Notification-");
    executor.setRejectedExecutionHandler((r, ex) ->
    // Para notificaciones, es mejor loggear y continuar sin fallar
    logger.warn("Notification task was rejected: {}", r));
    executor.initialize();
    return executor;
  }

  /**
   * Executor para cálculos pesados (clasificaciones, estadísticas)
   */
  @Bean(name = "calculationExecutor")
  public Executor calculationExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("Calculation-");
    executor.setRejectedExecutionHandler((r, ex) -> {
      throw new RuntimeException("Calculation task was rejected: " + r.toString());
    });
    executor.initialize();
    return executor;
  }

  /**
   * Executor general para tareas asíncronas diversas
   */
  @Bean(name = "generalAsyncExecutor")
  public Executor generalAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(3);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(200);
    executor.setThreadNamePrefix("GeneralAsync-");
    executor.setKeepAliveSeconds(60);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    executor.initialize();
    return executor;
  }
}