package com.udea.GPX.service;

import com.udea.GPX.dto.ClasificacionDTO;
import com.udea.GPX.model.*;
import com.udea.GPX.repository.IStageRepository;
import com.udea.GPX.repository.IStageResultRepository;
import com.udea.GPX.repository.IVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StageResultService {

    @Autowired
    private IStageResultRepository stageResultRepository;

    @Autowired
    private IStageRepository stageRepository;

    @Autowired
    private IVehicleRepository vehicleRepository;

    public List<StageResult> getAllResults() {
        return stageResultRepository.findAll();
    }

    public Optional<StageResult> getResultById(Long id) {
        return stageResultRepository.findById(id);
    }

    public StageResult saveResult(StageResult result) {
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

    public void deleteResult(Long id) {
        stageResultRepository.deleteById(id);
    }

    public List<StageResult> getResultsByCategoryAndStages(Long categoryId, int stageStart, int stageEnd) {
        List<Stage> stagesInRange = stageRepository.findAll().stream()
                .filter(s -> s.getOrderNumber() >= stageStart && s.getOrderNumber() <= stageEnd)
                .toList();

        return stageResultRepository.findAll().stream()
                .filter(r -> r.getVehicle().getCategory().getId().equals(categoryId))
                .filter(r -> stagesInRange.contains(r.getStage()))
                .filter(r -> !r.getStage().isNeutralized()) // Omitir stages neutralizados
                .sorted(Comparator.comparing(StageResult::getTimestamp)) // Ordenar por tiempo
                .collect(Collectors.toList());
    }

    public Duration calcularTiempoTotal(Vehicle vehicle, Event event) {
        // Obtener el stage inicial (orderNumber = 0)
        Optional<Stage> stageInicial = stageRepository.findByEventAndOrderNumber(event, 0);
        if (stageInicial.isEmpty()) return Duration.ZERO;

        // Obtener todos los resultados del vehículo en ese evento
        List<StageResult> resultados = stageResultRepository.findByVehicleAndStage_Event(vehicle, event);

        // Ordenarlos por orderNumber
        resultados.sort(Comparator.comparing(sr -> sr.getStage().getOrderNumber()));

        // Filtrar resultados que no están neutralizados
        List<StageResult> relevantes = resultados.stream()
                .filter(sr -> !sr.getStage().isNeutralized())
                .toList();

        if (relevantes.isEmpty()) return Duration.ZERO;

        // Calcular tiempo total desde el primer registro (inicio)
        LocalDateTime inicio = stageResultRepository.findByVehicleAndStage(vehicle, stageInicial.get())
                .map(StageResult::getTimestamp)
                .orElse(relevantes.get(0).getTimestamp());

        LocalDateTime fin = relevantes.get(relevantes.size() - 1).getTimestamp();
        Duration total = Duration.between(inicio, fin);

        // Aplicar penalizaciones y descuentos
        Duration penalizaciones = Duration.ZERO;
        Duration descuentos = Duration.ZERO;

        for (StageResult sr : relevantes) {
            if (sr.getPenaltyWaypoint() != null) penalizaciones = penalizaciones.plus(sr.getPenaltyWaypoint());
            if (sr.getPenaltySpeed() != null) penalizaciones = penalizaciones.plus(sr.getPenaltySpeed());
            if (sr.getDiscountClaim() != null) descuentos = descuentos.plus(sr.getDiscountClaim());
        }

        return total.plus(penalizaciones).minus(descuentos);
    }


    public List<ClasificacionDTO> getClasificacionPorCategoria(Event event, Category category) {
        List<Vehicle> vehiculos = vehicleRepository.findByCategory(category);
        List<ClasificacionDTO> clasificacion = new ArrayList<>();

        for (Vehicle v : vehiculos) {
            Duration tiempoTotal = calcularTiempoTotal(v, event);
            clasificacion.add(new ClasificacionDTO(v, tiempoTotal));
        }

        // Ordenar por menor tiempo
        clasificacion.sort(Comparator.comparing(ClasificacionDTO::getTiempoTotal));

        return clasificacion;
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


}