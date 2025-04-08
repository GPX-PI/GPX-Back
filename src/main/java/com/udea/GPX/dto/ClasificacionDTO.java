package com.udea.GPX.dto;

import com.udea.GPX.model.Vehicle;

import java.time.Duration;

public class ClasificacionDTO {
    private final Vehicle vehicle;
    private final Duration tiempoTotal;

    public ClasificacionDTO(Vehicle vehicle, Duration tiempoTotal) {
        this.vehicle = vehicle;
        this.tiempoTotal = tiempoTotal;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Duration getTiempoTotal() {
        return tiempoTotal;
    }
}