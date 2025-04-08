package com.udea.GPX.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "stage_result")
public class StageResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private int latitude;

    @Column(nullable = false)
    private int longitude;

    @Column(name = "penalty_waypoint")
    private Duration penaltyWaypoint;

    @Column(name = "penalty_speed")
    private Duration penaltySpeed;

    @Column(name = "discount_claim")
    private Duration discountClaim;

    @ManyToOne
    @JoinColumn(name = "fk_stage_id", nullable = false)
    private Stage stage;

    @ManyToOne
    @JoinColumn(name = "fk_vehicle_id", nullable = false)
    private Vehicle vehicle;

    public StageResult(Long id, LocalDateTime timestamp, int latitude, int longitude, Duration penaltyWaypoint, Duration penaltySpeed, Duration discountClaim, Stage stage, Vehicle vehicle) {
        this.id = id;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.penaltyWaypoint = penaltyWaypoint;
        this.penaltySpeed = penaltySpeed;
        this.discountClaim = discountClaim;
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

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
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
