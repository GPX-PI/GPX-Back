package com.udea.GPX.repository;

import com.udea.GPX.model.Category;
import com.udea.GPX.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByCategory(Category category);
}
