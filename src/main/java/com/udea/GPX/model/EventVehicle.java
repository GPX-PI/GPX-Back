package com.udea.gpx.model;

import jakarta.persistence.*;

@Entity
@Table(name = "event_vehicle")
public class EventVehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "fk_vehicle_id", nullable = false)
    private Vehicle vehicleId;

    public EventVehicle(Long id, Event event, Vehicle vehicleId) {
        this.id = id;
        this.event = event;
        this.vehicleId = vehicleId;
    }

    public EventVehicle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Vehicle getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Vehicle vehicleId) {
        this.vehicleId = vehicleId;
    }
}
