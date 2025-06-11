package com.udea.gpx.exception;

/**
 * Exception for connectivity issues
 */
public class ConnectivityException extends ExternalServiceException {
    public ConnectivityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectivityException(String message) {
        super(message);
    }
}
