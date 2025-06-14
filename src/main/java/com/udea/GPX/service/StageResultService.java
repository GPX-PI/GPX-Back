package com.udea.gpx.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.udea.gpx.dto.ClasificacionCompletaDTO;
import com.udea.gpx.dto.CreateStageResultDTO;
import com.udea.gpx.dto.UpdateStageResultDTO;
import com.udea.gpx.model.*;
import com.udea.gpx.repository.*;
import com.udea.gpx.util.BusinessRuleValidator;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
@Transactional(readOnly = true)
public class StageResultService {

    // Constants
    private static final String RESULT_NOT_FOUND_MSG = "Resultado no encontrado";

    private final IStageResultRepository stageResultRepository;
    private final IStageRepository stageRepository;
    private final IVehicleRepository vehicleRepository;
    private final BusinessRuleValidator businessRuleValidator;

    // Constructor injection (no @Autowired needed)
    public StageResultService(
            IStageResultRepository stageResultRepository,
            IStageRepository stageRepository,
            IVehicleRepository vehicleRepository,
            BusinessRuleValidator businessRuleValidator) {
        this.stageResultRepository = stageResultRepository;
        this.stageRepository = stageRepository;
        this.vehicleRepository = vehicleRepository;
        this.businessRuleValidator = businessRuleValidator;
    }

    // Configuración de memoria optimizada
    private static final int CHUNK_SIZE = 100; // Procesar en chunks de 100 elementos

    @Transactional
    public StageResult saveResult(StageResult result) {
        return stageResultRepository.save(result);
    }

    @Transactional
    public StageResult createResult(CreateStageResultDTO createDTO) {
        // Verificar si ya existe un resultado para este vehículo en esta etapa
        if (stageResultRepository.existsByVehicleIdAndStageId(createDTO.getVehicleId(), createDTO.getStageId())) {
            throw new IllegalArgumentException(
                    "Ya existe un resultado registrado para este participante en esta etapa. " +
                            "Use la función de edición para modificar el resultado existente.");
        }

        // Buscar stage y vehicle
        Stage stage = stageRepository.findById(createDTO.getStageId())
                .orElseThrow(() -> new RuntimeException("Etapa no encontrada con ID: " + createDTO.getStageId()));

        Vehicle vehicle = vehicleRepository.findById(createDTO.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + createDTO.getVehicleId()));

        // Validar reglas de negocio
        Event event = stage.getEvent();
        businessRuleValidator.validateStageResultTimestamp(createDTO.getTimestamp(), event);
        businessRuleValidator.validateGpsCoordinates(createDTO.getLatitude(), createDTO.getLongitude());

        // Crear nuevo StageResult
        StageResult result = new StageResult();
        result.setStage(stage);
        result.setVehicle(vehicle);
        result.setTimestamp(createDTO.getTimestamp());
        result.setLatitude(createDTO.getLatitude());
        result.setLongitude(createDTO.getLongitude());

        // Inicializar todas las penalizaciones en cero por defecto
        result.setPenaltyWaypoint(Duration.ZERO);
        result.setPenaltySpeed(Duration.ZERO);
        result.setDiscountClaim(Duration.ZERO);

