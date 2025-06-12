package com.udea.gpx.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.udea.gpx.util.FileValidator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

/**
 * Servicio para operaciones asíncronas de archivos
 */
@Service
public class AsyncFileService {

  private static final Logger logger = LoggerFactory.getLogger(AsyncFileService.class);

  private final FileValidator fileValidator;

  public AsyncFileService(FileValidator fileValidator) {
    this.fileValidator = fileValidator;
  }

  /**
   * Procesar imagen de forma síncrona (usado internamente por el batch)
   */
  private String processImageSynchronously(MultipartFile file, String targetDirectory) throws Exception {
    logger.info("Procesando imagen: {}", file.getOriginalFilename());

    // Validar archivo
    FileValidator.ValidationResult validationResult = fileValidator.validateImageFile(file);
    if (!validationResult.isValid()) {
      throw new IllegalArgumentException(validationResult.getErrorMessage());
    }

    // Crear directorio si no existe
    Path directory = Paths.get(targetDirectory);
    if (!Files.exists(directory)) {
      Files.createDirectories(directory);
    }

    // Generar nombre único
    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null) {
      originalFilename = "unnamed_file";
    }
    String uniqueFilename = System.currentTimeMillis() + "_" + System.nanoTime() + "_" +
        originalFilename.replaceAll("[^a-zA-Z0-9.]", "_");

    // Guardar archivo
    Path targetPath = directory.resolve(uniqueFilename);
    Files.copy(file.getInputStream(), targetPath);

