package com.udea.gpx.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "stage_result", indexes = {
        @Index(name = "idx_stage_result_stage_id", columnList = "fk_stage_id"),
        @Index(name = "idx_stage_result_vehicle_id", columnList = "fk_vehicle_id"),
        @Index(name = "idx_stage_result_timestamp", columnList = "timestamp"),
        @Index(name = "idx_stage_result_stage_vehicle", columnList = "fk_stage_id, fk_vehicle_id"),
        @Index(name = "idx_stage_result_vehicle_stage_order", columnList = "fk_vehicle_id, fk_stage_id"),
        @Index(name = "idx_stage_result_elapsed_time", columnList = "elapsed_time_seconds")
})
public class StageResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(name = "penalty_waypoint")
    private Duration penaltyWaypoint;

    @Column(name = "penalty_speed")
    private Duration penaltySpeed;

    @Column(name = "discount_claim")
    private Duration discountClaim;

    @Column(name = "elapsed_time_seconds")
    private Integer elapsedTimeSeconds;

    @ManyToOne
    @JoinColumn(name = "fk_stage_id", nullable = false)
    private Stage stage;

    @ManyToOne
    @JoinColumn(name = "fk_vehicle_id", nullable = false)
    private Vehicle vehicle;

    public StageResult(Long id, LocalDateTime timestamp, double latitude, double longitude, Duration penaltyWaypoint,
            Duration penaltySpeed, Duration discountClaim, Integer elapsedTimeSeconds, Stage stage, Vehicle vehicle) {
        this.id = id;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.penaltyWaypoint = penaltyWaypoint;
        this.penaltySpeed = penaltySpeed;
        this.discountClaim = discountClaim;
        this.elapsedTimeSeconds = elapsedTimeSeconds;
        this.stage = stage;
        this.vehicle = vehicle;
    }

    public StageResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Duration getPenaltyWaypoint() {
        return penaltyWaypoint;
    }

    public void setPenaltyWaypoint(Duration penaltyWaypoint) {
        this.penaltyWaypoint = penaltyWaypoint;
    }

    public Duration getPenaltySpeed() {
        return penaltySpeed;
    }

    public void setPenaltySpeed(Duration penaltySpeed) {
        this.penaltySpeed = penaltySpeed;
    }

    public Duration getDiscountClaim() {
        return discountClaim;
    }

    public void setDiscountClaim(Duration discountClaim) {
        this.discountClaim = discountClaim;
    }

    public Integer getElapsedTimeSeconds() {
        return elapsedTimeSeconds;
    }

    public void setElapsedTimeSeconds(Integer elapsedTimeSeconds) {
        this.elapsedTimeSeconds = elapsedTimeSeconds;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
