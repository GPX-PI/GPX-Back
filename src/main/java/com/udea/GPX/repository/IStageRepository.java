package com.udea.gpx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.gpx.model.Event;
import com.udea.gpx.model.Stage;

import java.util.List;
import java.util.Optional;

public interface IStageRepository extends JpaRepository<Stage, Long> {
    Optional<Stage> findByEventAndOrderNumber(Event event, int i);

    List<Stage> findByEventAndOrderNumberBetween(Event event, int stageStart, int stageEnd);
}
