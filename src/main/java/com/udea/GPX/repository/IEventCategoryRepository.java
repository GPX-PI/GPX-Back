package com.udea.GPX.repository;

import com.udea.GPX.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IEventCategoryRepository extends JpaRepository<EventCategory, Long> {
    List<EventCategory> findByEventId(Long eventId);
}
