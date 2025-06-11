package com.udea.gpx.service;

import org.springframework.stereotype.Service;

import com.udea.gpx.model.Vehicle;
import com.udea.gpx.repository.IVehicleRepository;
import com.udea.gpx.constants.AppConstants;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {
    private final IVehicleRepository vehicleRepository;

    // Constructor injection (no @Autowired needed)
    public VehicleService(IVehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public Vehicle updateVehicle(Long id, Vehicle updatedVehicle) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.Messages.VEHICULO_NO_ENCONTRADO));

        vehicle.setName(updatedVehicle.getName());
        vehicle.setSoat(updatedVehicle.getSoat());
        vehicle.setPlates(updatedVehicle.getPlates());
        vehicle.setCategory(updatedVehicle.getCategory());
        vehicle.setUser(updatedVehicle.getUser());

        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

}
