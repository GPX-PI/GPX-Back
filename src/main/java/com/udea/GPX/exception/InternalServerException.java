package com.udea.gpx.exception;

/**
 * Excepción específica para errores internos del servidor
 * Extiende RuntimeException para evitar problemas de verificación de
 * excepciones
 */
public class InternalServerException extends RuntimeException {

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
