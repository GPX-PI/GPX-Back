package com.udea.gpx.aspect;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AdminRequiredAspect Tests")
class AdminRequiredAspectTest {

    @Test
    @DisplayName("AdminRequiredAspect debe estar anotado como Component")
    void testAdminRequiredAspectIsComponent() {
        assertTrue(AdminRequiredAspect.class.isAnnotationPresent(Component.class));
    }

    @Test
    @DisplayName("AdminRequiredAspect debe estar anotado como Aspect")
    void testAdminRequiredAspectIsAspect() {
        assertTrue(AdminRequiredAspect.class.isAnnotationPresent(
                org.aspectj.lang.annotation.Aspect.class));
    }
}