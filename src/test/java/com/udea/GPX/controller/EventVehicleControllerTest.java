package com.udea.gpx.controller;

import com.udea.gpx.dto.ParticipantDTO;
import com.udea.gpx.model.EventVehicle;
import com.udea.gpx.model.User;
import com.udea.gpx.model.Vehicle;
import com.udea.gpx.service.EventVehicleService;
import com.udea.gpx.service.UserService;
import com.udea.gpx.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventVehicleController Tests")
class EventVehicleControllerTest {
    @Mock
    private EventVehicleService eventVehicleService;
    @Mock
    private UserService userService;
    @Mock
    private VehicleService vehicleService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private OAuth2User oAuth2User;
    @InjectMocks
    private EventVehicleController controller;

    @BeforeEach
    void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Controller should be instantiated")
    void shouldInstantiateController() {
        assertThat(controller).isNotNull();
    }

    @Test
    @DisplayName("getAllEventVehicles - Unauthorized user")
    void getAllEventVehicles_unauthorized() {
        when(securityContext.getAuthentication()).thenReturn(null);
        ResponseEntity<List<EventVehicle>> response = controller.getAllEventVehicles();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("getAllEventVehicles - Forbidden for non-admin")
    void getAllEventVehicles_forbidden() {
        User user = mock(User.class);
        when(user.isAdmin()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        ResponseEntity<List<EventVehicle>> response = controller.getAllEventVehicles();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("getAllEventVehicles - Success for admin")
    void getAllEventVehicles_success() {
        User user = mock(User.class);
        when(user.isAdmin()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        List<EventVehicle> list = Collections.singletonList(new EventVehicle());
        when(eventVehicleService.getAllEventVehicles()).thenReturn(list);
        ResponseEntity<List<EventVehicle>> response = controller.getAllEventVehicles();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    @DisplayName("getEventVehicleById - Unauthorized user")
    void getEventVehicleById_unauthorized() {
        when(securityContext.getAuthentication()).thenReturn(null);
        ResponseEntity<EventVehicle> response = controller.getEventVehicleById(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("getEventVehicleById - Not found")
    void getEventVehicleById_notFound() {
        User user = mock(User.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        when(eventVehicleService.getEventVehicleById(anyLong())).thenReturn(Optional.empty());
        ResponseEntity<EventVehicle> response = controller.getEventVehicleById(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("getEventVehicleById - Forbidden for non-owner")
    void getEventVehicleById_forbidden() {
        User user = mock(User.class);
        when(user.isAdmin()).thenReturn(false);
        when(user.getId()).thenReturn(2L);
        Vehicle vehicle = mock(Vehicle.class);
        User owner = mock(User.class);
        when(owner.getId()).thenReturn(1L);
        when(vehicle.getUser()).thenReturn(owner);
        EventVehicle ev = mock(EventVehicle.class);
        when(ev.getVehicleId()).thenReturn(vehicle);
        when(eventVehicleService.getEventVehicleById(anyLong())).thenReturn(Optional.of(ev));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        ResponseEntity<EventVehicle> response = controller.getEventVehicleById(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("getEventVehicleById - Success for admin")
    void getEventVehicleById_success_admin() {
        User user = mock(User.class);
        when(user.isAdmin()).thenReturn(true);
        EventVehicle ev = new EventVehicle();
        when(eventVehicleService.getEventVehicleById(anyLong())).thenReturn(Optional.of(ev));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        ResponseEntity<EventVehicle> response = controller.getEventVehicleById(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(ev);
    }

    @Test
    @DisplayName("getVehiclesByEventId - Success")
    void getVehiclesByEventId_success() {
        List<EventVehicle> list = Collections.singletonList(new EventVehicle());
        when(eventVehicleService.getVehiclesByEventId(anyLong())).thenReturn(list);
        ResponseEntity<List<EventVehicle>> response = controller.getVehiclesByEventId(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(list);
    }

    @Test
    @DisplayName("getEventParticipants - Success")
    void getEventParticipants_success() {
        EventVehicle ev = mock(EventVehicle.class);
        Vehicle vehicle = mock(Vehicle.class);
        User user = mock(User.class);
        when(ev.getVehicleId()).thenReturn(vehicle);
        when(vehicle.getUser()).thenReturn(user);
        when(user.getFirstName()).thenReturn("John");
        when(user.getLastName()).thenReturn("Doe");
        when(user.getId()).thenReturn(1L);
        when(user.getPicture()).thenReturn("pic");
        when(user.getTeamName()).thenReturn("team");
        when(vehicle.getId()).thenReturn(2L);
        when(vehicle.getName()).thenReturn("car");
        when(vehicle.getPlates()).thenReturn("ABC");
        when(vehicle.getSoat()).thenReturn("soat");
        when(vehicle.getCategory()).thenReturn(null);
        when(eventVehicleService.getVehiclesByEventId(anyLong())).thenReturn(List.of(ev));
        ResponseEntity<List<ParticipantDTO>> response = controller.getEventParticipants(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("getEventParticipants - Exception")
    void getEventParticipants_exception() {
        when(eventVehicleService.getVehiclesByEventId(anyLong())).thenThrow(new RuntimeException());
        ResponseEntity<List<ParticipantDTO>> response = controller.getEventParticipants(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Add more tests for endpoints and edge cases as needed
}
