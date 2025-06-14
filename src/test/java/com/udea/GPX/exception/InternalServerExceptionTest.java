package com.udea.gpx.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("InternalServerException Tests")
class InternalServerExceptionTest {
    @Test
    @DisplayName("Should instantiate with message")
    void shouldInstantiateWithMessage() {
        InternalServerException ex = new InternalServerException("error");
        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo("error");
    }

    @Test
    @DisplayName("Should instantiate with message and cause")
    void shouldInstantiateWithMessageAndCause() {
        Throwable cause = new RuntimeException("cause");
        InternalServerException ex = new InternalServerException("error", cause);
        assertThat(ex.getMessage()).isEqualTo("error");
        assertThat(ex.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Should be a RuntimeException")
    void shouldBeRuntimeException() {
        InternalServerException ex = new InternalServerException("error");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should preserve stack trace")
    void shouldPreserveStackTrace() {
        RuntimeException originalException = new RuntimeException("Original error");
        originalException.fillInStackTrace();

        InternalServerException ex = new InternalServerException("Wrapped error", originalException);

        assertThat(ex.getCause()).isSameAs(originalException);
        assertThat(ex.getMessage()).isEqualTo("Wrapped error");
        assertThat(ex.getCause().getMessage()).isEqualTo("Original error");
        assertThat(ex.getCause().getStackTrace()).isEqualTo(originalException.getStackTrace());
    }
}
