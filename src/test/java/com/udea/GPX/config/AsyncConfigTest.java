package com.udea.gpx.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AsyncConfig Tests")
class AsyncConfigTest {

    private AsyncConfig asyncConfig;

    @BeforeEach
    void setUp() {
        asyncConfig = new AsyncConfig();
    }

    @Test
    @DisplayName("fileProcessingExecutor debe crear ThreadPoolTaskExecutor configurado correctamente")
    void testFileProcessingExecutorConfiguration() {
        Executor executor = asyncConfig.fileProcessingExecutor();

        assertNotNull(executor);
        assertTrue(executor instanceof ThreadPoolTaskExecutor);

        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(2, taskExecutor.getCorePoolSize());
        assertEquals(5, taskExecutor.getMaxPoolSize());
        assertEquals(50, taskExecutor.getQueueCapacity());
        assertTrue(taskExecutor.getThreadNamePrefix().startsWith("FileProcessing-"));
    }

    @Test
    @DisplayName("notificationExecutor debe crear ThreadPoolTaskExecutor configurado correctamente")
    void testNotificationExecutorConfiguration() {
        Executor executor = asyncConfig.notificationExecutor();

        assertNotNull(executor);
        assertTrue(executor instanceof ThreadPoolTaskExecutor);

        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(1, taskExecutor.getCorePoolSize());
        assertEquals(3, taskExecutor.getMaxPoolSize());
        assertEquals(25, taskExecutor.getQueueCapacity());
        assertTrue(taskExecutor.getThreadNamePrefix().startsWith("Notification-"));
    }

    @Test
    @DisplayName("calculationExecutor debe crear ThreadPoolTaskExecutor configurado correctamente")
    void testCalculationExecutorConfiguration() {
        Executor executor = asyncConfig.calculationExecutor();

        assertNotNull(executor);
        assertTrue(executor instanceof ThreadPoolTaskExecutor);

        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(2, taskExecutor.getCorePoolSize());
        assertEquals(4, taskExecutor.getMaxPoolSize());
        assertEquals(100, taskExecutor.getQueueCapacity());
        assertTrue(taskExecutor.getThreadNamePrefix().startsWith("Calculation-"));
    }

    @Test
    @DisplayName("generalAsyncExecutor debe crear ThreadPoolTaskExecutor configurado correctamente")
    void testGeneralAsyncExecutorConfiguration() {
        Executor executor = asyncConfig.generalAsyncExecutor();

        assertNotNull(executor);
        assertTrue(executor instanceof ThreadPoolTaskExecutor);

        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(3, taskExecutor.getCorePoolSize());
        assertEquals(8, taskExecutor.getMaxPoolSize());
        assertEquals(200, taskExecutor.getQueueCapacity());
        assertTrue(taskExecutor.getThreadNamePrefix().startsWith("GeneralAsync-"));
        assertEquals(60, taskExecutor.getKeepAliveSeconds());
    }

    @Test
    @DisplayName("fileProcessingExecutor debe estar inicializado correctamente")
    void testFileProcessingExecutorInitialization() {
        Executor executor = asyncConfig.fileProcessingExecutor();
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;

        // Verificar que el executor está inicializado y configurado
        assertNotNull(taskExecutor);
        assertNotNull(taskExecutor.getThreadNamePrefix());
        assertTrue(taskExecutor.getCorePoolSize() > 0);
    }

    @Test
    @DisplayName("notificationExecutor debe estar inicializado correctamente")
    void testNotificationExecutorInitialization() {
        Executor executor = asyncConfig.notificationExecutor();
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;

        // Verificar que el executor está inicializado y configurado
        assertNotNull(taskExecutor);
        assertNotNull(taskExecutor.getThreadNamePrefix());
        assertTrue(taskExecutor.getCorePoolSize() > 0);
    }

    @Test
    @DisplayName("calculationExecutor debe estar inicializado correctamente")
    void testCalculationExecutorInitialization() {
        Executor executor = asyncConfig.calculationExecutor();
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;

        // Verificar que el executor está inicializado y configurado
        assertNotNull(taskExecutor);
        assertNotNull(taskExecutor.getThreadNamePrefix());
        assertTrue(taskExecutor.getCorePoolSize() > 0);
    }

