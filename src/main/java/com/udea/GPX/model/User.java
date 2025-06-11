package com.udea.gpx.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = true, length = 50)
    private String lastName;

    @Column(nullable = true, length = 15)
    private String identification;

    @Column(nullable = true, length = 15)
    private String phone;

    @Column(nullable = false)
    private boolean admin;

    @Column(length = 100, nullable = true)
    private String email;

    @Column(length = 20, nullable = true)
    private String role;

    @Column(name = "birthdate", nullable = true)
    private LocalDate birthdate;

    @Column(name = "type_of_id", length = 20, nullable = true)
    private String typeOfId;

    @Column(name = "team_name", length = 50, nullable = true)
    private String teamName;

    @Column(length = 50, nullable = true)
    private String eps;

    @Column(length = 5, nullable = true)
    private String rh;

    @Column(name = "emergency_phone", length = 15, nullable = true)
    private String emergencyPhone;

    @Column(length = 255, nullable = true)
    private String alergies;

    @Column(length = 100, nullable = true)
    private String wikiloc;

    @Column(length = 100, nullable = true)
    private String insurance;

    @Column(length = 100, nullable = true)
    private String terrapirata;

    @Column(length = 100, nullable = true)
    private String instagram;

    @Column(length = 100, nullable = true)
    private String facebook;

    @Column(name = "picture", length = 255, nullable = true)
    private String picture = "";

    @Column(nullable = true)
    @JsonIgnore
    private String password;

    // Campos para OAuth2
    @Column(name = "google_id", length = 255, nullable = true)
    private String googleId;

    @Column(name = "auth_provider", length = 50, nullable = true)
    private String authProvider = "LOCAL"; // LOCAL, GOOGLE

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
        this.picture = "";
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

}