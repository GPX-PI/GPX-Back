package com.udea.gpx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udea.gpx.model.User;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByGoogleId(String googleId);
}