    @Test
    @DisplayName("Todos los executors deben tener nombres únicos de prefijo")
    void testAllExecutorsHaveUniqueThreadPrefixes() {
        ThreadPoolTaskExecutor fileExecutor = (ThreadPoolTaskExecutor) asyncConfig.fileProcessingExecutor();
        ThreadPoolTaskExecutor notificationExecutor = (ThreadPoolTaskExecutor) asyncConfig.notificationExecutor();
        ThreadPoolTaskExecutor calculationExecutor = (ThreadPoolTaskExecutor) asyncConfig.calculationExecutor();
        ThreadPoolTaskExecutor generalExecutor = (ThreadPoolTaskExecutor) asyncConfig.generalAsyncExecutor();

        String filePrefix = fileExecutor.getThreadNamePrefix();
        String notificationPrefix = notificationExecutor.getThreadNamePrefix();
        String calculationPrefix = calculationExecutor.getThreadNamePrefix();
        String generalPrefix = generalExecutor.getThreadNamePrefix();

        // Verificar que todos los prefijos son diferentes
        assertNotEquals(filePrefix, notificationPrefix);
        assertNotEquals(filePrefix, calculationPrefix);
        assertNotEquals(filePrefix, generalPrefix);
        assertNotEquals(notificationPrefix, calculationPrefix);
        assertNotEquals(notificationPrefix, generalPrefix);
        assertNotEquals(calculationPrefix, generalPrefix);
    }

    @Test
    @DisplayName("Clase debe estar anotada como Configuration")
    void testConfigurationAnnotation() {
        assertTrue(AsyncConfig.class.isAnnotationPresent(
                org.springframework.context.annotation.Configuration.class));
    }

    @Test
    @DisplayName("Clase debe estar anotada como EnableAsync")
    void testEnableAsyncAnnotation() {
        assertTrue(AsyncConfig.class.isAnnotationPresent(EnableAsync.class));
    }

    @Test
    @DisplayName("fileProcessingExecutor debe estar anotado como Bean con nombre")
    void testFileProcessingExecutorBeanAnnotation() throws NoSuchMethodException {
        org.springframework.context.annotation.Bean beanAnnotation = AsyncConfig.class
                .getMethod("fileProcessingExecutor")
                .getAnnotation(org.springframework.context.annotation.Bean.class);

        assertNotNull(beanAnnotation);
        assertEquals("fileProcessingExecutor", beanAnnotation.name()[0]);
    }

    @Test
    @DisplayName("notificationExecutor debe estar anotado como Bean con nombre")
    void testNotificationExecutorBeanAnnotation() throws NoSuchMethodException {
        org.springframework.context.annotation.Bean beanAnnotation = AsyncConfig.class
                .getMethod("notificationExecutor")
                .getAnnotation(org.springframework.context.annotation.Bean.class);

        assertNotNull(beanAnnotation);
        assertEquals("notificationExecutor", beanAnnotation.name()[0]);
    }

    @Test
    @DisplayName("calculationExecutor debe estar anotado como Bean con nombre")
    void testCalculationExecutorBeanAnnotation() throws NoSuchMethodException {
        org.springframework.context.annotation.Bean beanAnnotation = AsyncConfig.class
                .getMethod("calculationExecutor")
                .getAnnotation(org.springframework.context.annotation.Bean.class);

        assertNotNull(beanAnnotation);
        assertEquals("calculationExecutor", beanAnnotation.name()[0]);
    }

    @Test
    @DisplayName("generalAsyncExecutor debe estar anotado como Bean con nombre")
    void testGeneralAsyncExecutorBeanAnnotation() throws NoSuchMethodException {
        org.springframework.context.annotation.Bean beanAnnotation = AsyncConfig.class
                .getMethod("generalAsyncExecutor")
                .getAnnotation(org.springframework.context.annotation.Bean.class);

        assertNotNull(beanAnnotation);
        assertEquals("generalAsyncExecutor", beanAnnotation.name()[0]);
    }

    @Test
    @DisplayName("Todos los executors deben estar inicializados")
    void testAllExecutorsAreInitialized() {
        ThreadPoolTaskExecutor fileExecutor = (ThreadPoolTaskExecutor) asyncConfig.fileProcessingExecutor();
        ThreadPoolTaskExecutor notificationExecutor = (ThreadPoolTaskExecutor) asyncConfig.notificationExecutor();
        ThreadPoolTaskExecutor calculationExecutor = (ThreadPoolTaskExecutor) asyncConfig.calculationExecutor();
        ThreadPoolTaskExecutor generalExecutor = (ThreadPoolTaskExecutor) asyncConfig.generalAsyncExecutor();

        // Verificar que están inicializados (no nulos y con configuración válida)
        assertNotNull(fileExecutor.getThreadNamePrefix());
        assertNotNull(notificationExecutor.getThreadNamePrefix());
        assertNotNull(calculationExecutor.getThreadNamePrefix());
        assertNotNull(generalExecutor.getThreadNamePrefix());

        // Verificar que tienen configuraciones válidas
        assertTrue(fileExecutor.getCorePoolSize() > 0);
        assertTrue(notificationExecutor.getCorePoolSize() > 0);
        assertTrue(calculationExecutor.getCorePoolSize() > 0);
        assertTrue(generalExecutor.getCorePoolSize() > 0);
    }

