package com.udea.GPX.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ExternalServiceClient externalServiceClient;

    @BeforeEach
    void setUp() throws Exception {
        externalServiceClient = new ExternalServiceClient();

        // Inject mock RestTemplate using reflection
        Field restTemplateField = ExternalServiceClient.class.getDeclaredField("restTemplate");
        restTemplateField.setAccessible(true);
        restTemplateField.set(externalServiceClient, restTemplate);
    }

    // checkGoogleOAuth2Health Tests

    @Test
    void checkGoogleOAuth2Health_WithSuccessfulResponse_ShouldReturnTrue() {
        // Given
        ResponseEntity<String> successResponse = new ResponseEntity<>("response", HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(successResponse);

        // When
        boolean result = externalServiceClient.checkGoogleOAuth2Health();

        // Then
        assertTrue(result);
        verify(restTemplate).getForEntity(
                eq("https://www.googleapis.com/oauth2/v2/tokeninfo?access_token=invalid"),
                eq(String.class));
    }

    @Test
    void checkGoogleOAuth2Health_WithErrorResponse_ShouldStillReturnTrue() {
        // Given - Error responses still mean the service is available
        ResponseEntity<String> errorResponse = new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(errorResponse);

        // When
        boolean result = externalServiceClient.checkGoogleOAuth2Health();

        // Then
        assertTrue(result);
    }

    @Test
    void checkGoogleOAuth2Health_WithRestClientException_ShouldThrowRuntimeException() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("Connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> externalServiceClient.checkGoogleOAuth2Health());

        assertEquals("Google OAuth2 no disponible", exception.getMessage());
        assertTrue(exception.getCause() instanceof RestClientException);
    }

    @Test
    void checkGoogleOAuth2Health_WithUnexpectedException_ShouldThrowRuntimeException() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new IllegalArgumentException("Unexpected error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> externalServiceClient.checkGoogleOAuth2Health());

        assertEquals("Error verificando OAuth2", exception.getMessage());
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }

    // fallbackOAuth2Health Tests

    @Test
    void fallbackOAuth2Health_ShouldReturnFalse() {
        // Given
        Exception testException = new RuntimeException("Test error");

        // When
        boolean result = externalServiceClient.fallbackOAuth2Health(testException);

        // Then
        assertFalse(result);
    }

    // getUserInfoFromProvider Tests

    @Test
    void getUserInfoFromProvider_WithValidToken_ShouldReturnUserInfo() {
        // Given
        String accessToken = "valid_token";
        String providerUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        Map<String, Object> userInfo = Map.of(
                "sub", "12345",
                "email", "user@example.com",
                "name", "Test User");

        @SuppressWarnings("unchecked")
        ResponseEntity<Map> response = new ResponseEntity<>(userInfo, HttpStatus.OK);
        when(restTemplate.exchange(eq(providerUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        // When
        Map<String, Object> result = externalServiceClient.getUserInfoFromProvider(accessToken, providerUrl);

        // Then
        assertEquals(userInfo, result);
        verify(restTemplate).exchange(
                eq(providerUrl),
                eq(HttpMethod.GET),
                argThat(entity -> {
                    String authHeader = entity.getHeaders().getFirst("Authorization");
                    return authHeader != null && authHeader.equals("Bearer " + accessToken);
                }),
                eq(Map.class));
    }

    @Test
    void getUserInfoFromProvider_WithInvalidToken_ShouldThrowRuntimeException() {
        // Given
        String accessToken = "invalid_token";
        String providerUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        when(restTemplate.exchange(eq(providerUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RestClientException("401 Unauthorized"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> externalServiceClient.getUserInfoFromProvider(accessToken, providerUrl));

        assertEquals("Error obteniendo datos del usuario", exception.getMessage());
        assertTrue(exception.getCause() instanceof RestClientException);
    }

    @Test
    void getUserInfoFromProvider_WithNetworkError_ShouldThrowRuntimeException() {
        // Given
        String accessToken = "valid_token";
        String providerUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        when(restTemplate.exchange(eq(providerUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenThrow(new RuntimeException("Network timeout"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> externalServiceClient.getUserInfoFromProvider(accessToken, providerUrl));

        assertEquals("Error obteniendo datos del usuario", exception.getMessage());
        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    // fallbackGetUserInfo Tests

    @Test
    void fallbackGetUserInfo_ShouldReturnFallbackUserInfo() {
        // Given
        String accessToken = "test_token";
        String providerUrl = "https://example.com";
        Exception testException = new RuntimeException("Test error");

        // When
        Map<String, Object> result = externalServiceClient.fallbackGetUserInfo(accessToken, providerUrl, testException);

        // Then
        assertNotNull(result);
        assertEquals("unknown", result.get("sub"));
        assertEquals("unknown@example.com", result.get("email"));
        assertEquals("Usuario Temporal", result.get("name"));
        assertEquals(true, result.get("fallback"));
    }

    @Test
    void fallbackGetUserInfo_ShouldContainAllRequiredFields() {
        // Given
        String accessToken = "test_token";
        String providerUrl = "https://example.com";
        Exception testException = new RuntimeException("Test error");

        // When
        Map<String, Object> result = externalServiceClient.fallbackGetUserInfo(accessToken, providerUrl, testException);

        // Then
        assertTrue(result.containsKey("sub"));
        assertTrue(result.containsKey("email"));
        assertTrue(result.containsKey("name"));
        assertTrue(result.containsKey("fallback"));
        assertEquals(4, result.size());
    }

    // checkInternetConnectivity Tests

    @Test
    void checkInternetConnectivity_WithSuccessfulConnection_ShouldReturnTrue() {
        // Given
        ResponseEntity<String> successResponse = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.getForEntity(eq("https://www.google.com"), eq(String.class)))
                .thenReturn(successResponse);

        // When
        boolean result = externalServiceClient.checkInternetConnectivity();

        // Then
        assertTrue(result);
        verify(restTemplate).getForEntity("https://www.google.com", String.class);
    }

    @Test
    void checkInternetConnectivity_With2xxStatus_ShouldReturnTrue() {
        // Given
        ResponseEntity<String> successResponse = new ResponseEntity<>("OK", HttpStatus.ACCEPTED);
        when(restTemplate.getForEntity(eq("https://www.google.com"), eq(String.class)))
                .thenReturn(successResponse);

        // When
        boolean result = externalServiceClient.checkInternetConnectivity();

        // Then
        assertTrue(result);
    }

    @Test
    void checkInternetConnectivity_WithNon2xxStatus_ShouldReturnFalse() {
        // Given
        ResponseEntity<String> errorResponse = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.getForEntity(eq("https://www.google.com"), eq(String.class)))
                .thenReturn(errorResponse);

        // When
        boolean result = externalServiceClient.checkInternetConnectivity();

        // Then
        assertFalse(result);
    }

    @Test
    void checkInternetConnectivity_WithConnectionError_ShouldThrowRuntimeException() {
        // Given
        when(restTemplate.getForEntity(eq("https://www.google.com"), eq(String.class)))
                .thenThrow(new RestClientException("Connection refused"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> externalServiceClient.checkInternetConnectivity());

        assertEquals("Sin conectividad", exception.getMessage());
        assertTrue(exception.getCause() instanceof RestClientException);
    }

    @Test
    void checkInternetConnectivity_WithTimeoutError_ShouldThrowRuntimeException() {
        // Given
        when(restTemplate.getForEntity(eq("https://www.google.com"), eq(String.class)))
                .thenThrow(new RuntimeException("Read timeout"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> externalServiceClient.checkInternetConnectivity());

        assertEquals("Sin conectividad", exception.getMessage());
        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    // fallbackInternetCheck Tests

    @Test
    void fallbackInternetCheck_ShouldReturnFalse() {
        // Given
        Exception testException = new RuntimeException("Test error");

        // When
        boolean result = externalServiceClient.fallbackInternetCheck(testException);

        // Then
        assertFalse(result);
    }

    // Integration-style Tests

    @Test
    void getUserInfoFromProvider_WithCorrectAuthorizationHeader_ShouldSetBearerToken() {
        // Given
        String accessToken = "test_access_token";
        String providerUrl = "https://api.example.com/user";

        Map<String, Object> userInfo = Map.of("id", "123", "name", "Test");
        @SuppressWarnings("unchecked")
        ResponseEntity<Map> response = new ResponseEntity<>(userInfo, HttpStatus.OK);

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        // When
        externalServiceClient.getUserInfoFromProvider(accessToken, providerUrl);

        // Then
        verify(restTemplate).exchange(
                eq(providerUrl),
                eq(HttpMethod.GET),
                argThat(httpEntity -> {
                    String authHeader = httpEntity.getHeaders().getFirst("Authorization");
                    return "Bearer test_access_token".equals(authHeader);
                }),
                eq(Map.class));
    }

    @Test
    void constructor_ShouldCreateInstanceWithRestTemplate() {
        // When
        ExternalServiceClient client = new ExternalServiceClient();

        // Then
        assertNotNull(client);
    }

    // Edge Cases

    @Test
    void getUserInfoFromProvider_WithNullResponseBody_ShouldReturnNull() {
        // Given
        String accessToken = "valid_token";
        String providerUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        @SuppressWarnings("unchecked")
        ResponseEntity<Map> response = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(eq(providerUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        // When
        Map<String, Object> result = externalServiceClient.getUserInfoFromProvider(accessToken, providerUrl);

        // Then
        assertNull(result);
    }

    @Test
    void getUserInfoFromProvider_WithEmptyResponseBody_ShouldReturnEmptyMap() {
        // Given
        String accessToken = "valid_token";
        String providerUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

        Map<String, Object> emptyUserInfo = Map.of();
        @SuppressWarnings("unchecked")
        ResponseEntity<Map> response = new ResponseEntity<>(emptyUserInfo, HttpStatus.OK);
        when(restTemplate.exchange(eq(providerUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        // When
        Map<String, Object> result = externalServiceClient.getUserInfoFromProvider(accessToken, providerUrl);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
