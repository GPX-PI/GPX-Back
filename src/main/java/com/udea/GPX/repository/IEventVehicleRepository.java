package com.udea.GPX.repository;

import com.udea.GPX.model.EventVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IEventVehicleRepository extends JpaRepository<EventVehicle, Long> {
    List<EventVehicle> findByEventId(Long eventId);
}
