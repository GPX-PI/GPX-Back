package com.udea.gpx.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.udea.gpx.annotation.RequireAdmin;
import com.udea.gpx.util.AuthUtils;

/**
 * Aspecto para manejar la verificación de permisos de administrador
 * mediante la anotación @RequireAdmin
 */
@Aspect
@Component
public class AdminRequiredAspect {

    private static final Logger logger = LoggerFactory.getLogger(AdminRequiredAspect.class);

    private final AuthUtils authUtils;

    public AdminRequiredAspect(AuthUtils authUtils) {
        this.authUtils = authUtils;
    }

    @Around("@annotation(requireAdmin)")
    public Object checkAdminAccess(ProceedingJoinPoint joinPoint, RequireAdmin requireAdmin) throws Throwable {
        if (!authUtils.isCurrentUserAdmin()) {
            String methodName = "unknown";
            String className = "unknown";
            if (joinPoint.getSignature() != null) {
                methodName = joinPoint.getSignature().getName();
            }
            if (joinPoint.getTarget() != null) {
                className = joinPoint.getTarget().getClass().getSimpleName();
            }
            logger.warn("Unauthorized access attempt to admin method {}.{}", className, methodName);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(requireAdmin.message());
        }

        return joinPoint.proceed();
    }
}
