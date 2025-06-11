package com.udea.gpx.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.udea.gpx.service.AsyncFileService.BatchProcessingResult;
import com.udea.gpx.service.AsyncFileService.ValidationSummary;
import com.udea.gpx.util.FileValidator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AsyncFileService Tests")
class AsyncFileServiceTest {

    @Mock
    private FileValidator fileValidator;

    @InjectMocks
    private AsyncFileService asyncFileService;

    @TempDir
    Path tempDir;

    private MultipartFile mockFile;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Setup mock file
        mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test-image.jpg");
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("fake image content".getBytes()));
    }

    // ========== PROCESS IMAGE ASYNC TESTS ==========

    @Test
    @DisplayName("processImageAsync - Debe procesar imagen válida exitosamente")
    void processImageAsync_shouldProcessValidImageSuccessfully() throws Exception {
        // Given
        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(mockFile)).thenReturn(validResult);

        String targetDirectory = tempDir.toString();

        // When
        CompletableFuture<String> future = asyncFileService.processImageAsync(mockFile, targetDirectory);
        String result = future.get(); // Then
        assertThat(result).isNotNull();
        assertThat(result).contains(tempDir.toString());
        assertThat(result).contains("test_image.jpg"); // The service sanitizes hyphens to underscores

        // Verificar que el archivo se creó
        Path savedFile = Path.of(result);
        assertThat(Files.exists(savedFile)).isTrue();

        verify(fileValidator).validateImageFile(mockFile);
    }

    @Test
    @DisplayName("processImageAsync - Debe fallar con imagen inválida")
    void processImageAsync_shouldFailWithInvalidImage() throws Exception {
        // Given
        FileValidator.ValidationResult invalidResult = FileValidator.ValidationResult.error("Invalid image");
        when(fileValidator.validateImageFile(mockFile)).thenReturn(invalidResult);

        String targetDirectory = tempDir.toString();

        // When
        CompletableFuture<String> future = asyncFileService.processImageAsync(mockFile, targetDirectory);

        // Then
        assertThatThrownBy(() -> future.get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid image");

        verify(fileValidator).validateImageFile(mockFile);
    }

    @Test
    @DisplayName("processImageAsync - Debe crear directorio si no existe")
    void processImageAsync_shouldCreateDirectoryIfNotExists() throws Exception {
        // Given
        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(mockFile)).thenReturn(validResult);

        Path newDir = tempDir.resolve("new-directory");
        String targetDirectory = newDir.toString();

        // When
        CompletableFuture<String> future = asyncFileService.processImageAsync(mockFile, targetDirectory);
        String result = future.get();

        // Then
        assertThat(Files.exists(newDir)).isTrue();
        assertThat(Files.isDirectory(newDir)).isTrue();
        assertThat(result).contains(newDir.toString());

        verify(fileValidator).validateImageFile(mockFile);
    }

    @Test
    @DisplayName("processImageAsync - Debe generar nombre único de archivo")
    void processImageAsync_shouldGenerateUniqueFilename() throws Exception {
        // Given
        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(mockFile)).thenReturn(validResult);

        String targetDirectory = tempDir.toString();

        // When
        CompletableFuture<String> future1 = asyncFileService.processImageAsync(mockFile, targetDirectory);
        CompletableFuture<String> future2 = asyncFileService.processImageAsync(mockFile, targetDirectory);

        String result1 = future1.get();
        String result2 = future2.get();

        // Then
        assertThat(result1).isNotEqualTo(result2);
        assertThat(Files.exists(Path.of(result1))).isTrue();
        assertThat(Files.exists(Path.of(result2))).isTrue();

        verify(fileValidator, times(2)).validateImageFile(mockFile);
    }

    @Test
    @DisplayName("processImageAsync - Debe sanitizar nombre de archivo")
    void processImageAsync_shouldSanitizeFilename() throws Exception {
        // Given
        when(mockFile.getOriginalFilename()).thenReturn("test@#$%image!.jpg");
        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(mockFile)).thenReturn(validResult);

        String targetDirectory = tempDir.toString();

        // When
        CompletableFuture<String> future = asyncFileService.processImageAsync(mockFile, targetDirectory);
        String result = future.get(); // Then
        assertThat(result).contains("test____image_.jpg"); // The service sanitizes special characters
        assertThat(Files.exists(Path.of(result))).isTrue();

        verify(fileValidator).validateImageFile(mockFile);
    }

    // ========== DELETE FILE ASYNC TESTS ==========

    @Test
    @DisplayName("deleteFileAsync - Debe eliminar archivo existente exitosamente")
    void deleteFileAsync_shouldDeleteExistingFileSuccessfully() throws Exception {
        // Given
        Path testFile = tempDir.resolve("test-file.txt");
        Files.write(testFile, "test content".getBytes());
        assertThat(Files.exists(testFile)).isTrue();

        // When
        CompletableFuture<Boolean> future = asyncFileService.deleteFileAsync(testFile.toString());
        Boolean result = future.get();

        // Then
        assertThat(result).isTrue();
        assertThat(Files.exists(testFile)).isFalse();
    }

    @Test
    @DisplayName("deleteFileAsync - Debe manejar archivo inexistente graciosamente")
    void deleteFileAsync_shouldHandleNonExistentFileGracefully() throws Exception {
        // Given
        String nonExistentFilePath = tempDir.resolve("non-existent.txt").toString();

        // When
        CompletableFuture<Boolean> future = asyncFileService.deleteFileAsync(nonExistentFilePath);
        Boolean result = future.get();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("deleteFileAsync - Debe manejar path null graciosamente")
    void deleteFileAsync_shouldHandleNullPathGracefully() throws Exception {
        // When
        CompletableFuture<Boolean> future = asyncFileService.deleteFileAsync(null);
        Boolean result = future.get();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("deleteFileAsync - Debe manejar path vacío graciosamente")
    void deleteFileAsync_shouldHandleEmptyPathGracefully() throws Exception {
        // When
        CompletableFuture<Boolean> future = asyncFileService.deleteFileAsync("   ");
        Boolean result = future.get();

        // Then
        assertThat(result).isTrue();
    }

    // ========== CLEANUP ORPHANED FILES ASYNC TESTS ==========

    @Test
    @DisplayName("cleanupOrphanedFilesAsync - Debe manejar directorio existente")
    void cleanupOrphanedFilesAsync_shouldHandleExistingDirectory() throws Exception {
        // Given
        String directoryPath = tempDir.toString();

        // When
        CompletableFuture<Integer> future = asyncFileService.cleanupOrphanedFilesAsync(directoryPath);
        Integer result = future.get();

        // Then
        assertThat(result).isEqualTo(0); // Por ahora solo retorna 0
    }

    @Test
    @DisplayName("cleanupOrphanedFilesAsync - Debe manejar directorio inexistente")
    void cleanupOrphanedFilesAsync_shouldHandleNonExistentDirectory() throws Exception {
        // Given
        String nonExistentDir = tempDir.resolve("non-existent").toString();

        // When
        CompletableFuture<Integer> future = asyncFileService.cleanupOrphanedFilesAsync(nonExistentDir);
        Integer result = future.get();

        // Then
        assertThat(result).isEqualTo(0);
    }

    // ========== VALIDATE MULTIPLE FILES ASYNC TESTS ==========

    @Test
    @DisplayName("validateMultipleFilesAsync - Debe validar múltiples archivos correctamente")
    void validateMultipleFilesAsync_shouldValidateMultipleFilesCorrectly() throws Exception {
        // Given
        MultipartFile validFile = mock(MultipartFile.class);
        when(validFile.getOriginalFilename()).thenReturn("valid.jpg");

        MultipartFile invalidFile = mock(MultipartFile.class);
        when(invalidFile.getOriginalFilename()).thenReturn("invalid.exe");

        MultipartFile[] files = { validFile, invalidFile };

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        FileValidator.ValidationResult invalidResult = FileValidator.ValidationResult.error("Invalid file type");

        when(fileValidator.validateImageFile(validFile)).thenReturn(validResult);
        when(fileValidator.validateImageFile(invalidFile)).thenReturn(invalidResult);

        // When
        CompletableFuture<ValidationSummary> future = asyncFileService.validateMultipleFilesAsync(files);
        ValidationSummary result = future.get();

        // Then
        assertThat(result.getValidFiles()).hasSize(1);
        assertThat(result.getValidFiles()).contains("valid.jpg");

        assertThat(result.getInvalidFiles()).hasSize(1);
        assertThat(result.getInvalidFiles()).containsKey("invalid.exe");
        assertThat(result.getInvalidFiles().get("invalid.exe")).isEqualTo("Invalid file type");

        verify(fileValidator).validateImageFile(validFile);
        verify(fileValidator).validateImageFile(invalidFile);
    }

    @Test
    @DisplayName("validateMultipleFilesAsync - Debe manejar archivos vacíos")
    void validateMultipleFilesAsync_shouldHandleEmptyFiles() throws Exception {
        // Given
        MultipartFile[] emptyFiles = {};

        // When
        CompletableFuture<ValidationSummary> future = asyncFileService.validateMultipleFilesAsync(emptyFiles);
        ValidationSummary result = future.get();

        // Then
        assertThat(result.getValidFiles()).isEmpty();
        assertThat(result.getInvalidFiles()).isEmpty();
    }

    @Test
    @DisplayName("validateMultipleFilesAsync - Debe manejar excepciones durante validación")
    void validateMultipleFilesAsync_shouldHandleValidationExceptions() throws Exception {
        // Given
        MultipartFile problematicFile = mock(MultipartFile.class);
        when(problematicFile.getOriginalFilename()).thenReturn("problem.jpg");
        when(fileValidator.validateImageFile(problematicFile)).thenThrow(new RuntimeException("Validation error"));

        MultipartFile[] files = { problematicFile };

        // When
        CompletableFuture<ValidationSummary> future = asyncFileService.validateMultipleFilesAsync(files);
        ValidationSummary result = future.get();

        // Then
        assertThat(result.getValidFiles()).isEmpty();
        assertThat(result.getInvalidFiles()).hasSize(1);
        assertThat(result.getInvalidFiles()).containsKey("problem.jpg");
        assertThat(result.getInvalidFiles().get("problem.jpg")).isEqualTo("Validation error");

        verify(fileValidator).validateImageFile(problematicFile);
    }

    // ========== PROCESS BATCH ASYNC TESTS ==========

    @Test
    @DisplayName("processBatchAsync - Debe procesar batch de archivos correctamente")
    void processBatchAsync_shouldProcessBatchCorrectly() throws Exception {
        // Given
        MultipartFile validFile = mock(MultipartFile.class);
        when(validFile.getOriginalFilename()).thenReturn("valid.jpg");
        when(validFile.isEmpty()).thenReturn(false);
        when(validFile.getSize()).thenReturn(1024L);
        when(validFile.getInputStream()).thenReturn(new ByteArrayInputStream("content".getBytes()));

        MultipartFile[] files = { validFile };

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(validFile)).thenReturn(validResult);

        String targetDirectory = tempDir.toString();

        // When
        CompletableFuture<BatchProcessingResult> future = asyncFileService.processBatchAsync(files, targetDirectory);
        BatchProcessingResult result = future.get();

        // Then
        assertThat(result.getSuccessfulFiles()).hasSize(1);
        assertThat(result.getSuccessfulFiles()).containsKey("valid.jpg");
        assertThat(result.getFailedFiles()).isEmpty();

        verify(fileValidator).validateImageFile(validFile);
    }

    @Test
    @DisplayName("processBatchAsync - Debe manejar fallos en el batch")
    void processBatchAsync_shouldHandleBatchFailures() throws Exception {
        // Given
        MultipartFile invalidFile = mock(MultipartFile.class);
        when(invalidFile.getOriginalFilename()).thenReturn("invalid.exe");

        MultipartFile[] files = { invalidFile };

        FileValidator.ValidationResult invalidResult = FileValidator.ValidationResult.error("Invalid file");
        when(fileValidator.validateImageFile(invalidFile)).thenReturn(invalidResult);

        String targetDirectory = tempDir.toString();

        // When
        CompletableFuture<BatchProcessingResult> future = asyncFileService.processBatchAsync(files, targetDirectory);
        BatchProcessingResult result = future.get();

        // Then
        assertThat(result.getSuccessfulFiles()).isEmpty();
        assertThat(result.getFailedFiles()).hasSize(1);
        assertThat(result.getFailedFiles()).containsKey("invalid.exe");

        verify(fileValidator).validateImageFile(invalidFile);
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("processImageAsync - Debe manejar IOException")
    void processImageAsync_shouldHandleIOException() throws Exception {
        // Given
        MultipartFile problematicFile = mock(MultipartFile.class);
        when(problematicFile.getOriginalFilename()).thenReturn("problem.jpg");
        when(problematicFile.getInputStream()).thenThrow(new IOException("IO Error"));

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(problematicFile)).thenReturn(validResult);

        String targetDirectory = tempDir.toString();

        // When
        CompletableFuture<String> future = asyncFileService.processImageAsync(problematicFile, targetDirectory);

        // Then
        assertThatThrownBy(() -> future.get())
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IOException.class);

        verify(fileValidator).validateImageFile(problematicFile);
    }

    @Test
    @DisplayName("processImageAsync - Debe manejar nombre de archivo null")
    void processImageAsync_shouldHandleNullFilename() throws Exception {
        // Given
        when(mockFile.getOriginalFilename()).thenReturn(null);
        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(mockFile)).thenReturn(validResult);

        String targetDirectory = tempDir.toString();

        // When
        CompletableFuture<String> future = asyncFileService.processImageAsync(mockFile, targetDirectory);

        // Then
        assertThatThrownBy(() -> future.get())
                .isInstanceOf(ExecutionException.class);

        verify(fileValidator).validateImageFile(mockFile);
    }

    // ========== VALIDATION SUMMARY TESTS ==========

    @Test
    @DisplayName("ValidationSummary - Debe agregar archivos válidos e inválidos correctamente")
    void validationSummary_shouldAddValidAndInvalidFilesCorrectly() {
        // Given
        ValidationSummary summary = new ValidationSummary();

        // When
        summary.addValid("valid1.jpg");
        summary.addValid("valid2.png");
        summary.addInvalid("invalid1.exe", "Wrong type");
        summary.addInvalid("invalid2.txt", "Wrong format");

        // Then
        assertThat(summary.getValidFiles()).hasSize(2);
        assertThat(summary.getValidFiles()).containsExactly("valid1.jpg", "valid2.png");

        assertThat(summary.getInvalidFiles()).hasSize(2);
        assertThat(summary.getInvalidFiles()).containsEntry("invalid1.exe", "Wrong type");
        assertThat(summary.getInvalidFiles()).containsEntry("invalid2.txt", "Wrong format");
    }

    // ========== BATCH PROCESSING RESULT TESTS ==========

    @Test
    @DisplayName("BatchProcessingResult - Debe agregar éxitos y fallos correctamente")
    void batchProcessingResult_shouldAddSuccessesAndFailuresCorrectly() {
        // Given
        BatchProcessingResult result = new BatchProcessingResult();

        // When
        result.addSuccess("file1.jpg", "/path/to/file1.jpg");
        result.addSuccess("file2.png", "/path/to/file2.png");
        result.addFailure("file3.exe", "Invalid type");
        result.addFailure("file4.txt", "Processing error");

        // Then
        assertThat(result.getSuccessfulFiles()).hasSize(2);
        assertThat(result.getSuccessfulFiles()).containsEntry("file1.jpg", "/path/to/file1.jpg");
        assertThat(result.getSuccessfulFiles()).containsEntry("file2.png", "/path/to/file2.png");

        assertThat(result.getFailedFiles()).hasSize(2);
        assertThat(result.getFailedFiles()).containsEntry("file3.exe", "Invalid type");
        assertThat(result.getFailedFiles()).containsEntry("file4.txt", "Processing error");
    }
}
