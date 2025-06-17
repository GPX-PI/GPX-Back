package com.udea.gpx.service;

import org.springframework.stereotype.Service;

import com.udea.gpx.model.Stage;
import com.udea.gpx.repository.IStageRepository;
import com.udea.gpx.constants.AppConstants;

import java.util.List;
import java.util.Optional;

@Service
public class StageService {

    private final IStageRepository stageRepository;

    // Constructor injection (no @Autowired needed)
    public StageService(IStageRepository stageRepository) {
        this.stageRepository = stageRepository;
    }

    public List<Stage> getAllStages() {
        return stageRepository.findAll();
    }

    public List<Stage> getStagesByEventId(Long eventId) {
        return stageRepository.findAll()
                .stream()
                .filter(stage -> stage.getEvent().getId().equals(eventId))
                .toList();
    }

    public Optional<Stage> getStageById(Long id) {
        return stageRepository.findById(id);
    }

    public Stage createStage(Stage stage) {
        // Validar que el orderNumber sea único en el evento
        validateUniqueOrderNumber(stage.getEvent().getId(), stage.getOrderNumber(), null);
        return stageRepository.save(stage);
    }

    public Stage updateStage(Long id, Stage updatedStage) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.Messages.ETAPA_NO_ENCONTRADA));

        // Validar que el orderNumber sea único en el evento (excluyendo la etapa
        // actual)
        validateUniqueOrderNumber(updatedStage.getEvent().getId(), updatedStage.getOrderNumber(), id);

        stage.setName(updatedStage.getName());
        stage.setOrderNumber(updatedStage.getOrderNumber());
        stage.setNeutralized(updatedStage.isNeutralized());
        stage.setEvent(updatedStage.getEvent());

        return stageRepository.save(stage);
    }

    /**
     * Valida que el orderNumber sea único dentro del evento
     * 
     * @param eventId        ID del evento
     * @param orderNumber    número de orden a validar
     * @param excludeStageId ID de la etapa a excluir (para actualizaciones)
     */
    private void validateUniqueOrderNumber(Long eventId, Integer orderNumber, Long excludeStageId) {
        Optional<Stage> existingStage = stageRepository.findByEventIdAndOrderNumber(eventId, orderNumber);

        // Si estamos actualizando, permitir el mismo orderNumber para la misma etapa
        if (existingStage.isPresent()
                && (excludeStageId == null || !existingStage.get().getId().equals(excludeStageId))) {
            throw new IllegalArgumentException(
                    String.format("Ya existe una etapa con el número de orden %d en este evento", orderNumber));
        }
    }

    public void deleteStage(Long id) {
        stageRepository.deleteById(id);
    }

}
