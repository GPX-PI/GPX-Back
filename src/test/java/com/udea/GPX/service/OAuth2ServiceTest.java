package com.udea.gpx.service;

import com.udea.gpx.util.TestDataBuilder;
import com.udea.gpx.model.User;
import com.udea.gpx.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2ServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private OAuth2User oauth2User;

    @InjectMocks
    private OAuth2Service oauth2Service;

    @BeforeEach
    void setUp() {
        // No need to instantiate since TestDataBuilder has static methods
    }

    // processOAuth2User Tests - Existing User Scenarios

    @Test
    void processOAuth2User_WithExistingUserByEmail_ShouldReturnExistingUser() {
        // Given
        String email = "test@example.com";
        String googleId = "google123";
        String name = "Test User";
        String picture = "http://example.com/picture.jpg";

        User existingUser = TestDataBuilder.buildUser(1L, false);
        existingUser.setEmail(email);
        existingUser.setGoogleId("existing123");

        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("sub")).thenReturn(googleId);
        when(oauth2User.getAttribute("name")).thenReturn(name);
        when(oauth2User.getAttribute("picture")).thenReturn(picture);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // When
        User result = oauth2Service.processOAuth2User(oauth2User);

        // Then
        assertEquals(existingUser, result);
        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).save(any());
    }

    @Test
    void processOAuth2User_WithExistingUserWithoutGoogleId_ShouldUpdateUserWithGoogleInfo() {
        // Given
        String email = "test@example.com";
        String googleId = "google123";
        String name = "Test User";
        String picture = "http://example.com/picture.jpg";

        User existingUser = TestDataBuilder.buildUser(1L, false);
        existingUser.setEmail(email);
        existingUser.setGoogleId(null);
        existingUser.setPicture(null);

        User updatedUser = TestDataBuilder.buildUser(1L, false);
        updatedUser.setEmail(email);
        updatedUser.setGoogleId(googleId);
        updatedUser.setAuthProvider("GOOGLE");
        updatedUser.setPicture(picture);

        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("sub")).thenReturn(googleId);
        when(oauth2User.getAttribute("name")).thenReturn(name);
        when(oauth2User.getAttribute("picture")).thenReturn(picture);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = oauth2Service.processOAuth2User(oauth2User);

        // Then
        assertEquals(updatedUser, result);
        verify(userRepository).findByEmail(email);
        verify(userRepository).save(argThat(user -> googleId.equals(user.getGoogleId()) &&
                "GOOGLE".equals(user.getAuthProvider()) &&
                picture.equals(user.getPicture())));
    }

    @Test
    void processOAuth2User_WithExistingUserWithoutPicture_ShouldUpdatePicture() {
        // Given
        String email = "test@example.com";
        String googleId = "google123";
        String name = "Test User";
        String picture = "http://example.com/picture.jpg";

        User existingUser = TestDataBuilder.buildUser(1L, false);
        existingUser.setEmail(email);
        existingUser.setGoogleId(null);
        existingUser.setPicture("");

        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("sub")).thenReturn(googleId);
        when(oauth2User.getAttribute("name")).thenReturn(name);
        when(oauth2User.getAttribute("picture")).thenReturn(picture);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        oauth2Service.processOAuth2User(oauth2User);

        // Then
        verify(userRepository).save(argThat(user -> picture.equals(user.getPicture())));
    }

    // processOAuth2User Tests - New User Scenarios

    @Test
    void processOAuth2User_WithNewUser_ShouldCreateUserWithBasicInfo() {
        // Given
        String email = "newuser@example.com";
        String googleId = "google123";
        String name = "John Doe";
        String picture = "http://example.com/picture.jpg";

        User newUser = TestDataBuilder.buildUser(1L, false);
        newUser.setEmail(email);
        newUser.setGoogleId(googleId);

        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("sub")).thenReturn(googleId);
        when(oauth2User.getAttribute("name")).thenReturn(name);
        when(oauth2User.getAttribute("picture")).thenReturn(picture);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(newUser); // When
        User result = oauth2Service.processOAuth2User(oauth2User);

        // Then
        assertEquals(newUser, result);
        verify(userRepository).save(argThat(user -> email.equals(user.getEmail()) &&
                googleId.equals(user.getGoogleId()) &&
                "GOOGLE".equals(user.getAuthProvider()) &&
                "John".equals(user.getFirstName()) &&
                "Doe".equals(user.getLastName()) &&
                picture.equals(user.getPicture()) &&
                user.getPassword() == null &&
                !user.isAdmin()));
    }

    @Test
    void processOAuth2User_WithNewUserAndSingleName_ShouldSetOnlyFirstName() {
        // Given
        String email = "newuser@example.com";
        String googleId = "google123";
        String name = "John";
        String picture = "http://example.com/picture.jpg";

        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("sub")).thenReturn(googleId);
        when(oauth2User.getAttribute("name")).thenReturn(name);
        when(oauth2User.getAttribute("picture")).thenReturn(picture);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // When
        oauth2Service.processOAuth2User(oauth2User);

        // Then
        verify(userRepository).save(argThat(user -> "John".equals(user.getFirstName()) &&
                user.getLastName() == null));
    }

    @Test
    void processOAuth2User_WithNewUserAndLongName_ShouldTruncateNames() {
        // Given
        String email = "newuser@example.com";
        String googleId = "google123";
        String longFirstName = "A".repeat(60); // 60 characters
        String longLastName = "B".repeat(60); // 60 characters
        String name = longFirstName + " " + longLastName;
        String picture = "http://example.com/picture.jpg";

        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("sub")).thenReturn(googleId);
        when(oauth2User.getAttribute("name")).thenReturn(name);
        when(oauth2User.getAttribute("picture")).thenReturn(picture);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // When
        oauth2Service.processOAuth2User(oauth2User);

        // Then
        verify(userRepository).save(argThat(user -> user.getFirstName().length() == 50 &&
                user.getLastName().length() == 50 &&
                user.getFirstName().equals("A".repeat(50)) &&
                user.getLastName().equals("B".repeat(50))));
    }

    @Test
    void processOAuth2User_WithNewUserAndNoName_ShouldUseDefaultNames() {
        // Given
        String email = "newuser@example.com";
        String googleId = "google123";
        String picture = "http://example.com/picture.jpg";

        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("sub")).thenReturn(googleId);
        when(oauth2User.getAttribute("name")).thenReturn(null);
        when(oauth2User.getAttribute("picture")).thenReturn(picture);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // When
        oauth2Service.processOAuth2User(oauth2User);

        // Then
        verify(userRepository).save(argThat(user -> "Usuario".equals(user.getFirstName()) &&
                "Google".equals(user.getLastName())));
    }

    @Test
    void processOAuth2User_WithNewUserAndEmptyName_ShouldUseDefaultNames() {
        // Given
        String email = "newuser@example.com";
        String googleId = "google123";
        String picture = "http://example.com/picture.jpg";

        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("sub")).thenReturn(googleId);
        when(oauth2User.getAttribute("name")).thenReturn("   "); // Whitespace only
        when(oauth2User.getAttribute("picture")).thenReturn(picture);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // When
        oauth2Service.processOAuth2User(oauth2User);

        // Then
        verify(userRepository).save(argThat(user -> "Usuario".equals(user.getFirstName()) &&
                "Google".equals(user.getLastName())));
    }

    @Test
    void processOAuth2User_WithNullPicture_ShouldSetEmptyPicture() {
        // Given
        String email = "newuser@example.com";
        String googleId = "google123";
        String name = "John Doe";

        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("sub")).thenReturn(googleId);
        when(oauth2User.getAttribute("name")).thenReturn(name);
        when(oauth2User.getAttribute("picture")).thenReturn(null);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // When
        oauth2Service.processOAuth2User(oauth2User);

        // Then
        verify(userRepository).save(argThat(user -> "".equals(user.getPicture())));
    }

    // processOAuth2User Tests - GoogleId Fallback @Test
    void processOAuth2User_WithNullSubAttribute_ShouldUseIdAttribute() {
        // Given
        String email = "newuser@example.com";
        String googleId = "google123";
        String name = "John Doe";
        String picture = "http://example.com/picture.jpg";

        when(oauth2User.getAttribute("email")).thenReturn(email);
        when(oauth2User.getAttribute("sub")).thenReturn(null);
        when(oauth2User.getAttribute("id")).thenReturn(googleId);
        when(oauth2User.getAttribute("name")).thenReturn(name);
        when(oauth2User.getAttribute("picture")).thenReturn(picture);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());

        // When
        oauth2Service.processOAuth2User(oauth2User);

        // Then
        verify(userRepository).save(argThat(user -> googleId.equals(user.getGoogleId())));
    }

    // findUserByGoogleId Tests

    @Test
    void findUserByGoogleId_WithExistingUser_ShouldReturnUser() {
        // Given
        String googleId = "google123";
        User user = TestDataBuilder.buildUser(1L, false);
        user.setGoogleId(googleId);

        when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.of(user));

        // When
        User result = oauth2Service.findUserByGoogleId(googleId);

        // Then
        assertEquals(user, result);
        verify(userRepository).findByGoogleId(googleId);
    }

    @Test
    void findUserByGoogleId_WithNonExistentUser_ShouldReturnNull() {
        // Given
        String googleId = "nonexistent";

        when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.empty());

        // When
        User result = oauth2Service.findUserByGoogleId(googleId);

        // Then
        assertNull(result);
        verify(userRepository).findByGoogleId(googleId);
    }

    // isProfileComplete Tests

    @Test
    void isProfileComplete_WithCompleteProfile_ShouldReturnTrue() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification("12345678");
        user.setPhone("555-1234");
        user.setRole("DRIVER");

        // When
        boolean result = oauth2Service.isProfileComplete(user);

        // Then
        assertTrue(result);
    }

    @Test
    void isProfileComplete_WithMissingIdentification_ShouldReturnFalse() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification(null);
        user.setPhone("555-1234");
        user.setRole("DRIVER");

        // When
        boolean result = oauth2Service.isProfileComplete(user);

        // Then
        assertFalse(result);
    }

    @Test
    void isProfileComplete_WithEmptyIdentification_ShouldReturnFalse() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification("   ");
        user.setPhone("555-1234");
        user.setRole("DRIVER");

        // When
        boolean result = oauth2Service.isProfileComplete(user);

        // Then
        assertFalse(result);
    }

    @Test
    void isProfileComplete_WithMissingPhone_ShouldReturnFalse() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification("12345678");
        user.setPhone(null);
        user.setRole("DRIVER");

        // When
        boolean result = oauth2Service.isProfileComplete(user);

        // Then
        assertFalse(result);
    }

    @Test
    void isProfileComplete_WithEmptyPhone_ShouldReturnFalse() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification("12345678");
        user.setPhone("");
        user.setRole("DRIVER");

        // When
        boolean result = oauth2Service.isProfileComplete(user);

        // Then
        assertFalse(result);
    }

    @Test
    void isProfileComplete_WithMissingRole_ShouldReturnFalse() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification("12345678");
        user.setPhone("555-1234");
        user.setRole(null);

        // When
        boolean result = oauth2Service.isProfileComplete(user);

        // Then
        assertFalse(result);
    }

    @Test
    void isProfileComplete_WithEmptyRole_ShouldReturnFalse() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification("12345678");
        user.setPhone("555-1234");
        user.setRole("   ");

        // When
        boolean result = oauth2Service.isProfileComplete(user);

        // Then
        assertFalse(result);
    }

    @Test
    void isProfileComplete_WithAllFieldsMissing_ShouldReturnFalse() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification(null);
        user.setPhone(null);
        user.setRole(null);

        // When
        boolean result = oauth2Service.isProfileComplete(user);

        // Then
        assertFalse(result);
    }

    // getMissingProfileFields Tests

    @Test
    void getMissingProfileFields_WithCompleteProfile_ShouldReturnEmptyArray() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification("12345678");
        user.setPhone("555-1234");
        user.setRole("DRIVER");

        // When
        String[] missingFields = oauth2Service.getMissingProfileFields(user);

        // Then
        assertEquals(0, missingFields.length);
    }

    @Test
    void getMissingProfileFields_WithMissingIdentification_ShouldReturnIdentification() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification(null);
        user.setPhone("555-1234");
        user.setRole("DRIVER");

        // When
        String[] missingFields = oauth2Service.getMissingProfileFields(user);

        // Then
        assertEquals(1, missingFields.length);
        assertEquals("identification", missingFields[0]);
    }

    @Test
    void getMissingProfileFields_WithMissingPhone_ShouldReturnPhone() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification("12345678");
        user.setPhone("");
        user.setRole("DRIVER");

        // When
        String[] missingFields = oauth2Service.getMissingProfileFields(user);

        // Then
        assertEquals(1, missingFields.length);
        assertEquals("phone", missingFields[0]);
    }

    @Test
    void getMissingProfileFields_WithMissingRole_ShouldReturnRole() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification("12345678");
        user.setPhone("555-1234");
        user.setRole("   ");

        // When
        String[] missingFields = oauth2Service.getMissingProfileFields(user);

        // Then
        assertEquals(1, missingFields.length);
        assertEquals("role", missingFields[0]);
    }

    @Test
    void getMissingProfileFields_WithMultipleMissingFields_ShouldReturnAllMissing() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification(null);
        user.setPhone("");
        user.setRole(null);

        // When
        String[] missingFields = oauth2Service.getMissingProfileFields(user);

        // Then
        assertEquals(3, missingFields.length);
        assertTrue(java.util.Arrays.asList(missingFields).contains("identification"));
        assertTrue(java.util.Arrays.asList(missingFields).contains("phone"));
        assertTrue(java.util.Arrays.asList(missingFields).contains("role"));
    }

    @Test
    void getMissingProfileFields_WithTwoMissingFields_ShouldReturnBothMissing() {
        // Given
        User user = TestDataBuilder.buildUser(1L, false);
        user.setIdentification("12345678");
        user.setPhone(null);
        user.setRole("");

        // When
        String[] missingFields = oauth2Service.getMissingProfileFields(user);

        // Then
        assertEquals(2, missingFields.length);
        assertTrue(java.util.Arrays.asList(missingFields).contains("phone"));
        assertTrue(java.util.Arrays.asList(missingFields).contains("role"));
    }
}
