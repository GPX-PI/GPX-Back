package com.udea.gpx.model;

import jakarta.persistence.*;

@Entity
@Table(name = "event_category")
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "fk_category_id", nullable = false)
    private Category category;

    public EventCategory(Long id, Event event, Category category) {
        this.id = id;
        this.event = event;
        this.category = category;
    }

    public EventCategory() {
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
