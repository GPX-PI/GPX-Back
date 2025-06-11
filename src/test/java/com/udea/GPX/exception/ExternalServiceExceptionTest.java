package com.udea.gpx.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("External Service Exception Tests")
class ExternalServiceExceptionTest {

    @Test
    @DisplayName("ExternalServiceException - Should create with message and cause")
    void externalServiceException_ShouldCreateWithMessageAndCause() {
        // Given
        String message = "Service error";
        RuntimeException cause = new RuntimeException("Root cause");

        // When
        ExternalServiceException exception = new ExternalServiceException(message, cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("ExternalServiceException - Should create with message only")
    void externalServiceException_ShouldCreateWithMessageOnly() {
        // Given
        String message = "Service error";

        // When
        ExternalServiceException exception = new ExternalServiceException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("OAuth2ServiceException - Should extend ExternalServiceException")
    void oauth2ServiceException_ShouldExtendExternalServiceException() {
        // Given
        String message = "OAuth2 error";
        RuntimeException cause = new RuntimeException("OAuth2 cause");

        // When
        OAuth2ServiceException exception = new OAuth2ServiceException(message, cause);

        // Then
        assertInstanceOf(ExternalServiceException.class, exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("OAuth2ServiceException - Should create with message only")
    void oauth2ServiceException_ShouldCreateWithMessageOnly() {
        // Given
        String message = "OAuth2 error";

        // When
        OAuth2ServiceException exception = new OAuth2ServiceException(message);

        // Then
        assertInstanceOf(ExternalServiceException.class, exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("ConnectivityException - Should extend ExternalServiceException")
    void connectivityException_ShouldExtendExternalServiceException() {
        // Given
        String message = "Connectivity error";
        RuntimeException cause = new RuntimeException("Network cause");

        // When
        ConnectivityException exception = new ConnectivityException(message, cause);

        // Then
        assertInstanceOf(ExternalServiceException.class, exception);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("ConnectivityException - Should create with message only")
    void connectivityException_ShouldCreateWithMessageOnly() {
        // Given
        String message = "Connectivity error";

        // When
        ConnectivityException exception = new ConnectivityException(message);

        // Then
        assertInstanceOf(ExternalServiceException.class, exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Exception hierarchy - Should maintain proper inheritance")
    void exceptionHierarchy_ShouldMaintainProperInheritance() {
        // Given & When
        OAuth2ServiceException oauth2Exception = new OAuth2ServiceException("OAuth2", new RuntimeException());
        ConnectivityException connectivityException = new ConnectivityException("Connectivity", new RuntimeException());

        // Then - Check inheritance chain
        assertInstanceOf(Exception.class, oauth2Exception);
        assertInstanceOf(ExternalServiceException.class, oauth2Exception);
        assertInstanceOf(OAuth2ServiceException.class, oauth2Exception);

        assertInstanceOf(Exception.class, connectivityException);
        assertInstanceOf(ExternalServiceException.class, connectivityException);
        assertInstanceOf(ConnectivityException.class, connectivityException);

        // Verify they are not instances of each other using class comparison
        assertNotEquals(oauth2Exception.getClass(), connectivityException.getClass());
        assertNotEquals(ConnectivityException.class, oauth2Exception.getClass());
        assertNotEquals(OAuth2ServiceException.class, connectivityException.getClass());
    }
}
