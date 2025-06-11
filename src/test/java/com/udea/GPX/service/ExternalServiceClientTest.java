package com.udea.gpx.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.udea.gpx.exception.ConnectivityException;
import com.udea.gpx.exception.OAuth2ServiceException;

import java.util.Map;

/**
 * Tests unitarios para ExternalServiceClient
 * Valida el comportamiento del cliente de servicios externos con Circuit
 * Breaker
 */
@ExtendWith(MockitoExtension.class)
class ExternalServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExternalServiceClient externalServiceClient;

    // Test constants
    private static final String GOOGLE_OAUTH_URL = "https://www.googleapis.com/oauth2/v2/tokeninfo?access_token=invalid";
    private static final String GOOGLE_BASE_URL = "https://www.google.com";
    private static final String TEST_ACCESS_TOKEN = "test_access_token";
    private static final String TEST_PROVIDER_URL = "https://provider.example.com/userinfo";
    private static final String SUB_KEY = "sub";
    private static final String EMAIL_KEY = "email";
    private static final String NAME_KEY = "name";
    private static final String PROVIDER_URL_KEY = "provider_url";
    private static final String FALLBACK_KEY = "fallback";

    @BeforeEach
    void setUp() {
        // Inyectar el mock del RestTemplate usando reflexión
        ReflectionTestUtils.setField(externalServiceClient, "restTemplate", restTemplate);
        reset(restTemplate);
    }

    @Test
    void testCheckGoogleOAuth2HealthSuccess() {
        // Arrange
        ResponseEntity<String> responseEntity = new ResponseEntity<>("response", HttpStatus.BAD_REQUEST);
        when(restTemplate.getForEntity(GOOGLE_OAUTH_URL, String.class))
                .thenReturn(responseEntity);

        // Act & Assert
        assertDoesNotThrow(() -> {
            boolean result = externalServiceClient.checkGoogleOAuth2Health();
            assertTrue(result);
        });
        verify(restTemplate).getForEntity(GOOGLE_OAUTH_URL, String.class);
    }

    @Test
    void testCheckGoogleOAuth2HealthWithRestClientException() {
        // Arrange
        when(restTemplate.getForEntity(GOOGLE_OAUTH_URL, String.class))
                .thenThrow(new ResourceAccessException("Connection timeout"));

        // Act & Assert
        OAuth2ServiceException exception = assertThrows(
                OAuth2ServiceException.class,
                () -> externalServiceClient.checkGoogleOAuth2Health());

        assertTrue(exception.getMessage().contains("Google OAuth2 no disponible"));
        assertNotNull(exception.getCause());
        verify(restTemplate).getForEntity(GOOGLE_OAUTH_URL, String.class);
    }

    @Test
    void testCheckGoogleOAuth2HealthWithGeneralException() {
        // Arrange
        when(restTemplate.getForEntity(GOOGLE_OAUTH_URL, String.class))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        OAuth2ServiceException exception = assertThrows(
                OAuth2ServiceException.class,
                () -> externalServiceClient.checkGoogleOAuth2Health());

        assertTrue(exception.getMessage().contains("Error crítico"));
        assertNotNull(exception.getCause());
        verify(restTemplate).getForEntity(GOOGLE_OAUTH_URL, String.class);
    }

    @Test
    void testFallbackOAuth2Health() {
        // Arrange
        OAuth2ServiceException testException = new OAuth2ServiceException("Test error");

        // Act
        boolean result = externalServiceClient.fallbackOAuth2Health(testException);

        // Assert
        assertFalse(result);
    }

    @Test
    @SuppressWarnings("unchecked") // Suprimir warnings de tipo para ParameterizedTypeReference
    void testGetUserInfoFromProviderSuccess() {
        // Arrange
        Map<String, Object> expectedUserInfo = Map.of(
                SUB_KEY, "12345",
                EMAIL_KEY, "test@example.com",
                NAME_KEY, "Test User");
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(expectedUserInfo, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(TEST_PROVIDER_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        // Act
        Map<String, Object> result = assertDoesNotThrow(
                () -> externalServiceClient.getUserInfoFromProvider(TEST_ACCESS_TOKEN, TEST_PROVIDER_URL));

        // Assert
        assertEquals(expectedUserInfo, result);
        assertEquals("12345", result.get(SUB_KEY));
        assertEquals("test@example.com", result.get(EMAIL_KEY));
        assertEquals("Test User", result.get(NAME_KEY));
        verify(restTemplate).exchange(
                eq(TEST_PROVIDER_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }

    @Test
    @SuppressWarnings("unchecked") // Suprimir warnings de tipo para ParameterizedTypeReference
    void testGetUserInfoFromProviderWithNullResponse() {
        // Arrange
        ResponseEntity<Map<String, Object>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(TEST_PROVIDER_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        // Act
        Map<String, Object> result = assertDoesNotThrow(
                () -> externalServiceClient.getUserInfoFromProvider(TEST_ACCESS_TOKEN, TEST_PROVIDER_URL));

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked") // Suprimir warnings de tipo para ParameterizedTypeReference
    void testGetUserInfoFromProviderWithRestClientException() {
        // Arrange
        when(restTemplate.exchange(
                eq(TEST_PROVIDER_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenThrow(new ResourceAccessException("Connection refused"));

        // Act & Assert
        OAuth2ServiceException exception = assertThrows(
                OAuth2ServiceException.class,
                () -> externalServiceClient.getUserInfoFromProvider(TEST_ACCESS_TOKEN, TEST_PROVIDER_URL));

        assertTrue(exception.getMessage().contains("Error obteniendo datos del usuario desde provider"));
        assertNotNull(exception.getCause());
        verify(restTemplate).exchange(
                eq(TEST_PROVIDER_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class));
    }

    @Test
    @SuppressWarnings("unchecked") // Suprimir warnings de tipo para ParameterizedTypeReference
    void testGetUserInfoFromProviderWithGeneralException() {
        // Arrange
        when(restTemplate.exchange(
                eq(TEST_PROVIDER_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        OAuth2ServiceException exception = assertThrows(
                OAuth2ServiceException.class,
                () -> externalServiceClient.getUserInfoFromProvider(TEST_ACCESS_TOKEN, TEST_PROVIDER_URL));

        assertTrue(exception.getMessage().contains("Error crítico"));
        assertNotNull(exception.getCause());
    }

    @Test
    void testFallbackGetUserInfo() {
        // Arrange
        OAuth2ServiceException testException = new OAuth2ServiceException("Test error");

        // Act
        Map<String, Object> result = externalServiceClient.fallbackGetUserInfo(
                TEST_ACCESS_TOKEN, TEST_PROVIDER_URL, testException);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey(SUB_KEY));
        assertTrue(result.containsKey(EMAIL_KEY));
        assertTrue(result.containsKey(NAME_KEY));
        assertTrue(result.containsKey(PROVIDER_URL_KEY));
        assertTrue(result.containsKey(FALLBACK_KEY));

        assertEquals("unknown@example.com", result.get(EMAIL_KEY));
        assertEquals("Usuario Temporal", result.get(NAME_KEY));
        assertEquals(TEST_PROVIDER_URL, result.get(PROVIDER_URL_KEY));
        assertEquals(Boolean.TRUE, result.get(FALLBACK_KEY));

        String subValue = (String) result.get(SUB_KEY);
        assertTrue(subValue.startsWith("temp_"));
    }

    @Test
    void testCheckInternetConnectivitySuccess() {
        // Arrange
        ResponseEntity<String> responseEntity = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.getForEntity(GOOGLE_BASE_URL, String.class))
                .thenReturn(responseEntity);

        // Act
        boolean result = assertDoesNotThrow(() -> externalServiceClient.checkInternetConnectivity());

        // Assert
        assertTrue(result);
        verify(restTemplate).getForEntity(GOOGLE_BASE_URL, String.class);
    }

    @Test
    void testCheckInternetConnectivityWithNon2xxStatus() {
        // Arrange
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.getForEntity(GOOGLE_BASE_URL, String.class))
                .thenReturn(responseEntity);

        // Act
        boolean result = assertDoesNotThrow(() -> externalServiceClient.checkInternetConnectivity());

        // Assert
        assertFalse(result);
        verify(restTemplate).getForEntity(GOOGLE_BASE_URL, String.class);
    }

    @Test
    void testCheckInternetConnectivityWithRestClientException() {
        // Arrange
        when(restTemplate.getForEntity(GOOGLE_BASE_URL, String.class))
                .thenThrow(new ResourceAccessException("No internet connection"));

        // Act & Assert
        ConnectivityException exception = assertThrows(
                ConnectivityException.class,
                () -> externalServiceClient.checkInternetConnectivity());

        assertTrue(exception.getMessage().contains("Sin conectividad a Internet"));
        assertNotNull(exception.getCause());
        verify(restTemplate).getForEntity(GOOGLE_BASE_URL, String.class);
    }

    @Test
    void testCheckInternetConnectivityWithGeneralException() {
        // Arrange
        when(restTemplate.getForEntity(GOOGLE_BASE_URL, String.class))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        ConnectivityException exception = assertThrows(
                ConnectivityException.class,
                () -> externalServiceClient.checkInternetConnectivity());

        assertTrue(exception.getMessage().contains("Error crítico"));
        assertNotNull(exception.getCause());
    }

    @Test
    void testFallbackInternetCheck() {
        // Arrange
        ConnectivityException testException = new ConnectivityException("Test connectivity error");

        // Act
        boolean result = externalServiceClient.fallbackInternetCheck(testException);

        // Assert
        assertFalse(result);
    }

    @Test
    void testConstructorCreatesRestTemplate() {
        // Act
        ExternalServiceClient newClient = new ExternalServiceClient();

        // Assert
        assertNotNull(newClient);
        // Verificamos que se puede llamar a los métodos sin errores de inicialización
        assertDoesNotThrow(() -> {
            ConnectivityException ex = new ConnectivityException("test");
            newClient.fallbackInternetCheck(ex);
        });
    }
}