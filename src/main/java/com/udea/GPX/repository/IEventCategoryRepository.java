package com.udea.GPX.repository;

import com.udea.GPX.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEventCategoryRepository extends JpaRepository<EventCategory, Long> {
}
