package com.udea.GPX.service;

import com.udea.GPX.dto.ClasificacionCompletaDTO;
import com.udea.GPX.dto.CreateStageResultDTO;
import com.udea.GPX.dto.UpdateStageResultDTO;
import com.udea.GPX.model.*;
import com.udea.GPX.repository.IStageRepository;
import com.udea.GPX.repository.IStageResultRepository;
import com.udea.GPX.repository.IVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;

@Service
public class StageResultService {

    @Autowired
    private IStageResultRepository stageResultRepository;

    @Autowired
    private IStageRepository stageRepository;

    @Autowired
    private IVehicleRepository vehicleRepository;

    public StageResult saveResult(StageResult result) {
        return stageResultRepository.save(result);
    }

    public StageResult createResult(CreateStageResultDTO createDTO) {
        // Buscar entidades por ID
        Stage stage = stageRepository.findById(createDTO.getStageId())
                .orElseThrow(() -> new RuntimeException("Etapa no encontrada con ID: " + createDTO.getStageId()));

        Vehicle vehicle = vehicleRepository.findById(createDTO.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con ID: " + createDTO.getVehicleId()));

        // Crear nueva entidad StageResult
        StageResult result = new StageResult();
        result.setStage(stage);
        result.setVehicle(vehicle);
        result.setTimestamp(createDTO.getTimestamp());
        result.setLatitude(createDTO.getLatitude());
        result.setLongitude(createDTO.getLongitude());

        return stageResultRepository.save(result);
    }

    public StageResult updateResult(Long id, StageResult updatedResult) {
        return stageResultRepository.findById(id).map(result -> {
            result.setTimestamp(updatedResult.getTimestamp());
            result.setLatitude(updatedResult.getLatitude());
            result.setLongitude(updatedResult.getLongitude());
            result.setPenaltyWaypoint(updatedResult.getPenaltyWaypoint());
            result.setPenaltySpeed(updatedResult.getPenaltySpeed());
            result.setDiscountClaim(updatedResult.getDiscountClaim());
            return stageResultRepository.save(result);
        }).orElseThrow(() -> new RuntimeException("Resultado no encontrado"));
    }

    public StageResult updateResultFromDTO(Long id, UpdateStageResultDTO updateDTO) {
        return stageResultRepository.findById(id).map(result -> {
            // Actualizar stage si es diferente
            if (updateDTO.getStageId() != null && !result.getStage().getId().equals(updateDTO.getStageId())) {
                Stage stage = stageRepository.findById(updateDTO.getStageId())
                        .orElseThrow(
                                () -> new RuntimeException("Etapa no encontrada con ID: " + updateDTO.getStageId()));
                result.setStage(stage);
            }

            // Actualizar vehicle si es diferente
            if (updateDTO.getVehicleId() != null && !result.getVehicle().getId().equals(updateDTO.getVehicleId())) {
                Vehicle vehicle = vehicleRepository.findById(updateDTO.getVehicleId())
                        .orElseThrow(() -> new RuntimeException(
                                "Vehículo no encontrado con ID: " + updateDTO.getVehicleId()));
                result.setVehicle(vehicle);
            }

            // Actualizar campos básicos
            if (updateDTO.getTimestamp() != null) {
                result.setTimestamp(updateDTO.getTimestamp());
            }
            if (updateDTO.getLatitude() != null) {
                result.setLatitude(updateDTO.getLatitude());
            }
            if (updateDTO.getLongitude() != null) {
                result.setLongitude(updateDTO.getLongitude());
            }

            return stageResultRepository.save(result);
        }).orElseThrow(() -> new RuntimeException("Resultado no encontrado"));
    }

    public void deleteResult(Long id) {
        stageResultRepository.deleteById(id);
    }

    public void updateElapsedTimesForEvent(Long eventId) {
        List<StageResult> allResults = stageResultRepository.findAll().stream()
                .filter(r -> r.getStage().getEvent().getId().equals(eventId))
                .toList();

        Map<Long, List<StageResult>> resultsByVehicle = allResults.stream()
                .collect(Collectors.groupingBy(r -> r.getVehicle().getId()));

        for (List<StageResult> vehicleResults : resultsByVehicle.values()) {
            vehicleResults.sort(Comparator.comparing(r -> r.getStage().getOrderNumber()));

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

    public List<ClasificacionCompletaDTO> getClasificacionPorCategoria(Long eventId, Long categoryId) {
        List<Vehicle> vehicles = vehicleRepository.findByCategory(new Category(categoryId));
        List<StageResult> allResults = stageResultRepository.findAll().stream()
                .filter(r -> r.getStage().getEvent().getId().equals(eventId))
                .filter(r -> vehicles.contains(r.getVehicle()))
                .collect(Collectors.toList());
        return buildClasificacionCompleta(allResults);
    }

    public List<ClasificacionCompletaDTO> getClasificacionPorStage(Long eventId, Integer stageNumber) {
        List<StageResult> allResults = stageResultRepository.findAll().stream()
                .filter(r -> r.getStage().getEvent().getId().equals(eventId))
                .filter(r -> Objects.equals(r.getStage().getOrderNumber(), stageNumber))
                .collect(Collectors.toList());
        return buildClasificacionCompleta(allResults, stageNumber);
    }

    public List<ClasificacionCompletaDTO> getClasificacionGeneral(Long eventId) {
        List<StageResult> allResults = stageResultRepository.findAll().stream()
                .filter(r -> r.getStage().getEvent().getId().equals(eventId))
                .collect(Collectors.toList());
        return buildClasificacionCompleta(allResults);
    }

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
                .orElseThrow(() -> new RuntimeException("Resultado no encontrado"));
    }

    // --- MÉTODOS AUXILIARES ---
    private List<ClasificacionCompletaDTO> buildClasificacionCompleta(List<StageResult> results) {
        Map<Long, List<StageResult>> resultsByVehicle = results.stream()
                .collect(Collectors.groupingBy(r -> r.getVehicle().getId()));

        return resultsByVehicle.values().stream()
                .map(vehicleResults -> {
                    Vehicle vehicle = vehicleResults.get(0).getVehicle();
                    String driverName = vehicle.getUser() != null
                            ? (vehicle.getUser().getFirstName() + " " + vehicle.getUser().getLastName())
                            : "";
                    String userPicture = vehicle.getUser() != null ? vehicle.getUser().getPicture() : "";
                    String teamName = vehicle.getUser() != null ? vehicle.getUser().getTeamName() : "";
                    // Crear lista de StageTimeCellDTO con penalizaciones y descuentos
                    List<ClasificacionCompletaDTO.StageTimeCellDTO> stageTimes = vehicleResults.stream()
                            .collect(Collectors.toMap(
                                    r -> r.getStage().getOrderNumber(),
                                    r -> r,
                                    (existing, replacement) -> replacement))
                            .values().stream()
                            .map(r -> new ClasificacionCompletaDTO.StageTimeCellDTO(
                                    r.getStage().getOrderNumber(),
                                    r.getElapsedTimeSeconds() != null ? r.getElapsedTimeSeconds() : 0,
                                    r.getId(),
                                    r.getPenaltyWaypoint() != null ? (int) r.getPenaltyWaypoint().getSeconds() : 0,
                                    r.getPenaltySpeed() != null ? (int) r.getPenaltySpeed().getSeconds() : 0,
                                    r.getDiscountClaim() != null ? (int) r.getDiscountClaim().getSeconds() : 0))
                            .sorted(Comparator.comparing(ClasificacionCompletaDTO.StageTimeCellDTO::getStageOrder))
                            .collect(Collectors.toList());
                    // Sumar los tiempos ajustados
                    Integer totalTime = stageTimes.stream()
                            .mapToInt(ClasificacionCompletaDTO.StageTimeCellDTO::getAdjustedTimeSeconds).sum();
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
                })
                .sorted(Comparator.comparing(ClasificacionCompletaDTO::getTotalTime))
                .collect(Collectors.toList());
    }

    private List<ClasificacionCompletaDTO> buildClasificacionCompleta(List<StageResult> results, Integer stageNumber) {
        return results.stream()
                .map(r -> {
                    Vehicle vehicle = r.getVehicle();
                    String driverName = vehicle.getUser() != null
                            ? (vehicle.getUser().getFirstName() + " " + vehicle.getUser().getLastName())
                            : "";
                    String userPicture = vehicle.getUser() != null ? vehicle.getUser().getPicture() : "";
                    String teamName = vehicle.getUser() != null ? vehicle.getUser().getTeamName() : "";
                    ClasificacionCompletaDTO.StageTimeCellDTO cell = new ClasificacionCompletaDTO.StageTimeCellDTO(
                            stageNumber,
                            r.getElapsedTimeSeconds() != null ? r.getElapsedTimeSeconds() : 0,
                            r.getId(),
                            r.getPenaltyWaypoint() != null ? (int) r.getPenaltyWaypoint().getSeconds() : 0,
                            r.getPenaltySpeed() != null ? (int) r.getPenaltySpeed().getSeconds() : 0,
                            r.getDiscountClaim() != null ? (int) r.getDiscountClaim().getSeconds() : 0);
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
                })
                .sorted(Comparator.comparing(c -> c.getStageTimes().get(0).getAdjustedTimeSeconds()))
                .collect(Collectors.toList());
    }

    public List<StageResult> getResultsByStageRange(Event event, int stageStart, int stageEnd) {
        List<Stage> stagesInRange = stageRepository.findByEventAndOrderNumberBetween(
                event, stageStart, stageEnd);

        return stageResultRepository.findAll().stream()
                .filter(r -> stagesInRange.contains(r.getStage()))
                .filter(r -> !r.getStage().isNeutralized())
                .sorted(Comparator.comparing(StageResult::getTimestamp))
                .collect(Collectors.toList());
    }

    public List<StageResult> getResultsByEvent(Long eventId) {
        return stageResultRepository.findAll().stream()
                .filter(r -> r.getStage().getEvent().getId().equals(eventId))
                .sorted(Comparator.comparing((StageResult r) -> r.getStage().getOrderNumber())
                        .thenComparing(StageResult::getTimestamp))
                .collect(Collectors.toList());
    }

}