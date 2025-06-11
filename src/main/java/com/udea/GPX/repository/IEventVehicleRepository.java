package com.udea.gpx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.gpx.model.EventVehicle;

import java.util.List;

public interface IEventVehicleRepository extends JpaRepository<EventVehicle, Long> {
    List<EventVehicle> findByEventId(Long eventId);
}
