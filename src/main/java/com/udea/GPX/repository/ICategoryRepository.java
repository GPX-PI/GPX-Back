package com.udea.gpx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.gpx.model.Category;

public interface ICategoryRepository extends JpaRepository<Category, Long> {
}
