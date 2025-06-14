package com.udea.gpx.aspect;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import org.aspectj.lang.ProceedingJoinPoint;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import com.udea.gpx.annotation.RequireAdmin;
import com.udea.gpx.util.AuthUtils;
import org.springframework.http.HttpStatus;

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

    @Test
    @DisplayName("Debe retornar FORBIDDEN si el usuario no es admin")
    void checkAdminAccess_shouldReturnForbiddenIfNotAdmin() throws Throwable {
        AuthUtils authUtils = Mockito.mock(AuthUtils.class);
        Mockito.when(authUtils.isCurrentUserAdmin()).thenReturn(false);
        ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        RequireAdmin requireAdmin = Mockito.mock(RequireAdmin.class);
        Mockito.when(requireAdmin.message()).thenReturn("No tienes permisos de admin");
        AdminRequiredAspect aspect = new AdminRequiredAspect(authUtils);

        Object result = aspect.checkAdminAccess(joinPoint, requireAdmin);
        assertTrue(result instanceof ResponseEntity);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("No tienes permisos de admin", response.getBody());
    }

    @Test
    @DisplayName("Debe ejecutar el método si el usuario es admin")
    void checkAdminAccess_shouldProceedIfAdmin() throws Throwable {
        AuthUtils authUtils = Mockito.mock(AuthUtils.class);
        Mockito.when(authUtils.isCurrentUserAdmin()).thenReturn(true);
        ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        RequireAdmin requireAdmin = Mockito.mock(RequireAdmin.class);
        Object expected = new Object();
        Mockito.when(joinPoint.proceed()).thenReturn(expected);
        AdminRequiredAspect aspect = new AdminRequiredAspect(authUtils);

        Object result = aspect.checkAdminAccess(joinPoint, requireAdmin);
        assertSame(expected, result);
    }
}