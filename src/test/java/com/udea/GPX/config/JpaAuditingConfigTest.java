package com.udea.gpx.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JpaAuditingConfig Tests")
class JpaAuditingConfigTest {

    private JpaAuditingConfig jpaAuditingConfig;

    @BeforeEach
    void setUp() {
        jpaAuditingConfig = new JpaAuditingConfig();
    }

    @Test
    @DisplayName("JpaAuditingConfig debe estar anotado como Configuration")
    void testJpaAuditingConfigIsConfiguration() {
        assertTrue(JpaAuditingConfig.class.isAnnotationPresent(
                org.springframework.context.annotation.Configuration.class));
    }

    @Test
    @DisplayName("JpaAuditingConfig debe estar anotado como EnableJpaAuditing")
    void testJpaAuditingConfigIsEnableJpaAuditing() {
        assertTrue(JpaAuditingConfig.class.isAnnotationPresent(EnableJpaAuditing.class));
    }

    @Test
    @DisplayName("JpaAuditingConfig debe poder ser instanciado")
    void testJpaAuditingConfigCanBeInstantiated() {
        assertNotNull(jpaAuditingConfig);
    }
}