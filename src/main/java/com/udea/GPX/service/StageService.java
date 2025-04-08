package com.udea.GPX.service;

import com.udea.GPX.model.Stage;
import com.udea.GPX.repository.IStageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StageService {

    @Autowired
    private IStageRepository stageRepository;

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
                .orElseThrow(() -> new RuntimeException("Stage no encontrado"));

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
