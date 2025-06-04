package com.udea.GPX.dto;

import java.util.List;

public class ClasificacionCompletaDTO {
    private Long vehicleId;
    private String vehicleName;
    private String driverName; // Asumiendo que esto viene del vehículo
    private Long categoryId;
    private String categoryName;
    private List<StageTimeCellDTO> stageTimes; // Cambiado a lista de DTOs
    private Integer totalTime; // Tiempo total acumulado
    private String userPicture;
    private String teamName;

    // Constructor
    public ClasificacionCompletaDTO(Long vehicleId, String vehicleName, String driverName,
            Long categoryId, String categoryName,
            List<StageTimeCellDTO> stageTimes, Integer totalTime, String userPicture, String teamName) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.driverName = driverName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.stageTimes = stageTimes;
        this.totalTime = totalTime;
        this.userPicture = userPicture;
        this.teamName = teamName;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<StageTimeCellDTO> getStageTimes() {
        return stageTimes;
    }

    public void setStageTimes(List<StageTimeCellDTO> stageTimes) {
        this.stageTimes = stageTimes;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    // DTO para cada celda de tiempo de etapa
    public static class StageTimeCellDTO {
        private Integer stageOrder;
        private Integer elapsedTimeSeconds;
        private Long stageResultId;
        private Integer penaltyWaypointSeconds;
        private Integer penaltySpeedSeconds;
        private Integer discountClaimSeconds;

        public StageTimeCellDTO(Integer stageOrder, Integer elapsedTimeSeconds, Long stageResultId,
                Integer penaltyWaypointSeconds, Integer penaltySpeedSeconds, Integer discountClaimSeconds) {
            this.stageOrder = stageOrder;
            this.elapsedTimeSeconds = elapsedTimeSeconds;
            this.stageResultId = stageResultId;
            this.penaltyWaypointSeconds = penaltyWaypointSeconds;
            this.penaltySpeedSeconds = penaltySpeedSeconds;
            this.discountClaimSeconds = discountClaimSeconds;
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

        public Long getStageResultId() {
            return stageResultId;
        }

        public void setStageResultId(Long stageResultId) {
            this.stageResultId = stageResultId;
        }

        public Integer getPenaltyWaypointSeconds() {
            return penaltyWaypointSeconds;
        }

        public void setPenaltyWaypointSeconds(Integer penaltyWaypointSeconds) {
            this.penaltyWaypointSeconds = penaltyWaypointSeconds;
        }

        public Integer getPenaltySpeedSeconds() {
            return penaltySpeedSeconds;
        }

        public void setPenaltySpeedSeconds(Integer penaltySpeedSeconds) {
            this.penaltySpeedSeconds = penaltySpeedSeconds;
        }

        public Integer getDiscountClaimSeconds() {
            return discountClaimSeconds;
        }

        public void setDiscountClaimSeconds(Integer discountClaimSeconds) {
            this.discountClaimSeconds = discountClaimSeconds;
        }

        public Integer getAdjustedTimeSeconds() {
            return elapsedTimeSeconds
                    + (penaltyWaypointSeconds != null ? penaltyWaypointSeconds : 0)
                    + (penaltySpeedSeconds != null ? penaltySpeedSeconds : 0)
                    - (discountClaimSeconds != null ? discountClaimSeconds : 0);
        }
    }
}