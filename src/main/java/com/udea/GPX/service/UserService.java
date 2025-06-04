package com.udea.GPX.service;

import com.udea.GPX.model.User;
import com.udea.GPX.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private IUserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setIdentification(updatedUser.getIdentification());
        user.setPhone(updatedUser.getPhone());
        user.setAdmin(updatedUser.isAdmin());
        user.setEmail(updatedUser.getEmail());
        user.setRole(updatedUser.getRole());
        user.setBirthdate(updatedUser.getBirthdate());
        user.setTypeOfId(updatedUser.getTypeOfId());
        user.setTeamName(updatedUser.getTeamName());
        user.setEps(updatedUser.getEps());
        user.setRh(updatedUser.getRh());
        user.setEmergencyPhone(updatedUser.getEmergencyPhone());
        user.setAlergies(updatedUser.getAlergies());
        user.setWikiloc(updatedUser.getWikiloc());
        user.setInsurance(updatedUser.getInsurance());
        user.setTerrapirata(updatedUser.getTerrapirata());
        user.setInstagram(updatedUser.getInstagram());
        user.setFacebook(updatedUser.getFacebook());
        user.setPicture(updatedUser.getPicture());

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public boolean checkPassword(User user, String rawPassword) {
        // Aquí puedes usar un encoder, por ahora simple comparación
        return user.getPassword().equals(rawPassword);
    }

}
