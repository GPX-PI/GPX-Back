package com.udea.GPX;

        import com.udea.GPX.controller.UserController;
        import com.udea.GPX.model.User;
        import com.udea.GPX.service.UserService;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;
        import org.mockito.InjectMocks;
        import org.mockito.Mock;
        import org.mockito.MockitoAnnotations;
        import org.springframework.boot.test.context.SpringBootTest;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;

        import java.time.LocalDate;
        import java.util.Arrays;
        import java.util.List;
        import java.util.Objects;
        import java.util.Optional;

        import static org.junit.jupiter.api.Assertions.assertEquals;
        import static org.junit.jupiter.api.Assertions.assertNull;
        import static org.mockito.Mockito.when;

        @SpringBootTest
        public class UserControllerTests {

            @Mock
            private UserService userService;

            @InjectMocks
            private UserController userController;

            private User buildUser(Long id, String firstName, boolean admin) {
                return new User(
                        id,
                        firstName,
                        "Apellido",
                        "123456789",
                        "3101234567",
                        admin,
                        "correo@ejemplo.com",
                        "piloto",
                        LocalDate.of(1990, 1, 1),
                        "CC",
                        "EquipoX",
                        "EPSX",
                        "O+",
                        "3000000000",
                        "Ninguna",
                        "wikilocUser",
                        "SeguroX",
                        "terrapirataUser",
                        "instaUser",
                        "fbUser"
                );
            }

            @BeforeEach
            void setUp() {
                MockitoAnnotations.openMocks(this);
            }

            @Test
            void getAllUsers_shouldReturnOK() {
                // Arrange
                List<User> users = Arrays.asList(
                        buildUser(1L, "Juan", false),
                        buildUser(2L, "María", true)
                );
                when(userService.getAllUsers()).thenReturn(users);

                // Act
                ResponseEntity<List<User>> response = userController.getAllUsers();

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(2, Objects.requireNonNull(response.getBody()).size());
            }

            @Test
            void getUserById_whenUserExists_shouldReturnOK() {
                // Arrange
                Long userId = 1L;
                User user = buildUser(userId, "Juan", false);
                when(userService.getUserById(userId)).thenReturn(Optional.of(user));

                // Act
                ResponseEntity<User> response = userController.getUserById(userId);

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("Juan", Objects.requireNonNull(response.getBody()).getFirstName());
                assertEquals("correo@ejemplo.com", response.getBody().getEmail());
            }

            @Test
            void getUserById_whenUserNotExists_shouldReturnNotFound() {
                // Arrange
                Long userId = 1L;
                when(userService.getUserById(userId)).thenReturn(Optional.empty());

                // Act
                ResponseEntity<User> response = userController.getUserById(userId);

                // Assert
                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertNull(response.getBody());
            }

            @Test
            void getAdminUsers_shouldReturnOK() {
                // Arrange
                List<User> users = Arrays.asList(
                        buildUser(1L, "Juan", false),
                        buildUser(2L, "María", true)
                );
                when(userService.getAllUsers()).thenReturn(users);

                // Act
                ResponseEntity<List<User>> response = userController.getAdminUsers();

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(1, Objects.requireNonNull(response.getBody()).size());
                assertEquals("María", response.getBody().get(0).getFirstName());
            }

            @Test
            void updateUser_shouldUpdateAllFields() {
                // Arrange
                Long userId = 1L;
                User originalUser = buildUser(userId, "Juan", false);
                User updatedUser = new User(
                        userId,
                        "Juanito",
                        "Pérez",
                        "123456789",
                        "3101234567",
                        true,
                        "nuevo@correo.com",
                        "juez",
                        LocalDate.of(1985, 5, 5),
                        "CE",
                        "EquipoY",
                        "EPSY",
                        "A-",
                        "3111111111",
                        "Polen",
                        "wikilocNuevo",
                        "SeguroY",
                        "terrapirataNuevo",
                        "instaNuevo",
                        "fbNuevo"
                );
                when(userService.updateUser(userId, updatedUser)).thenReturn(updatedUser);

                // Act
                ResponseEntity<User> response = userController.updateUser(userId, updatedUser);

                // Assert
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("Juanito", Objects.requireNonNull(response.getBody()).getFirstName());
                assertEquals("juez", response.getBody().getRole());
                assertEquals("fbNuevo", response.getBody().getFacebook());
            }
        }