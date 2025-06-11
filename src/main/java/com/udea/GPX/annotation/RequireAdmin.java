package com.udea.gpx.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para marcar métodos que requieren permisos de administrador.
 * Cuando se aplica esta anotación, el método verificará automáticamente
 * si el usuario actual tiene permisos de administrador antes de ejecutarse.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAdmin {
    /**
     * Mensaje personalizado para cuando el acceso sea denegado.
     * 
     * @return el mensaje de error personalizado
     */
    String message() default "Access denied: Admin privileges required";
}
