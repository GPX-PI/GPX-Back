package com.udea.gpx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.gpx.model.Event;

import java.time.LocalDate;
import java.util.List;

public interface IEventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEndDateAfterOrEndDateEquals(LocalDate date, LocalDate date2);

    List<Event> findByEndDateBefore(LocalDate date);
}
