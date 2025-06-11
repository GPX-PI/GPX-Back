package com.udea.gpx.exception;

/**
 * Base exception for external service operations
 */
public class ExternalServiceException extends Exception {
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalServiceException(String message) {
        super(message);
    }
}
