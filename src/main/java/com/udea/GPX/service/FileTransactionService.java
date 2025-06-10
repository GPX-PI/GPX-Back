package com.udea.GPX.service;

import com.udea.GPX.exception.FileOperationException;
import com.udea.GPX.exception.FileOperationException.FileErrorType;
import com.udea.GPX.util.FileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Servicio transaccional para operaciones de archivos con rollback automático
 */
@Service
public class FileTransactionService {

  private static final Logger logger = LoggerFactory.getLogger(FileTransactionService.class);

  @Autowired
  private FileValidator fileValidator;

  // ThreadLocal para manejar transacciones de archivos por hilo
  private final ThreadLocal<FileTransaction> currentTransaction = new ThreadLocal<>();

  /**
   * Inicia una nueva transacción de archivos
   */
  public void beginTransaction() {
    if (currentTransaction.get() != null) {
      logger.warn("Se está iniciando una nueva transacción cuando ya existe una activa");
    }
    currentTransaction.set(new FileTransaction());
    logger.debug("Transacción de archivos iniciada");
  }

  /**
   * Confirma la transacción actual
   */
  public void commitTransaction() {
    FileTransaction transaction = currentTransaction.get();
    if (transaction != null) {
      transaction.commit();
      currentTransaction.remove();
      logger.debug("Transacción de archivos confirmada");
    }
  }

  /**
   * Revierte la transacción actual
   */
  public void rollbackTransaction() {
    FileTransaction transaction = currentTransaction.get();
    if (transaction != null) {
      transaction.rollback();
      currentTransaction.remove();
      logger.info("Transacción de archivos revertida");
    }
  }

  /**
   * Valida y guarda un archivo con rollback automático
   */
  public String saveFileTransactional(MultipartFile file, String fileType, String directory) {
    FileTransaction transaction = ensureTransaction();

    try {
      // Validar archivo según tipo
      FileValidator.ValidationResult validationResult;
      if ("image".equals(fileType) || "picture".equals(fileType)) {
        validationResult = fileValidator.validateImageFile(file);
      } else if ("document".equals(fileType) || "insurance".equals(fileType)) {
        validationResult = fileValidator.validateDocumentFile(file);
      } else {
        throw new FileOperationException(
            "Tipo de archivo no soportado: " + fileType,
            FileErrorType.VALIDATION_ERROR,
            file.getOriginalFilename(),
            "SAVE");
      }

      if (!validationResult.isValid()) {
        throw new FileOperationException(
            "Archivo no válido: " + validationResult.getErrorMessage(),
            FileErrorType.VALIDATION_ERROR,
            file.getOriginalFilename(),
            "SAVE");
      }

      // Generar nombre único del archivo
      String fileName = generateUniqueFileName(file.getOriginalFilename());
      String uploadDir = directory != null ? directory : "uploads/";
      Path filePath = Paths.get(uploadDir, fileName);

      // Crear directorio si no existe
      Files.createDirectories(filePath.getParent());

      // Guardar archivo
      Files.write(filePath, file.getBytes());

      // Agregar a la transacción para posible rollback
      transaction.addCreatedFile(filePath.toString());

      logger.info("Archivo guardado exitosamente: {}", filePath);
      return filePath.toString();

    } catch (IOException e) {
      throw new FileOperationException(
          "Error al guardar archivo: " + e.getMessage(),
          e,
          FileErrorType.STORAGE_ERROR,
          file.getOriginalFilename(),
          "SAVE");
    } catch (Exception e) {
      if (e instanceof FileOperationException) {
        throw e;
      }
      throw new FileOperationException(
          "Error inesperado al procesar archivo: " + e.getMessage(),
          e,
          FileErrorType.PROCESSING_ERROR,
          file.getOriginalFilename(),
          "SAVE");
    }
  }

  /**
   * Elimina un archivo con rollback automático
   */
  public void deleteFileTransactional(String filePath) {
    if (filePath == null || filePath.trim().isEmpty() || filePath.startsWith("http")) {
      return; // No eliminar URLs externas o rutas vacías
    }

    FileTransaction transaction = ensureTransaction();

    try {
      Path path = Paths.get(filePath);
      if (Files.exists(path)) {
        // Hacer backup del archivo antes de eliminarlo
        String backupPath = createBackup(path);
        transaction.addBackupFile(filePath, backupPath);

        // Eliminar archivo
        Files.delete(path);
        logger.info("Archivo eliminado exitosamente: {}", filePath);
      } else {
        logger.warn("Archivo no encontrado para eliminar: {}", filePath);
      }

    } catch (IOException e) {
      throw new FileOperationException(
          "Error al eliminar archivo: " + e.getMessage(),
          e,
          FileErrorType.DELETION_ERROR,
          filePath,
          "DELETE");
    }
  }

