package com.udea.gpx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.gpx.model.EventCategory;

import java.util.List;

public interface IEventCategoryRepository extends JpaRepository<EventCategory, Long> {
    List<EventCategory> findByEventId(Long eventId);
}