    @Test
    @DisplayName("Configuraciones de pool deben ser apropiadas para cada tipo de tarea")
    void testPoolConfigurationsAreAppropriateForTaskType() {
        ThreadPoolTaskExecutor fileExecutor = (ThreadPoolTaskExecutor) asyncConfig.fileProcessingExecutor();
        ThreadPoolTaskExecutor notificationExecutor = (ThreadPoolTaskExecutor) asyncConfig.notificationExecutor();
        ThreadPoolTaskExecutor calculationExecutor = (ThreadPoolTaskExecutor) asyncConfig.calculationExecutor();
        ThreadPoolTaskExecutor generalExecutor = (ThreadPoolTaskExecutor) asyncConfig.generalAsyncExecutor();

        // File processing: pools medianos, queue media (I/O bound)
        assertEquals(2, fileExecutor.getCorePoolSize());
        assertEquals(5, fileExecutor.getMaxPoolSize());
        assertEquals(50, fileExecutor.getQueueCapacity());

        // Notifications: pools pequeños, queue pequeña (no crítico)
        assertEquals(1, notificationExecutor.getCorePoolSize());
        assertEquals(3, notificationExecutor.getMaxPoolSize());
        assertEquals(25, notificationExecutor.getQueueCapacity());

        // Calculations: pools medianos, queue grande (CPU bound)
        assertEquals(2, calculationExecutor.getCorePoolSize());
        assertEquals(4, calculationExecutor.getMaxPoolSize());
        assertEquals(100, calculationExecutor.getQueueCapacity());

        // General: pools grandes, queue muy grande (general purpose)
        assertEquals(3, generalExecutor.getCorePoolSize());
        assertEquals(8, generalExecutor.getMaxPoolSize());
        assertEquals(200, generalExecutor.getQueueCapacity());
    }

    @Test
    @DisplayName("generalAsyncExecutor debe tener configuración específica")
    void testGeneralExecutorSpecificConfiguration() {
        ThreadPoolTaskExecutor generalExecutor = (ThreadPoolTaskExecutor) asyncConfig.generalAsyncExecutor();

        // Solo el general executor debe tener configuración de keep alive específica
        assertEquals(60, generalExecutor.getKeepAliveSeconds());

        // Verificar que tiene configuración básica
        assertNotNull(generalExecutor.getThreadNamePrefix());
        assertTrue(generalExecutor.getCorePoolSize() > 0);
    }

    @Test
    @DisplayName("Todos los executors deben tener configuración de threads válida")
    void testAllExecutorsHaveValidThreadConfiguration() {
        ThreadPoolTaskExecutor fileExecutor = (ThreadPoolTaskExecutor) asyncConfig.fileProcessingExecutor();
        ThreadPoolTaskExecutor notificationExecutor = (ThreadPoolTaskExecutor) asyncConfig.notificationExecutor();
        ThreadPoolTaskExecutor calculationExecutor = (ThreadPoolTaskExecutor) asyncConfig.calculationExecutor();
        ThreadPoolTaskExecutor generalExecutor = (ThreadPoolTaskExecutor) asyncConfig.generalAsyncExecutor();

        // Verificar que todos tienen configuración válida
        assertTrue(fileExecutor.getCorePoolSize() > 0);
        assertTrue(notificationExecutor.getCorePoolSize() > 0);
        assertTrue(calculationExecutor.getCorePoolSize() > 0);
        assertTrue(generalExecutor.getCorePoolSize() > 0);

        // Verificar que max pool size >= core pool size
        assertTrue(fileExecutor.getMaxPoolSize() >= fileExecutor.getCorePoolSize());
        assertTrue(notificationExecutor.getMaxPoolSize() >= notificationExecutor.getCorePoolSize());
        assertTrue(calculationExecutor.getMaxPoolSize() >= calculationExecutor.getCorePoolSize());
        assertTrue(generalExecutor.getMaxPoolSize() >= generalExecutor.getCorePoolSize());
    }

    @Test
    @DisplayName("Executors deben retornar nuevas instancias en cada llamada")
    void testExecutorsReturnNewInstancesOnEachCall() {
        Executor fileExecutor1 = asyncConfig.fileProcessingExecutor();
        Executor fileExecutor2 = asyncConfig.fileProcessingExecutor();

        Executor notificationExecutor1 = asyncConfig.notificationExecutor();
        Executor notificationExecutor2 = asyncConfig.notificationExecutor();

        // Cada llamada al método bean debe retornar nueva instancia
        assertNotSame(fileExecutor1, fileExecutor2);
        assertNotSame(notificationExecutor1, notificationExecutor2);
    }
}