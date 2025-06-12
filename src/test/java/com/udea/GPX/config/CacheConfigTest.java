package com.udea.gpx.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.annotation.EnableCaching;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CacheConfig Tests")
class CacheConfigTest {

    private CacheConfig cacheConfig;

    @BeforeEach
    void setUp() {
        cacheConfig = new CacheConfig();
    }

    @Test
    @DisplayName("CacheConfig debe estar anotado como Configuration")
    void testCacheConfigIsConfiguration() {
        assertTrue(CacheConfig.class.isAnnotationPresent(
                org.springframework.context.annotation.Configuration.class));
    }

    @Test
    @DisplayName("CacheConfig debe estar anotado como EnableCaching")
    void testCacheConfigIsEnableCaching() {
        assertTrue(CacheConfig.class.isAnnotationPresent(EnableCaching.class));
    }

    @Test
    @DisplayName("CacheConfig debe poder ser instanciado")
    void testCacheConfigCanBeInstantiated() {
        assertNotNull(cacheConfig);
    }
}