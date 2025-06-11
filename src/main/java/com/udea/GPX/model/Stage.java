package com.udea.gpx.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "stage", indexes = {
        @Index(name = "idx_stage_event_id", columnList = "fk_event_id"),
        @Index(name = "idx_stage_order_number", columnList = "order_number"),
        @Index(name = "idx_stage_event_order", columnList = "fk_event_id, order_number"),
        @Index(name = "idx_stage_neutralized", columnList = "is_neutralized"),
        @Index(name = "idx_stage_event_neutralized", columnList = "fk_event_id, is_neutralized")
})
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "order_number", nullable = false)
    private int orderNumber;

    @Column(name = "is_neutralized", nullable = false)
    @JsonProperty("isNeutralized")
    private boolean isNeutralized;

    @ManyToOne
    @JoinColumn(name = "fk_event_id", nullable = false)
    private Event event;

    public Stage(Long id, String name, int orderNumber, boolean isNeutralized, Event event) {
        this.id = id;
        this.name = name;
        this.orderNumber = orderNumber;
        this.isNeutralized = isNeutralized;
        this.event = event;
    }

    public Stage() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public boolean isNeutralized() {
        return isNeutralized;
    }

    public void setNeutralized(boolean neutralized) {
        isNeutralized = neutralized;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