  /**
   * Actualiza un archivo (elimina el anterior y guarda el nuevo)
   */
  public String updateFileTransactional(MultipartFile newFile, String oldFilePath, String fileType, String directory) {
    FileTransaction transaction = ensureTransaction();

    try {
      // Eliminar archivo anterior si existe
      if (oldFilePath != null && !oldFilePath.trim().isEmpty() && !oldFilePath.startsWith("http")) {
        deleteFileTransactional(oldFilePath);
      }

      // Guardar nuevo archivo
      return saveFileTransactional(newFile, fileType, directory);

    } catch (Exception e) {
      // El rollback se manejará automáticamente
      throw e;
    }
  }

  // Métodos privados auxiliares

  private FileTransaction ensureTransaction() {
    FileTransaction transaction = currentTransaction.get();
    if (transaction == null) {
      beginTransaction();
      transaction = currentTransaction.get();
    }
    return transaction;
  }

  private String generateUniqueFileName(String originalFileName) {
    if (originalFileName == null || originalFileName.isEmpty()) {
      throw new FileOperationException(
          "Nombre de archivo no válido",
          FileErrorType.VALIDATION_ERROR);
    }

    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String extension = "";
    int dotIndex = originalFileName.lastIndexOf('.');
    if (dotIndex > 0) {
      extension = originalFileName.substring(dotIndex);
    }

    return timestamp + "_" + System.nanoTime() + extension;
  }

  private String createBackup(Path originalPath) throws IOException {
    String backupDir = "backups/";
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    String backupFileName = timestamp + "_backup_" + originalPath.getFileName();
    Path backupPath = Paths.get(backupDir, backupFileName);

    Files.createDirectories(backupPath.getParent());
    Files.copy(originalPath, backupPath);

    return backupPath.toString();
  }

  /**
   * Clase interna para manejar transacciones de archivos
   */
  private static class FileTransaction {
    private final List<String> createdFiles = new ArrayList<>();
    private final List<BackupInfo> backupFiles = new ArrayList<>();
    private boolean committed = false;

    public void addCreatedFile(String filePath) {
      createdFiles.add(filePath);
    }

    public void addBackupFile(String originalPath, String backupPath) {
      backupFiles.add(new BackupInfo(originalPath, backupPath));
    }

    public void commit() {
      // Eliminar archivos de backup ya que la transacción fue exitosa
      for (BackupInfo backup : backupFiles) {
        try {
          Path backupPath = Paths.get(backup.backupPath);
          if (Files.exists(backupPath)) {
            Files.delete(backupPath);
          }
        } catch (IOException e) {
          // Log pero no fallar la transacción
          logger.warn("No se pudo eliminar backup: {}", backup.backupPath, e);
        }
      }
      committed = true;
    }

    public void rollback() {
      if (committed) {
        logger.warn("Intentando hacer rollback de una transacción ya confirmada");
        return;
      }

      // Eliminar archivos creados durante la transacción
      for (String filePath : createdFiles) {
        try {
          Path path = Paths.get(filePath);
          if (Files.exists(path)) {
            Files.delete(path);
            logger.debug("Archivo eliminado durante rollback: {}", filePath);
          }
        } catch (IOException e) {
          logger.error("Error al eliminar archivo durante rollback: {}", filePath, e);
        }
      }

      // Restaurar archivos desde backup
      for (BackupInfo backup : backupFiles) {
        try {
          Path originalPath = Paths.get(backup.originalPath);
          Path backupPath = Paths.get(backup.backupPath);

          if (Files.exists(backupPath)) {
            Files.createDirectories(originalPath.getParent());
            Files.copy(backupPath, originalPath);
            Files.delete(backupPath);
            logger.debug("Archivo restaurado desde backup: {}", backup.originalPath);
          }
        } catch (IOException e) {
          logger.error("Error al restaurar archivo desde backup: {}", backup.originalPath, e);
        }
      }
    }

    private static class BackupInfo {
      final String originalPath;
      final String backupPath;

      BackupInfo(String originalPath, String backupPath) {
        this.originalPath = originalPath;
        this.backupPath = backupPath;
      }
    }
  }
}