    logger.info("Imagen procesada exitosamente: {}", uniqueFilename);
    return targetPath.toString();
  }

  /**
   * Procesar archivo de imagen de forma asíncrona
   */
  @Async("fileProcessingExecutor")
  public CompletableFuture<String> processImageAsync(MultipartFile file, String targetDirectory) {
    logger.info("Iniciando procesamiento asíncrono de imagen: {}", file.getOriginalFilename());

    try {
      // Validar archivo
      FileValidator.ValidationResult validationResult = fileValidator.validateImageFile(file);
      if (!validationResult.isValid()) {
        throw new IllegalArgumentException(validationResult.getErrorMessage());
      }

      // Crear directorio si no existe
      Path directory = Paths.get(targetDirectory);
      if (!Files.exists(directory)) {
        Files.createDirectories(directory);
      } // Generar nombre único
      String originalFilename = file.getOriginalFilename();
      if (originalFilename == null) {
        originalFilename = "unnamed_file";
      }
      String uniqueFilename = System.currentTimeMillis() + "_" + System.nanoTime() + "_" +
          originalFilename.replaceAll("[^a-zA-Z0-9.]", "_");

      // Guardar archivo
      Path targetPath = directory.resolve(uniqueFilename);
      Files.copy(file.getInputStream(), targetPath);

      logger.info("Imagen procesada exitosamente: {}", uniqueFilename);
      return CompletableFuture.completedFuture(targetPath.toString());

    } catch (Exception e) {
      logger.error("Error procesando imagen: {}", e.getMessage(), e);
      return CompletableFuture.failedFuture(e);
    }
  }

  /**
   * Eliminar archivo de forma asíncrona
   */
  @Async("fileProcessingExecutor")
  public CompletableFuture<Boolean> deleteFileAsync(String filePath) {
    logger.info("Eliminando archivo asíncronamente: {}", filePath);

    try {
      if (filePath == null || filePath.trim().isEmpty()) {
        return CompletableFuture.completedFuture(true);
      }

      Path path = Paths.get(filePath);
      if (Files.exists(path)) {
        Files.delete(path);
        logger.info("Archivo eliminado exitosamente: {}", filePath);
      } else {
        logger.warn("Archivo no existe: {}", filePath);
      }

      return CompletableFuture.completedFuture(true);

    } catch (Exception e) {
      logger.error("Error eliminando archivo: {}", e.getMessage(), e);
      return CompletableFuture.completedFuture(false);
    }
  }

  /**
   * Limpiar archivos huérfanos de forma asíncrona
   */
  @Async("fileProcessingExecutor")
  public CompletableFuture<Integer> cleanupOrphanedFilesAsync(String directory) {
    logger.info("Iniciando limpieza de archivos huérfanos en: {}", directory);

    try {
      Path dirPath = Paths.get(directory);
      if (!Files.exists(dirPath)) {
        return CompletableFuture.completedFuture(0);
      }

      // Implementar lógica para identificar archivos huérfanos basándose en
      // referencias de BD
      // Supongamos que existe un método getReferencedFilenames() que retorna una
      // lista de archivos referenciados en la BD

      java.util.Set<String> referencedFiles = getReferencedFilenames(); // Debe implementarse según la BD real
      int deletedCount = 0;

      try (java.util.stream.Stream<Path> files = Files.list(dirPath)) {
        for (Path file : (Iterable<Path>) files::iterator) {
          if (Files.isRegularFile(file)) {
            String filename = file.getFileName().toString();
            if (!referencedFiles.contains(filename)) {
              Files.delete(file);
              deletedCount++;
              logger.info("Archivo huérfano eliminado: {}", filename);
            }
          }
        }
      }

      logger.info("Cleanup de archivos completado. Archivos eliminados: {}", deletedCount);

      return CompletableFuture.completedFuture(deletedCount);

    } catch (Exception e) {
      logger.error("Error en cleanup de archivos: {}", e.getMessage(), e);
      return CompletableFuture.completedFuture(0);
    }
  }

  /**
   * Validar múltiples archivos de forma asíncrona
   */
  @Async("fileProcessingExecutor")
  public CompletableFuture<ValidationSummary> validateMultipleFilesAsync(MultipartFile[] files) {
    logger.info("Validando {} archivos asíncronamente", files.length);

    ValidationSummary summary = new ValidationSummary();

    for (MultipartFile file : files) {
      try {
        FileValidator.ValidationResult result = fileValidator.validateImageFile(file);
        if (result.isValid()) {
          summary.addValid(file.getOriginalFilename());
        } else {
          summary.addInvalid(file.getOriginalFilename(), result.getErrorMessage());
        }
      } catch (Exception e) {
        summary.addInvalid(file.getOriginalFilename(), e.getMessage());
      }
    }

    logger.info("Validación completada. Válidos: {}, Inválidos: {}",
        summary.getValidFiles().size(), summary.getInvalidFiles().size());

    return CompletableFuture.completedFuture(summary);
  }

  /**
   * Procesar batch de archivos de forma asíncrona
   */
  @Async("fileProcessingExecutor")
  public CompletableFuture<BatchProcessingResult> processBatchAsync(MultipartFile[] files, String targetDirectory) {
    logger.info("Procesando batch de {} archivos", files.length);

    BatchProcessingResult result = new BatchProcessingResult();
    for (MultipartFile file : files) {
      try {
        // Procesar archivo directamente en lugar de usar async interno
        String savedPath = processImageSynchronously(file, targetDirectory);
        result.addSuccess(file.getOriginalFilename(), savedPath);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        result.addFailure(file.getOriginalFilename(), "Processing interrupted: " + ie.getMessage());
      } catch (Exception e) {
        result.addFailure(file.getOriginalFilename(), e.getMessage());
      }
    }

    logger.info("Batch procesado. Exitosos: {}, Fallidos: {}",
        result.getSuccessfulFiles().size(), result.getFailedFiles().size());

    return CompletableFuture.completedFuture(result);
  }

  /**
   * Obtener lista de archivos referenciados en la base de datos.
   * Este método debe ser implementado para consultar la BD real.
   */
  private java.util.Set<String> getReferencedFilenames() {
    // TODO: Implementar lógica real para obtener archivos referenciados desde la
    // base de datos.
    // Por ahora, retorna un set vacío para evitar errores de compilación.
    return new java.util.HashSet<>();
  }

  // Clases auxiliares para resultados
  public static class ValidationSummary {
    private java.util.List<String> validFiles = new java.util.ArrayList<>();
    private java.util.Map<String, String> invalidFiles = new java.util.HashMap<>();

    public void addValid(String filename) {
      validFiles.add(filename);
    }

    public void addInvalid(String filename, String error) {
      invalidFiles.put(filename, error);
    }

    public java.util.List<String> getValidFiles() {
      return validFiles;
    }

    public java.util.Map<String, String> getInvalidFiles() {
      return invalidFiles;
    }
  }

  public static class BatchProcessingResult {
    private java.util.Map<String, String> successfulFiles = new java.util.HashMap<>();
    private java.util.Map<String, String> failedFiles = new java.util.HashMap<>();

    public void addSuccess(String filename, String savedPath) {
      successfulFiles.put(filename, savedPath);
    }

    public void addFailure(String filename, String error) {
      failedFiles.put(filename, error);
    }

    public java.util.Map<String, String> getSuccessfulFiles() {
      return successfulFiles;
    }

    public java.util.Map<String, String> getFailedFiles() {
      return failedFiles;
    }
  }
}