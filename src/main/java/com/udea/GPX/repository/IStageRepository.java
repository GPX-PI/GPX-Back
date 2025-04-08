package com.udea.GPX.repository;

import com.udea.GPX.model.Event;
import com.udea.GPX.model.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IStageRepository extends JpaRepository<Stage, Long> {
    Optional<Stage> findByEventAndOrderNumber(Event event, int i);

    List<Stage> findByEventAndOrderNumberBetween(Event event, int stageStart, int stageEnd);
}
