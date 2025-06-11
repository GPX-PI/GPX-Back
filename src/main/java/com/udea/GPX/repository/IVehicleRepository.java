package com.udea.gpx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.gpx.model.Category;
import com.udea.gpx.model.Vehicle;

import java.util.List;

public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByCategory(Category category);
}
