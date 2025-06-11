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
        return stageRepository.save(stage);
    }

    public Stage updateStage(Long id, Stage updatedStage) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.Messages.ETAPA_NO_ENCONTRADA));

        stage.setName(updatedStage.getName());
        stage.setOrderNumber(updatedStage.getOrderNumber());
        stage.setNeutralized(updatedStage.isNeutralized());
        stage.setEvent(updatedStage.getEvent());

        return stageRepository.save(stage);
    }

    public void deleteStage(Long id) {
        stageRepository.deleteById(id);
    }

}
