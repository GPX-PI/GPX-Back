package com.udea.gpx.exception;

/**
 * Exception for OAuth2 service specific errors
 */
public class OAuth2ServiceException extends ExternalServiceException {
    public OAuth2ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuth2ServiceException(String message) {
        super(message);
    }
}
