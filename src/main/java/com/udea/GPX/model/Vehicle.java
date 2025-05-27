package com.udea.GPX.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(name = "soat", length = 100)
    private String soat;

    @Column(name = "plates", length = 10)
    private String plates;

    @ManyToOne
    @JoinColumn(name = "fk_category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    private User user;

    public Vehicle(Long id, String name, String soat, String plates, Category category, User user) {
        this.id = id;
        this.name = name;
        this.soat = soat;
        this.plates = plates;
        this.category = category;
        this.user = user;
    }

    public Vehicle() {

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

    public String getSoat() {
        return soat;
    }

    public void setSoat(String soat) {
        this.soat = soat;
    }

    public String getPlates() {
        return plates;
    }

    public void setPlates(String plates) {
        this.plates = plates;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}