package com.udea.GPX.dto;

public class VehicleStageTimeDTO {
    private Long vehicleId;
    private Long stageId;
    private Integer stageOrder;
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