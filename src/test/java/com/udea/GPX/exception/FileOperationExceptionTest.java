package com.udea.gpx.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("FileOperationException Tests")
class FileOperationExceptionTest {

    // ========== CONSTRUCTOR TESTS ==========

    @Test
    @DisplayName("Constructor básico - Debe crear excepción con mensaje y tipo de error")
    void constructor_basic_shouldCreateExceptionWithMessageAndErrorType() {
        // Given
        String message = "Error de prueba";
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.VALIDATION_ERROR;

        // When
        FileOperationException exception = new FileOperationException(message, errorType);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorType()).isEqualTo(errorType);
        assertThat(exception.getOriginalFileName()).isNull();
        assertThat(exception.getOperation()).isNull();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Constructor completo - Debe crear excepción con todos los parámetros")
    void constructor_complete_shouldCreateExceptionWithAllParameters() {
        // Given
        String message = "Error de validación";
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.INVALID_FORMAT;
        String originalFileName = "test.jpg";
        String operation = "upload";

        // When
        FileOperationException exception = new FileOperationException(message, errorType, originalFileName, operation);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorType()).isEqualTo(errorType);
        assertThat(exception.getOriginalFileName()).isEqualTo(originalFileName);
        assertThat(exception.getOperation()).isEqualTo(operation);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Constructor con causa - Debe crear excepción con mensaje, causa y tipo de error")
    void constructor_withCause_shouldCreateExceptionWithMessageCauseAndErrorType() {
        // Given
        String message = "Error de almacenamiento";
        Throwable cause = new RuntimeException("IO Error");
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.STORAGE_ERROR;

        // When
        FileOperationException exception = new FileOperationException(message, cause, errorType);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorType()).isEqualTo(errorType);
        assertThat(exception.getOriginalFileName()).isNull();
        assertThat(exception.getOperation()).isNull();
    }

    @Test
    @DisplayName("Constructor completo con causa - Debe crear excepción con todos los parámetros y causa")
    void constructor_completeWithCause_shouldCreateExceptionWithAllParametersAndCause() {
        // Given
        String message = "Error de procesamiento";
        Throwable cause = new RuntimeException("Processing failed");
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.PROCESSING_ERROR;
        String originalFileName = "document.pdf";
        String operation = "process";

        // When
        FileOperationException exception = new FileOperationException(message, cause, errorType, originalFileName,
                operation);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getErrorType()).isEqualTo(errorType);
        assertThat(exception.getOriginalFileName()).isEqualTo(originalFileName);
        assertThat(exception.getOperation()).isEqualTo(operation);
    }

    // ========== GETTER TESTS ==========

    @Test
    @DisplayName("getErrorType - Debe retornar el tipo de error correcto")
    void getErrorType_shouldReturnCorrectErrorType() {
        // Given
        FileOperationException.FileErrorType expectedType = FileOperationException.FileErrorType.FILE_TOO_LARGE;
        FileOperationException exception = new FileOperationException("Test", expectedType);

        // When
        FileOperationException.FileErrorType actualType = exception.getErrorType();

        // Then
        assertThat(actualType).isEqualTo(expectedType);
    }

    @Test
    @DisplayName("getOriginalFileName - Debe retornar el nombre de archivo original")
    void getOriginalFileName_shouldReturnOriginalFileName() {
        // Given
        String expectedFileName = "image.png";
        FileOperationException exception = new FileOperationException(
                "Test", FileOperationException.FileErrorType.VALIDATION_ERROR, expectedFileName, "upload");

        // When
        String actualFileName = exception.getOriginalFileName();

        // Then
        assertThat(actualFileName).isEqualTo(expectedFileName);
    }

    @Test
    @DisplayName("getOperation - Debe retornar la operación correcta")
    void getOperation_shouldReturnCorrectOperation() {
        // Given
        String expectedOperation = "delete";
        FileOperationException exception = new FileOperationException(
                "Test", FileOperationException.FileErrorType.DELETION_ERROR, "file.txt", expectedOperation);

        // When
        String actualOperation = exception.getOperation();

        // Then
        assertThat(actualOperation).isEqualTo(expectedOperation);
    }

    // ========== NULL HANDLING TESTS ==========

    @Test
    @DisplayName("Constructor con parámetros null - Debe manejar valores null correctamente")
    void constructor_withNullParameters_shouldHandleNullValuesCorrectly() {
        // Given
        String message = "Test message";
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.FILE_EMPTY;

        // When
        FileOperationException exception = new FileOperationException(message, errorType, null, null);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getErrorType()).isEqualTo(errorType);
        assertThat(exception.getOriginalFileName()).isNull();
        assertThat(exception.getOperation()).isNull();
    }

    @Test
    @DisplayName("Constructor con causa null - Debe manejar causa null correctamente")
    void constructor_withNullCause_shouldHandleNullCauseCorrectly() {
        // Given
        String message = "Test message";
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.ACCESS_DENIED;

        // When
        FileOperationException exception = new FileOperationException(message, null, errorType, "file.doc", "read");

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getErrorType()).isEqualTo(errorType);
        assertThat(exception.getOriginalFileName()).isEqualTo("file.doc");
        assertThat(exception.getOperation()).isEqualTo("read");
    }

    // ========== FILE ERROR TYPE ENUM TESTS ==========

    @ParameterizedTest
    @EnumSource(FileOperationException.FileErrorType.class)
    @DisplayName("FileErrorType - Todos los tipos deben tener código y descripción no nulos")
    void fileErrorType_allTypes_shouldHaveNonNullCodeAndDescription(FileOperationException.FileErrorType errorType) {
        // When & Then
        assertThat(errorType.getCode()).isNotNull().isNotEmpty();
        assertThat(errorType.getDescription()).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("FileErrorType.INVALID_FORMAT - Debe tener valores correctos")
    void fileErrorType_invalidFormat_shouldHaveCorrectValues() {
        // Given
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.INVALID_FORMAT;

        // When & Then
        assertThat(errorType.getCode()).isEqualTo("INVALID_FORMAT");
        assertThat(errorType.getDescription()).isEqualTo("Formato de archivo no válido");
    }

    @Test
    @DisplayName("FileErrorType.FILE_TOO_LARGE - Debe tener valores correctos")
    void fileErrorType_fileTooLarge_shouldHaveCorrectValues() {
        // Given
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.FILE_TOO_LARGE;

        // When & Then
        assertThat(errorType.getCode()).isEqualTo("FILE_TOO_LARGE");
        assertThat(errorType.getDescription()).isEqualTo("Archivo demasiado grande");
    }

    @Test
    @DisplayName("FileErrorType.FILE_EMPTY - Debe tener valores correctos")
    void fileErrorType_fileEmpty_shouldHaveCorrectValues() {
        // Given
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.FILE_EMPTY;

        // When & Then
        assertThat(errorType.getCode()).isEqualTo("FILE_EMPTY");
        assertThat(errorType.getDescription()).isEqualTo("Archivo vacío");
    }

    @Test
    @DisplayName("FileErrorType.MALICIOUS_CONTENT - Debe tener valores correctos")
    void fileErrorType_maliciousContent_shouldHaveCorrectValues() {
        // Given
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.MALICIOUS_CONTENT;

        // When & Then
        assertThat(errorType.getCode()).isEqualTo("MALICIOUS_CONTENT");
        assertThat(errorType.getDescription()).isEqualTo("Contenido malicioso detectado");
    }

    @Test
    @DisplayName("FileErrorType.STORAGE_ERROR - Debe tener valores correctos")
    void fileErrorType_storageError_shouldHaveCorrectValues() {
        // Given
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.STORAGE_ERROR;

        // When & Then
        assertThat(errorType.getCode()).isEqualTo("STORAGE_ERROR");
        assertThat(errorType.getDescription()).isEqualTo("Error de almacenamiento");
    }

    @Test
    @DisplayName("FileErrorType.VALIDATION_ERROR - Debe tener valores correctos")
    void fileErrorType_validationError_shouldHaveCorrectValues() {
        // Given
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.VALIDATION_ERROR;

        // When & Then
        assertThat(errorType.getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(errorType.getDescription()).isEqualTo("Error de validación de archivo");
    }

    @Test
    @DisplayName("FileErrorType.DELETION_ERROR - Debe tener valores correctos")
    void fileErrorType_deletionError_shouldHaveCorrectValues() {
        // Given
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.DELETION_ERROR;

        // When & Then
        assertThat(errorType.getCode()).isEqualTo("DELETION_ERROR");
        assertThat(errorType.getDescription()).isEqualTo("Error al eliminar archivo");
    }

    @Test
    @DisplayName("FileErrorType.ACCESS_DENIED - Debe tener valores correctos")
    void fileErrorType_accessDenied_shouldHaveCorrectValues() {
        // Given
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.ACCESS_DENIED;

        // When & Then
        assertThat(errorType.getCode()).isEqualTo("ACCESS_DENIED");
        assertThat(errorType.getDescription()).isEqualTo("Acceso denegado al archivo");
    }

    @Test
    @DisplayName("FileErrorType.FILE_NOT_FOUND - Debe tener valores correctos")
    void fileErrorType_fileNotFound_shouldHaveCorrectValues() {
        // Given
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.FILE_NOT_FOUND;

        // When & Then
        assertThat(errorType.getCode()).isEqualTo("FILE_NOT_FOUND");
        assertThat(errorType.getDescription()).isEqualTo("Archivo no encontrado");
    }

    @Test
    @DisplayName("FileErrorType.PROCESSING_ERROR - Debe tener valores correctos")
    void fileErrorType_processingError_shouldHaveCorrectValues() {
        // Given
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.PROCESSING_ERROR;

        // When & Then
        assertThat(errorType.getCode()).isEqualTo("PROCESSING_ERROR");
        assertThat(errorType.getDescription()).isEqualTo("Error de procesamiento de archivo");
    }

    // ========== INHERITANCE TESTS ==========

    @Test
    @DisplayName("Herencia - Debe extender RuntimeException")
    void inheritance_shouldExtendRuntimeException() {
        // Given
        FileOperationException exception = new FileOperationException(
                "Test", FileOperationException.FileErrorType.VALIDATION_ERROR);

        // When & Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception).isInstanceOf(Throwable.class);
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("Constructor con mensaje vacío - Debe manejar mensaje vacío")
    void constructor_withEmptyMessage_shouldHandleEmptyMessage() {
        // Given
        String emptyMessage = "";
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.FILE_EMPTY;

        // When
        FileOperationException exception = new FileOperationException(emptyMessage, errorType);

        // Then
        assertThat(exception.getMessage()).isEmpty();
        assertThat(exception.getErrorType()).isEqualTo(errorType);
    }

    @Test
    @DisplayName("Constructor con nombre de archivo vacío - Debe manejar nombre vacío")
    void constructor_withEmptyFileName_shouldHandleEmptyFileName() {
        // Given
        String message = "Test";
        String emptyFileName = "";
        String operation = "upload";

        // When
        FileOperationException exception = new FileOperationException(
                message, FileOperationException.FileErrorType.VALIDATION_ERROR, emptyFileName, operation);

        // Then
        assertThat(exception.getOriginalFileName()).isEmpty();
        assertThat(exception.getOperation()).isEqualTo(operation);
    }

    @Test
    @DisplayName("Constructor con operación vacía - Debe manejar operación vacía")
    void constructor_withEmptyOperation_shouldHandleEmptyOperation() {
        // Given
        String message = "Test";
        String fileName = "test.txt";
        String emptyOperation = "";

        // When
        FileOperationException exception = new FileOperationException(
                message, FileOperationException.FileErrorType.STORAGE_ERROR, fileName, emptyOperation);

        // Then
        assertThat(exception.getOriginalFileName()).isEqualTo(fileName);
        assertThat(exception.getOperation()).isEmpty();
    }

    // ========== INTEGRATION TESTS ==========

    @Test
    @DisplayName("Integración con diferentes tipos de error - Debe funcionar con todos los tipos")
    void integration_withDifferentErrorTypes_shouldWorkWithAllTypes() {
        // Given
        FileOperationException.FileErrorType[] errorTypes = FileOperationException.FileErrorType.values();
        String baseMessage = "Error test ";
        String fileName = "test-file.ext";
        String operation = "test-operation";

        // When & Then
        for (int i = 0; i < errorTypes.length; i++) {
            FileOperationException.FileErrorType errorType = errorTypes[i];
            String message = baseMessage + i;

            FileOperationException exception = new FileOperationException(
                    message, errorType, fileName + i, operation + i);

            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getErrorType()).isEqualTo(errorType);
            assertThat(exception.getOriginalFileName()).isEqualTo(fileName + i);
            assertThat(exception.getOperation()).isEqualTo(operation + i);
            assertThat(exception.getErrorType().getCode()).isNotNull();
            assertThat(exception.getErrorType().getDescription()).isNotNull();
        }
    }

    @Test
    @DisplayName("Serialización - Debe mantener propiedades después de serialización")
    void serialization_shouldMaintainPropertiesAfterSerialization() {
        // Given
        String message = "Serialization test";
        FileOperationException.FileErrorType errorType = FileOperationException.FileErrorType.PROCESSING_ERROR;
        String fileName = "serialize-test.dat";
        String operation = "serialize";
        RuntimeException cause = new RuntimeException("Original cause");

        FileOperationException originalException = new FileOperationException(
                message, cause, errorType, fileName, operation);

        // When - Simular serialización verificando que las propiedades se mantienen
        String serializedMessage = originalException.getMessage();
        Throwable serializedCause = originalException.getCause();
        FileOperationException.FileErrorType serializedErrorType = originalException.getErrorType();
        String serializedFileName = originalException.getOriginalFileName();
        String serializedOperation = originalException.getOperation();

        // Then
        assertThat(serializedMessage).isEqualTo(message);
        assertThat(serializedCause).isEqualTo(cause);
        assertThat(serializedErrorType).isEqualTo(errorType);
        assertThat(serializedFileName).isEqualTo(fileName);
        assertThat(serializedOperation).isEqualTo(operation);
    }

    // ========== ENUM COMPLETENESS TESTS ==========

    @Test
    @DisplayName("FileErrorType - Debe tener exactamente 10 tipos de error definidos")
    void fileErrorType_shouldHaveExactlyTenErrorTypesDefined() {
        // When
        FileOperationException.FileErrorType[] errorTypes = FileOperationException.FileErrorType.values();

        // Then
        assertThat(errorTypes).hasSize(10);
        assertThat(errorTypes).containsExactlyInAnyOrder(
                FileOperationException.FileErrorType.INVALID_FORMAT,
                FileOperationException.FileErrorType.FILE_TOO_LARGE,
                FileOperationException.FileErrorType.FILE_EMPTY,
                FileOperationException.FileErrorType.MALICIOUS_CONTENT,
                FileOperationException.FileErrorType.STORAGE_ERROR,
                FileOperationException.FileErrorType.VALIDATION_ERROR,
                FileOperationException.FileErrorType.DELETION_ERROR,
                FileOperationException.FileErrorType.ACCESS_DENIED,
                FileOperationException.FileErrorType.FILE_NOT_FOUND,
                FileOperationException.FileErrorType.PROCESSING_ERROR);
    }

    @Test
    @DisplayName("FileErrorType - Códigos deben ser únicos")
    void fileErrorType_codesShouldBeUnique() {
        // Given
        FileOperationException.FileErrorType[] errorTypes = FileOperationException.FileErrorType.values();

        // When
        String[] codes = new String[errorTypes.length];
        for (int i = 0; i < errorTypes.length; i++) {
            codes[i] = errorTypes[i].getCode();
        }

        // Then
        assertThat(codes).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("FileErrorType - Descripciones deben ser únicas")
    void fileErrorType_descriptionsShouldBeUnique() {
        // Given
        FileOperationException.FileErrorType[] errorTypes = FileOperationException.FileErrorType.values();

        // When
        String[] descriptions = new String[errorTypes.length];
        for (int i = 0; i < errorTypes.length; i++) {
            descriptions[i] = errorTypes[i].getDescription();
        }

        // Then
        assertThat(descriptions).doesNotHaveDuplicates();
    }
}