        return stageResultRepository.save(result);
    }

    @Transactional
    public StageResult updateResult(Long id, StageResult updatedResult) {
        return stageResultRepository.findById(id)
                .map(result -> {
                    result.setTimestamp(updatedResult.getTimestamp());
                    result.setLatitude(updatedResult.getLatitude());
                    result.setLongitude(updatedResult.getLongitude());
                    result.setElapsedTimeSeconds(updatedResult.getElapsedTimeSeconds());
                    return stageResultRepository.save(result);
                }).orElseThrow(() -> new RuntimeException(RESULT_NOT_FOUND_MSG));
    }

    @Transactional
    public StageResult updateResultFromDTO(Long id, UpdateStageResultDTO updateDTO) {
        return stageResultRepository.findById(id).map(result -> {

            // Si se está cambiando el vehículo o la etapa, verificar duplicados
            if ((updateDTO.getStageId() != null && !result.getStage().getId().equals(updateDTO.getStageId())) ||
                    (updateDTO.getVehicleId() != null
                            && !result.getVehicle().getId().equals(updateDTO.getVehicleId()))) {

                Long newVehicleId = updateDTO.getVehicleId() != null ? updateDTO.getVehicleId()
                        : result.getVehicle().getId();
                Long newStageId = updateDTO.getStageId() != null ? updateDTO.getStageId() : result.getStage().getId();

                // Verificar si ya existe otro resultado para la nueva combinación
                // vehículo-etapa
                Optional<StageResult> existingResult = stageResultRepository.findByVehicleIdAndStageId(newVehicleId,
                        newStageId);
                if (existingResult.isPresent() && !existingResult.get().getId().equals(id)) {
                    throw new IllegalArgumentException(
                            "Ya existe un resultado registrado para este participante en esta etapa.");
                }
            }

            updateStageIfDifferent(result, updateDTO);
            updateVehicleIfDifferent(result, updateDTO);
            validateBusinessRules(result, updateDTO);
            updateBasicFields(result, updateDTO);
            return stageResultRepository.save(result);
        }).orElseThrow(() -> new RuntimeException(RESULT_NOT_FOUND_MSG));
    }

    /**
     * Actualiza la etapa del resultado si es diferente
     */
    private void updateStageIfDifferent(StageResult result, UpdateStageResultDTO updateDTO) {
        if (updateDTO.getStageId() != null && !result.getStage().getId().equals(updateDTO.getStageId())) {
            Stage stage = stageRepository.findById(updateDTO.getStageId())
                    .orElseThrow(() -> new RuntimeException("Etapa no encontrada con ID: " + updateDTO.getStageId()));
            result.setStage(stage);
        }
    }

    /**
     * Actualiza el vehículo del resultado si es diferente
     */
    private void updateVehicleIfDifferent(StageResult result, UpdateStageResultDTO updateDTO) {
        if (updateDTO.getVehicleId() != null && !result.getVehicle().getId().equals(updateDTO.getVehicleId())) {
            Vehicle vehicle = vehicleRepository.findById(updateDTO.getVehicleId())
                    .orElseThrow(
                            () -> new RuntimeException("Vehículo no encontrado con ID: " + updateDTO.getVehicleId()));
            result.setVehicle(vehicle);
        }
    }

    /**
     * Valida las reglas de negocio antes de actualizar
     */
    private void validateBusinessRules(StageResult result, UpdateStageResultDTO updateDTO) {
        Event event = result.getStage().getEvent();
        if (updateDTO.getTimestamp() != null) {
            businessRuleValidator.validateStageResultTimestamp(updateDTO.getTimestamp(), event);
        }
        if (updateDTO.getLatitude() != null || updateDTO.getLongitude() != null) {
            businessRuleValidator.validateGpsCoordinates(updateDTO.getLatitude(), updateDTO.getLongitude());
        }
    }

    /**
     * Actualiza los campos básicos del resultado
     */
    private void updateBasicFields(StageResult result, UpdateStageResultDTO updateDTO) {
        if (updateDTO.getTimestamp() != null) {
            result.setTimestamp(updateDTO.getTimestamp());
        }
        if (updateDTO.getLatitude() != null) {
            result.setLatitude(updateDTO.getLatitude());
        }
        if (updateDTO.getLongitude() != null) {
            result.setLongitude(updateDTO.getLongitude());
        }
    }

    @Transactional
    public void deleteResult(Long id) {
        stageResultRepository.deleteById(id);
    }

    /**
     * Método optimizado para actualizar tiempos transcurridos por chunks
     */
    @Transactional
    public void updateElapsedTimesForEvent(Long eventId) {
        // OPTIMIZADO: Usar consulta específica en lugar de findAll().stream().filter()
        List<StageResult> allResults = stageResultRepository
                .findByEventIdWithTimestampOrderedByVehicleAndStage(eventId);

        // Procesar por chunks para optimizar memoria
        processResultsInChunks(allResults, CHUNK_SIZE);
    }

    /**
     * Procesa resultados en chunks para evitar problemas de memoria
     */
    private void processResultsInChunks(List<StageResult> allResults, int chunkSize) {
        Map<Long, List<StageResult>> resultsByVehicle = allResults.stream()
                .collect(Collectors.groupingBy(r -> r.getVehicle().getId()));

        // Dividir vehículos en chunks
        List<List<Long>> vehicleChunks = partitionList(new ArrayList<>(resultsByVehicle.keySet()), chunkSize);

        for (List<Long> vehicleChunk : vehicleChunks) {
            processVehicleChunk(vehicleChunk, resultsByVehicle);

            // Forzar liberación de memoria después de cada chunk
            // System.gc() removed as it's not recommended to force garbage collection
        }
    }

    /**
     * Procesa un chunk de vehículos
     */
    private void processVehicleChunk(List<Long> vehicleIds, Map<Long, List<StageResult>> resultsByVehicle) {
        for (Long vehicleId : vehicleIds) {
            List<StageResult> vehicleResults = resultsByVehicle.get(vehicleId);

            // Los resultados ya vienen ordenados por vehicle.id y stage.orderNumber
            for (int i = 0; i < vehicleResults.size() - 1; i++) {
                StageResult currentResult = vehicleResults.get(i);
                StageResult nextResult = vehicleResults.get(i + 1);

                if (!currentResult.getStage().isNeutralized() &&
                        currentResult.getTimestamp() != null &&
                        nextResult.getTimestamp() != null) {

                    int elapsedSeconds = (int) Duration.between(
                            currentResult.getTimestamp(),
                            nextResult.getTimestamp()).getSeconds();

                    currentResult.setElapsedTimeSeconds(elapsedSeconds);
                    stageResultRepository.save(currentResult);
                }
            }
        }
    }

    /**
     * Clasificación optimizada por categoría con procesamiento paralelo
     */
    public List<ClasificacionCompletaDTO> getClasificacionPorCategoria(Long eventId, Long categoryId) {
        List<StageResult> allResults = stageResultRepository.findByEventIdAndCategoryId(eventId, categoryId);
        return buildClasificacionOptimizada(allResults);
    }

    /**
     * Clasificación optimizada por etapa con chunks
     */
    public List<ClasificacionCompletaDTO> getClasificacionPorStage(Long eventId, Integer stageNumber) {
        List<StageResult> allResults = stageResultRepository.findByEventIdAndStageNumber(eventId, stageNumber);
        return buildClasificacionOptimizadaForStage(allResults);
    }

    /**
     * Clasificación general optimizada con procesamiento paralelo
     */
    public List<ClasificacionCompletaDTO> getClasificacionGeneral(Long eventId) {
        List<StageResult> allResults = stageResultRepository.findByEventIdOrderedForClassification(eventId);
        return buildClasificacionOptimizada(allResults);
    }

    @Transactional
    public StageResult aplicarPenalizacion(Long id, Duration penaltyWaypoint, Duration penaltySpeed,
            Duration discountClaim) {
        return stageResultRepository.findById(id)
                .map(result -> {
                    // Siempre asignar los valores, incluso PT0S (Duration.ZERO) para limpiar
                    // penalizaciones
                    result.setPenaltyWaypoint(penaltyWaypoint);
                    result.setPenaltySpeed(penaltySpeed);
                    result.setDiscountClaim(discountClaim);
                    return stageResultRepository.save(result);
                })
                .orElseThrow(() -> new RuntimeException(RESULT_NOT_FOUND_MSG));
    }

    // --- MÉTODOS AUXILIARES OPTIMIZADOS ---

    /**
     * Construcción optimizada de clasificación con procesamiento paralelo
     */
    private List<ClasificacionCompletaDTO> buildClasificacionOptimizada(List<StageResult> results) {
        // Procesar en chunks si hay muchos resultados
        if (results.size() > CHUNK_SIZE * 2) {
            return buildClasificacionWithChunks(results);
        }

        // Para conjuntos pequeños, usar el método tradicional optimizado
        return buildClasificacionCompleta(results);
    }

    /**
     * Construcción optimizada de clasificación para etapa específica
     */
    private List<ClasificacionCompletaDTO> buildClasificacionOptimizadaForStage(List<StageResult> results) {
        // Para etapas específicas, cada resultado representa un vehículo diferente
        if (results.size() > CHUNK_SIZE) {
            return results.parallelStream()
                    .map(this::buildClasificacionForSingleStage)
                    .sorted(Comparator.comparing(c -> c.getStageTimes().get(0).getAdjustedTimeSeconds()))
                    .toList();
        }

        return results.stream()
                .map(this::buildClasificacionForSingleStage)
                .sorted(Comparator.comparing(c -> c.getStageTimes().get(0).getAdjustedTimeSeconds()))
                .toList();
    }

    /**
     * Procesamiento por chunks para conjuntos grandes de datos
     */
    private List<ClasificacionCompletaDTO> buildClasificacionWithChunks(List<StageResult> results) {
        Map<Long, List<StageResult>> resultsByVehicle = results.stream()
                .collect(Collectors.groupingBy(r -> r.getVehicle().getId()));

        List<List<Long>> vehicleChunks = partitionList(new ArrayList<>(resultsByVehicle.keySet()), CHUNK_SIZE);
        List<ClasificacionCompletaDTO> allClasificaciones = new ArrayList<>();

        for (List<Long> chunk : vehicleChunks) {
            // Procesar chunk y agregar resultados
            List<ClasificacionCompletaDTO> chunkResults = chunk.parallelStream()
                    .map(vehicleId -> buildClasificacionForVehicle(resultsByVehicle.get(vehicleId)))
                    .toList();

            allClasificaciones.addAll(chunkResults);

            // Liberación de memoria intermedia
            chunkResults.clear();
        }

        // Ordenar resultado final
        allClasificaciones.sort(Comparator.comparing(ClasificacionCompletaDTO::getTotalTime));
        return allClasificaciones;
    }

    /**
     * Construye clasificación para un vehículo específico (optimizado para memoria)
     */
    private ClasificacionCompletaDTO buildClasificacionForVehicle(List<StageResult> vehicleResults) {
        Vehicle vehicle = vehicleResults.get(0).getVehicle();
        String driverName = vehicle.getUser() != null
                ? (vehicle.getUser().getFirstName() + " " + vehicle.getUser().getLastName())
                : "";
        String userPicture = vehicle.getUser() != null ? vehicle.getUser().getPicture() : "";
        String teamName = vehicle.getUser() != null ? vehicle.getUser().getTeamName() : "";

        // Stream optimizado para stage times
        List<ClasificacionCompletaDTO.StageTimeCellDTO> stageTimes = vehicleResults.stream()
                .collect(Collectors.toMap(
                        r -> r.getStage().getOrderNumber(),
                        r -> r,
                        (existing, replacement) -> replacement))
                .values().stream()
                .map(this::createStageTimeCell)
                .sorted(Comparator.comparing(ClasificacionCompletaDTO.StageTimeCellDTO::getStageOrder))
                .toList();

        // Cálculo optimizado de tiempo total
        int totalTime = stageTimes.stream()
                .mapToInt(ClasificacionCompletaDTO.StageTimeCellDTO::getAdjustedTimeSeconds)
                .sum();

        return new ClasificacionCompletaDTO(
                vehicle.getId(),
                vehicle.getName(),
                driverName,
                vehicle.getCategory().getId(),
                vehicle.getCategory().getName(),
                stageTimes,
                totalTime,
                userPicture,
                teamName);
    }

    /**
     * Crea un StageTimeCell optimizado
     */
    private ClasificacionCompletaDTO.StageTimeCellDTO createStageTimeCell(StageResult r) {
        return new ClasificacionCompletaDTO.StageTimeCellDTO(
                r.getStage().getOrderNumber(),
                r.getElapsedTimeSeconds() != null ? r.getElapsedTimeSeconds() : 0,
                r.getId(),
                r.getPenaltyWaypoint() != null ? (int) r.getPenaltyWaypoint().getSeconds() : 0,
                r.getPenaltySpeed() != null ? (int) r.getPenaltySpeed().getSeconds() : 0,
                r.getDiscountClaim() != null ? (int) r.getDiscountClaim().getSeconds() : 0);
    }

    /**
     * Construye clasificación para una sola etapa (optimizado)
     */
    private ClasificacionCompletaDTO buildClasificacionForSingleStage(StageResult r) {
        Vehicle vehicle = r.getVehicle();
        String driverName = vehicle.getUser() != null
                ? (vehicle.getUser().getFirstName() + " " + vehicle.getUser().getLastName())
                : "";
        String userPicture = vehicle.getUser() != null ? vehicle.getUser().getPicture() : "";
        String teamName = vehicle.getUser() != null ? vehicle.getUser().getTeamName() : "";

        ClasificacionCompletaDTO.StageTimeCellDTO cell = createStageTimeCell(r);
        List<ClasificacionCompletaDTO.StageTimeCellDTO> stageTimes = List.of(cell);

        return new ClasificacionCompletaDTO(
                vehicle.getId(),
                vehicle.getName(),
                driverName,
                vehicle.getCategory().getId(),
                vehicle.getCategory().getName(),
                stageTimes,
                cell.getAdjustedTimeSeconds(),
                userPicture,
                teamName);
    }

    /**
     * Método tradicional optimizado para conjuntos pequeños
     */
    private List<ClasificacionCompletaDTO> buildClasificacionCompleta(List<StageResult> results) {
        Map<Long, List<StageResult>> resultsByVehicle = results.stream()
                .collect(Collectors.groupingBy(r -> r.getVehicle().getId()));

        return resultsByVehicle.values().stream()
                .map(this::buildClasificacionForVehicle)
                .sorted(Comparator.comparing(ClasificacionCompletaDTO::getTotalTime))
                .toList();
    }

    /**
     * Divide una lista en chunks del tamaño especificado
     */
    private <T> List<List<T>> partitionList(List<T> list, int chunkSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            partitions.add(list.subList(i, Math.min(i + chunkSize, list.size())));
        }
        return partitions;
    }

    // Métodos existentes (mantenidos para compatibilidad)
    public List<StageResult> getResultsByStageRange(Event event, int stageStart, int stageEnd) {
        return stageResultRepository.findByEventIdAndStageRange(event.getId(), stageStart, stageEnd);
    }

    public List<StageResult> getResultsByEvent(Long eventId) {
        return stageResultRepository.findByEventIdOrderedForClassification(eventId);
    }

}