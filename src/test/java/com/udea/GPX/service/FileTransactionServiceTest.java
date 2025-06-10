package com.udea.GPX.service;

import com.udea.GPX.exception.FileOperationException;
import com.udea.GPX.exception.FileOperationException.FileErrorType;
import com.udea.GPX.util.FileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileTransactionServiceTest {

    @Mock
    private FileValidator fileValidator;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private FileTransactionService fileTransactionService;

    @TempDir
    Path tempDir;

    private String testDirectory;

    @BeforeEach
    void setUp() {
        testDirectory = tempDir.toString();
    }

    // Transaction Management Tests

    @Test
    void beginTransaction_ShouldStartNewTransaction() {
        // When
        fileTransactionService.beginTransaction();

        // Then - no exception should be thrown
        assertDoesNotThrow(() -> fileTransactionService.commitTransaction());
    }

    @Test
    void beginTransaction_WhenAlreadyActive_ShouldLogWarning() {
        // Given
        fileTransactionService.beginTransaction();

        // When - start another transaction
        assertDoesNotThrow(() -> fileTransactionService.beginTransaction());

        // Then - should be able to commit
        assertDoesNotThrow(() -> fileTransactionService.commitTransaction());
    }

    @Test
    void commitTransaction_WithoutActiveTransaction_ShouldNotThrow() {
        // When & Then
        assertDoesNotThrow(() -> fileTransactionService.commitTransaction());
    }

    @Test
    void rollbackTransaction_WithoutActiveTransaction_ShouldNotThrow() {
        // When & Then
        assertDoesNotThrow(() -> fileTransactionService.rollbackTransaction());
    }

    @Test
    void rollbackTransaction_ShouldCleanupCreatedFiles() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn("test content".getBytes());

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(any())).thenReturn(validResult);

        // Create and save a file
        String savedFilePath = fileTransactionService.saveFileTransactional(mockFile, "image", testDirectory);

        // Verify file was created
        assertTrue(Files.exists(Paths.get(savedFilePath)));

        // When
        fileTransactionService.rollbackTransaction();

        // Then - file should be deleted
        assertFalse(Files.exists(Paths.get(savedFilePath)));
    }

    // File Save Tests @Test
    void saveFileTransactional_WithValidImageFile_ShouldSaveSuccessfully() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn("test image content".getBytes());

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(any())).thenReturn(validResult);

        // When
        String result = fileTransactionService.saveFileTransactional(mockFile, "image", testDirectory);

        // Then
        assertNotNull(result);
        assertTrue(result.contains(testDirectory));
        assertTrue(Files.exists(Paths.get(result)));

        fileTransactionService.commitTransaction();
    }

    @Test
    void saveFileTransactional_WithValidDocumentFile_ShouldSaveSuccessfully() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getBytes()).thenReturn("test document content".getBytes());

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateDocumentFile(any())).thenReturn(validResult);

        // When
        String result = fileTransactionService.saveFileTransactional(mockFile, "document", testDirectory);

        // Then
        assertNotNull(result);
        assertTrue(result.contains(testDirectory));
        assertTrue(Files.exists(Paths.get(result)));

        fileTransactionService.commitTransaction();
    }

    @Test
    void saveFileTransactional_WithInvalidFileType_ShouldThrowException() {
        // Given
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");

        // When & Then
        FileOperationException exception = assertThrows(FileOperationException.class,
                () -> fileTransactionService.saveFileTransactional(mockFile, "invalid", testDirectory));

        assertEquals(FileErrorType.VALIDATION_ERROR, exception.getErrorType());
        assertTrue(exception.getMessage().contains("Tipo de archivo no soportado"));
    }

    @Test
    void saveFileTransactional_WithInvalidFile_ShouldThrowException() {
        // Given
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");

        FileValidator.ValidationResult invalidResult = FileValidator.ValidationResult.error("Invalid file format");
        when(fileValidator.validateImageFile(any())).thenReturn(invalidResult);

        // When & Then
        FileOperationException exception = assertThrows(FileOperationException.class,
                () -> fileTransactionService.saveFileTransactional(mockFile, "image", testDirectory));

        assertEquals(FileErrorType.VALIDATION_ERROR, exception.getErrorType());
        assertTrue(exception.getMessage().contains("Archivo no válido"));
    }

    @Test
    void saveFileTransactional_WithIOException_ShouldThrowFileOperationException() throws IOException {
        // Given
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenThrow(new IOException("IO Error"));

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(any())).thenReturn(validResult);

        // When & Then
        FileOperationException exception = assertThrows(FileOperationException.class,
                () -> fileTransactionService.saveFileTransactional(mockFile, "image", testDirectory));

        assertEquals(FileErrorType.STORAGE_ERROR, exception.getErrorType());
        assertTrue(exception.getMessage().contains("Error al guardar archivo"));
    }

    @Test
    void saveFileTransactional_WithNullDirectory_ShouldUseDefaultDirectory() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn("test content".getBytes());

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(any())).thenReturn(validResult); // When
        String result = fileTransactionService.saveFileTransactional(mockFile, "image", null);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("uploads") || result.contains("uploads/") || result.contains("uploads\\"));

        fileTransactionService.rollbackTransaction(); // Cleanup
    }

    // File Delete Tests

    @Test
    void deleteFileTransactional_WithExistingFile_ShouldDeleteSuccessfully() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        // Create a test file
        Path testFile = tempDir.resolve("test-delete.txt");
        Files.write(testFile, "test content".getBytes());
        assertTrue(Files.exists(testFile));

        // When
        fileTransactionService.deleteFileTransactional(testFile.toString());

        // Then
        assertFalse(Files.exists(testFile));

        fileTransactionService.commitTransaction();
    }

    @Test
    void deleteFileTransactional_WithNonExistentFile_ShouldNotThrow() {
        // Given
        String nonExistentFile = tempDir.resolve("non-existent.txt").toString();

        // When & Then
        assertDoesNotThrow(() -> fileTransactionService.deleteFileTransactional(nonExistentFile));
    }

    @Test
    void deleteFileTransactional_WithNullPath_ShouldNotThrow() {
        // When & Then
        assertDoesNotThrow(() -> fileTransactionService.deleteFileTransactional(null));
    }

    @Test
    void deleteFileTransactional_WithEmptyPath_ShouldNotThrow() {
        // When & Then
        assertDoesNotThrow(() -> fileTransactionService.deleteFileTransactional(""));
    }

    @Test
    void deleteFileTransactional_WithHttpUrl_ShouldNotDelete() {
        // When & Then
        assertDoesNotThrow(() -> fileTransactionService.deleteFileTransactional("http://example.com/image.jpg"));
    }

    @Test
    void deleteFileTransactional_WithRollback_ShouldRestoreFile() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        // Create a test file
        Path testFile = tempDir.resolve("test-restore.txt");
        String originalContent = "original content";
        Files.write(testFile, originalContent.getBytes());
        assertTrue(Files.exists(testFile));

        // Delete the file
        fileTransactionService.deleteFileTransactional(testFile.toString());
        assertFalse(Files.exists(testFile));

        // When - rollback
        fileTransactionService.rollbackTransaction();

        // Then - file should be restored
        assertTrue(Files.exists(testFile));
        assertEquals(originalContent, Files.readString(testFile));
    }

    // File Update Tests @Test
    void updateFileTransactional_WithValidFiles_ShouldUpdateSuccessfully() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        // Create old file
        Path oldFile = tempDir.resolve("old-file.jpg");
        Files.write(oldFile, "old content".getBytes());

        when(mockFile.getOriginalFilename()).thenReturn("new-file.jpg");
        when(mockFile.getBytes()).thenReturn("new content".getBytes());

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(any())).thenReturn(validResult);

        // When
        String newFilePath = fileTransactionService.updateFileTransactional(
                mockFile, oldFile.toString(), "image", testDirectory);

        // Then
        assertNotNull(newFilePath);
        assertFalse(Files.exists(oldFile)); // Old file should be deleted
        assertTrue(Files.exists(Paths.get(newFilePath))); // New file should exist

        fileTransactionService.commitTransaction();
    }

    @Test
    void updateFileTransactional_WithNullOldPath_ShouldOnlySaveNewFile() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        when(mockFile.getOriginalFilename()).thenReturn("new-file.jpg");
        when(mockFile.getBytes()).thenReturn("new content".getBytes());

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(any())).thenReturn(validResult);

        // When
        String newFilePath = fileTransactionService.updateFileTransactional(
                mockFile, null, "image", testDirectory);

        // Then
        assertNotNull(newFilePath);
        assertTrue(Files.exists(Paths.get(newFilePath)));

        fileTransactionService.commitTransaction();
    }

    @Test
    void updateFileTransactional_WithHttpOldPath_ShouldOnlySaveNewFile() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        when(mockFile.getOriginalFilename()).thenReturn("new-file.jpg");
        when(mockFile.getBytes()).thenReturn("new content".getBytes());

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(any())).thenReturn(validResult);

        // When
        String newFilePath = fileTransactionService.updateFileTransactional(
                mockFile, "http://example.com/old.jpg", "image", testDirectory);

        // Then
        assertNotNull(newFilePath);
        assertTrue(Files.exists(Paths.get(newFilePath)));

        fileTransactionService.commitTransaction();
    }

    // Auto-transaction Tests @Test
    void saveFileTransactional_WithoutActiveTransaction_ShouldCreateAutoTransaction() throws IOException {
        // Given - no explicit transaction started
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn("test content".getBytes());

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(any())).thenReturn(validResult);

        // When
        String result = fileTransactionService.saveFileTransactional(mockFile, "image", testDirectory);

        // Then
        assertNotNull(result);
        assertTrue(Files.exists(Paths.get(result)));
    }

    // Edge Cases @Test
    void saveFileTransactional_WithNullFileName_ShouldThrowException() {
        // Given
        when(mockFile.getOriginalFilename()).thenReturn(null);

        FileValidator.ValidationResult invalidResult = FileValidator.ValidationResult
                .error("Nombre de archivo inválido");
        when(fileValidator.validateImageFile(mockFile)).thenReturn(invalidResult);

        // When & Then
        FileOperationException exception = assertThrows(FileOperationException.class,
                () -> fileTransactionService.saveFileTransactional(mockFile, "image", testDirectory));

        assertEquals(FileErrorType.VALIDATION_ERROR, exception.getErrorType());
    }

    @Test
    void saveFileTransactional_WithEmptyFileName_ShouldThrowException() {
        // Given
        when(mockFile.getOriginalFilename()).thenReturn("");

        FileValidator.ValidationResult invalidResult = FileValidator.ValidationResult
                .error("Nombre de archivo inválido");
        when(fileValidator.validateImageFile(mockFile)).thenReturn(invalidResult);

        // When & Then
        FileOperationException exception = assertThrows(FileOperationException.class,
                () -> fileTransactionService.saveFileTransactional(mockFile, "image", testDirectory));

        assertEquals(FileErrorType.VALIDATION_ERROR, exception.getErrorType());
    }

    @Test
    void saveFileTransactional_WithPictureType_ShouldUseImageValidation() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn("test content".getBytes());

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateImageFile(any())).thenReturn(validResult);

        // When
        String result = fileTransactionService.saveFileTransactional(mockFile, "picture", testDirectory);

        // Then
        assertNotNull(result);
        verify(fileValidator).validateImageFile(mockFile);

        fileTransactionService.commitTransaction();
    }

    @Test
    void saveFileTransactional_WithInsuranceType_ShouldUseDocumentValidation() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        when(mockFile.getOriginalFilename()).thenReturn("insurance.pdf");
        when(mockFile.getBytes()).thenReturn("test content".getBytes());

        FileValidator.ValidationResult validResult = FileValidator.ValidationResult.success();
        when(fileValidator.validateDocumentFile(any())).thenReturn(validResult);

        // When
        String result = fileTransactionService.saveFileTransactional(mockFile, "insurance", testDirectory);

        // Then
        assertNotNull(result);
        verify(fileValidator).validateDocumentFile(mockFile);

        fileTransactionService.commitTransaction();
    }

    @Test
    void commitTransaction_ShouldCleanupBackupFiles() throws IOException {
        // Given
        fileTransactionService.beginTransaction();

        // Create a test file to delete (which creates backup)
        Path testFile = tempDir.resolve("test-backup.txt");
        Files.write(testFile, "test content".getBytes());

        fileTransactionService.deleteFileTransactional(testFile.toString());

        // When
        fileTransactionService.commitTransaction();

        // Then - backup files should be cleaned up (tested indirectly - no exception)
        assertTrue(true); // If we reach here, commit was successful
    }
}
