package com.udea.GPX.repository;

import com.udea.GPX.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IEventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEndDateAfterOrEndDateEquals(LocalDate date, LocalDate date2);

    List<Event> findByEndDateBefore(LocalDate date);
}
