package com.udea.GPX.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class VehicleStageTimeDTO {
    @NotNull(message = "ID del vehículo es requerido")
    @Positive(message = "ID del vehículo debe ser positivo")
    private Long vehicleId;

    @NotNull(message = "ID de la etapa es requerido")
    @Positive(message = "ID de la etapa debe ser positivo")
    private Long stageId;

    @NotNull(message = "Orden de la etapa es requerido")
    @Positive(message = "Orden de la etapa debe ser positivo")
    private Integer stageOrder;

    @NotNull(message = "Tiempo transcurrido es requerido")
    @PositiveOrZero(message = "Tiempo transcurrido debe ser mayor o igual a cero")
    private Integer elapsedTimeSeconds;

    public VehicleStageTimeDTO(Long vehicleId, Long stageId, Integer stageOrder, Integer elapsedTimeSeconds) {
        this.vehicleId = vehicleId;
        this.stageId = stageId;
        this.stageOrder = stageOrder;
        this.elapsedTimeSeconds = elapsedTimeSeconds;
    }

    // Getters y Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public Integer getStageOrder() {
        return stageOrder;
    }

    public void setStageOrder(Integer stageOrder) {
        this.stageOrder = stageOrder;
    }

    public Integer getElapsedTimeSeconds() {
        return elapsedTimeSeconds;
    }

    public void setElapsedTimeSeconds(Integer elapsedTimeSeconds) {
        this.elapsedTimeSeconds = elapsedTimeSeconds;
    }
}