package com.udea.GPX.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @Column(nullable = false, length = 15)
    private String identification;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false)
    private boolean admin;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String role;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "type_of_id", length = 20)
    private String typeOfId;

    @Column(name = "team_name", length = 50)
    private String teamName;

    @Column(length = 50)
    private String eps;

    @Column(length = 5)
    private String rh;

    @Column(name = "emergency_phone", length = 15)
    private String emergencyPhone;

    @Column(length = 255)
    private String alergies;

    @Column(length = 100)
    private String wikiloc;

    @Column(length = 100)
    private String insurance;

    @Column(length = 100)
    private String terrapirata;

    @Column(length = 100)
    private String instagram;

    @Column(length = 100)
    private String facebook;

    public User() {
    }

    public User(Long id, String firstName, String lastName, String identification, String phone, boolean admin,
                String email, String role, LocalDate birthdate, String typeOfId, String teamName, String eps,
                String rh, String emergencyPhone, String alergies, String wikiloc, String insurance,
                String terrapirata, String instagram, String facebook) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.identification = identification;
        this.phone = phone;
        this.admin = admin;
        this.email = email;
        this.role = role;
        this.birthdate = birthdate;
        this.typeOfId = typeOfId;
        this.teamName = teamName;
        this.eps = eps;
        this.rh = rh;
        this.emergencyPhone = emergencyPhone;
        this.alergies = alergies;
        this.wikiloc = wikiloc;
        this.insurance = insurance;
        this.terrapirata = terrapirata;
        this.instagram = instagram;
        this.facebook = facebook;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getTypeOfId() {
        return typeOfId;
    }

    public void setTypeOfId(String typeOfId) {
        this.typeOfId = typeOfId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public String getRh() {
        return rh;
    }

    public void setRh(String rh) {
        this.rh = rh;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getAlergies() {
        return alergies;
    }

    public void setAlergies(String alergies) {
        this.alergies = alergies;
    }

    public String getWikiloc() {
        return wikiloc;
    }

    public void setWikiloc(String wikiloc) {
        this.wikiloc = wikiloc;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getTerrapirata() {
        return terrapirata;
    }

    public void setTerrapirata(String terrapirata) {
        this.terrapirata = terrapirata;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

}