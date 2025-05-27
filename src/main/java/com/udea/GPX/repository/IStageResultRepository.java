package com.udea.GPX.repository;

import com.udea.GPX.model.Event;
import com.udea.GPX.model.Stage;
import com.udea.GPX.model.StageResult;
import com.udea.GPX.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IStageResultRepository extends JpaRepository<StageResult, Long> {

    List<StageResult> findByVehicleAndStage_Event(Vehicle vehicle, Event event);

    List<StageResult> findByVehicleAndStage(Vehicle vehicle, Stage stage);

    List<StageResult> findByStage(Stage stage);

}
