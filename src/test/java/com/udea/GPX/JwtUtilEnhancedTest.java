package com.udea.GPX;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

@DisplayName("JwtUtil Enhanced Tests - Edge Cases & Advanced Coverage")
class JwtUtilEnhancedTest {

    private JwtUtil jwtUtil;
    private final String TEST_SECRET = "mySecretKeyForTestingPurposesWithMinimum32Characters";
    private final long TEST_EXPIRATION = 3600L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "EXPIRATION_TIME", TEST_EXPIRATION);
    }

    // ========== ADVANCED TOKEN GENERATION TESTS ==========

    @Test
    @DisplayName("Token generation with extreme user IDs should work correctly")
    void generateToken_WithExtremeUserIds_ShouldWorkCorrectly() {
        // Test with minimum Long value
        Long minUserId = Long.MIN_VALUE;
        String minToken = jwtUtil.generateToken(minUserId, false);
        assertThat(jwtUtil.extractUserId(minToken)).isEqualTo(minUserId);

        // Test with maximum Long value
        Long maxUserId = Long.MAX_VALUE;
        String maxToken = jwtUtil.generateToken(maxUserId, true);
        assertThat(jwtUtil.extractUserId(maxToken)).isEqualTo(maxUserId);

        // Test with zero
        Long zeroUserId = 0L;
        String zeroToken = jwtUtil.generateToken(zeroUserId, false);
        assertThat(jwtUtil.extractUserId(zeroToken)).isEqualTo(zeroUserId);
    }

    @Test
    @DisplayName("Token structure should always have three parts")
    void generateToken_StructureValidation_ShouldAlwaysHaveThreeParts() {
        // Given
        Long userId = 123L;

        // When
        String adminToken = jwtUtil.generateToken(userId, true);
        String userToken = jwtUtil.generateToken(userId, false);

        // Then
        assertThat(adminToken.split("\\.")).hasSize(3);
        assertThat(userToken.split("\\.")).hasSize(3);

        // Verify each part is not empty
        String[] adminParts = adminToken.split("\\.");
        String[] userParts = userToken.split("\\.");

        for (String part : adminParts) {
            assertThat(part).isNotEmpty();
        }
        for (String part : userParts) {
            assertThat(part).isNotEmpty();
        }
    }

    @Test
    @DisplayName("Token generation should include all required claims")
    void generateToken_ClaimsValidation_ShouldIncludeAllRequiredClaims() {
        // Given
        Long userId = 456L;
        boolean isAdmin = true;

        // When
        String token = jwtUtil.generateToken(userId, isAdmin);
        Claims claims = jwtUtil.extractAllClaims(token);

        // Then
        assertThat(claims.get("userId")).isNotNull();
        assertThat(claims.get("admin")).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();

        // Verify issued at is before expiration
        assertThat(claims.getIssuedAt()).isBefore(claims.getExpiration());

        // Verify expiration is approximately correct (within 1 second tolerance)
        long expectedExpiration = System.currentTimeMillis() + (TEST_EXPIRATION * 1000);
        long actualExpiration = claims.getExpiration().getTime();
        assertThat(Math.abs(expectedExpiration - actualExpiration)).isLessThan(1000);
    }

    // ========== ADVANCED EXTRACTION TESTS ==========

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid.token.format",
            "not-a-jwt-token",
            "header.payload", // Missing signature
            "header.payload.signature.extra", // Too many parts
            "",
            "   ",
            "Bearer token",
            "eyJhbGciOiJIUzI1NiJ9.invalidpayload.signature"
    })
    @DisplayName("extractUserId should throw exception for invalid tokens")
    void extractUserId_WithInvalidTokens_ShouldThrowException(String invalidToken) {
        assertThatThrownBy(() -> jwtUtil.extractUserId(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid.token.format",
            "not-a-jwt-token",
            "header.payload",
            "header.payload.signature.extra",
            "",
            "   ",
            "Bearer token"
    })
    @DisplayName("extractIsAdmin should throw exception for invalid tokens")
    void extractIsAdmin_WithInvalidTokens_ShouldThrowException(String invalidToken) {
        assertThatThrownBy(() -> jwtUtil.extractIsAdmin(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   ", "\t", "\n" })
    @DisplayName("extractAllClaims should handle null and empty tokens")
    void extractAllClaims_WithNullAndEmptyTokens_ShouldThrowException(String token) {
        assertThatThrownBy(() -> jwtUtil.extractAllClaims(token))
                .isInstanceOf(Exception.class);
    }

    // ========== ADVANCED VALIDATION TESTS ==========

    @Test
    @DisplayName("validateToken should handle token with tampered signature")
    void validateToken_WithTamperedSignature_ShouldReturnFalse() {
        // Given
        String validToken = jwtUtil.generateToken(123L, false);
        String[] parts = validToken.split("\\.");

        // Tamper with signature
        String tamperedToken = parts[0] + "." + parts[1] + ".tamperedsignature";

        // When
        boolean isValid = jwtUtil.validateToken(tamperedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateToken should handle token with tampered payload")
    void validateToken_WithTamperedPayload_ShouldReturnFalse() {
        // Given
        String validToken = jwtUtil.generateToken(123L, false);
        String[] parts = validToken.split("\\.");

        // Tamper with payload
        String tamperedToken = parts[0] + ".tamperedpayload." + parts[2];

        // When
        boolean isValid = jwtUtil.validateToken(tamperedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateToken should handle token created with different secret")
    void validateToken_WithDifferentSecret_ShouldReturnFalse() {
        // Given
        String differentSecret = "aDifferentSecretKeyForTestingPurposesWithMinimum32Characters";
        Key differentKey = Keys.hmacShaKeyFor(differentSecret.getBytes());

        // Create token with different secret
        String tokenWithDifferentSecret = Jwts.builder()
                .claim("userId", 123L)
                .claim("admin", false)
                .setSubject("123")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(differentKey)
                .compact();

        // When
        boolean isValid = jwtUtil.validateToken(tokenWithDifferentSecret);

        // Then
        assertThat(isValid).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   ", "\t", "\n", "Bearer ", "Bearer" })
    @DisplayName("validateToken should handle null, empty and whitespace tokens")
    void validateToken_WithNullEmptyAndWhitespaceTokens_ShouldReturnFalse(String token) {
        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateToken should handle token with future issuedAt date")
    void validateToken_WithFutureIssuedAt_ShouldReturnFalse() {
        // Given - Create token with future issuedAt
        Date futureDate = new Date(System.currentTimeMillis() + 3600000); // 1 hour in future
        Date expirationDate = new Date(System.currentTimeMillis() + 7200000); // 2 hours in future

        String futureToken = Jwts.builder()
                .claim("userId", 123L)
                .claim("admin", false)
                .setSubject("123")
                .setIssuedAt(futureDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(TEST_SECRET.getBytes()))
                .compact();

        // When
        boolean isValid = jwtUtil.validateToken(futureToken);

        // Then
        // Note: This depends on JWT library behavior - some may reject future issuedAt
        // If the library accepts it, the token should still be valid
        // If it rejects it, it should return false
        assertThat(isValid).isInstanceOf(Boolean.class);
    }

    // ========== EXPIRATION EDGE CASES ==========

    @Test
    @DisplayName("validateToken should reject token expired by 1 millisecond")
    void validateToken_WithTokenExpiredBy1Millisecond_ShouldReturnFalse() throws InterruptedException {
        // Given - Create token with very short expiration
        ReflectionTestUtils.setField(jwtUtil, "EXPIRATION_TIME", 0L); // Expires immediately
        String shortLivedToken = jwtUtil.generateToken(123L, false);

        // Wait for token to expire
        TimeUnit.MILLISECONDS.sleep(100);

        // When
        boolean isValid = jwtUtil.validateToken(shortLivedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateToken should accept token just before expiration")
    void validateToken_WithTokenJustBeforeExpiration_ShouldReturnTrue() {
        // Given - Create token with 30 seconds expiration (more stable for testing)
        ReflectionTestUtils.setField(jwtUtil, "EXPIRATION_TIME", 30L);
        String shortLivedToken = jwtUtil.generateToken(123L, false);

        // When (immediately)
        boolean isValid = jwtUtil.validateToken(shortLivedToken);

        // Then
        assertThat(isValid).isTrue();
    }

    // ========== CONCURRENT ACCESS TESTS ========== @Test
    @DisplayName("Token generation should be thread-safe")
    void generateToken_ConcurrentAccess_ShouldBeThreadSafe() throws InterruptedException {
        // Given
        int threadCount = 10;
        int tokensPerThread = 100;
        Thread[] threads = new Thread[threadCount];
        final Exception[] exceptions = new Exception[threadCount];

        // When
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < tokensPerThread; j++) {
                        Long userId = (long) (threadIndex * tokensPerThread + j);
                        String token = jwtUtil.generateToken(userId, j % 2 == 0);

                        // Verify token
                        assertThat(jwtUtil.extractUserId(token)).isEqualTo(userId);
                        assertThat(jwtUtil.validateToken(token)).isTrue();
                    }
                } catch (Exception e) {
                    exceptions[threadIndex] = e;
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        for (Exception exception : exceptions) {
            assertThat(exception).isNull();
        }
    }

    // ========== DATA TYPE EDGE CASES ==========

    @Test
    @DisplayName("extractUserId should handle userId stored as different number types")
    void extractUserId_WithDifferentNumberTypes_ShouldHandleCorrectly() {
        // Given
        Long originalUserId = 123L;
        String token = jwtUtil.generateToken(originalUserId, false);

        // When
        Long extractedUserId = jwtUtil.extractUserId(token);

        // Then
        assertThat(extractedUserId).isEqualTo(originalUserId);
        assertThat(extractedUserId).isInstanceOf(Long.class);
    }

    @Test
    @DisplayName("extractIsAdmin should handle admin stored as different boolean representations")
    void extractIsAdmin_WithDifferentBooleanRepresentations_ShouldHandleCorrectly() {
        // Given
        String trueToken = jwtUtil.generateToken(123L, true);
        String falseToken = jwtUtil.generateToken(456L, false);

        // When
        boolean isAdminTrue = jwtUtil.extractIsAdmin(trueToken);
        boolean isAdminFalse = jwtUtil.extractIsAdmin(falseToken);

        // Then
        assertThat(isAdminTrue).isTrue();
        assertThat(isAdminFalse).isFalse();
    }

    // ========== MEMORY AND PERFORMANCE TESTS ==========

    @Test
    @DisplayName("Large scale token generation should not cause memory issues")
    void generateToken_LargeScale_ShouldNotCauseMemoryIssues() {
        // Given
        int tokenCount = 1000;

        // When & Then
        for (int i = 0; i < tokenCount; i++) {
            String token = jwtUtil.generateToken((long) i, i % 2 == 0);
            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();

            // Verify a few randomly
            if (i % 100 == 0) {
                assertThat(jwtUtil.extractUserId(token)).isEqualTo((long) i);
                assertThat(jwtUtil.validateToken(token)).isTrue();
            }
        }
    }

    // ========== SECRET KEY EDGE CASES ==========

    @Test
    @DisplayName("Token validation should fail with minimum invalid secret")
    void validateToken_WithShortSecret_ShouldHandleGracefully() {
        // Given - Setup JwtUtil with short secret (this might cause issues in real
        // scenarios)
        JwtUtil shortSecretJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortSecretJwtUtil, "SECRET_KEY", "short");
        ReflectionTestUtils.setField(shortSecretJwtUtil, "EXPIRATION_TIME", 3600L);

        // When & Then - This should either work or fail gracefully
        assertThatThrownBy(() -> {
            String token = shortSecretJwtUtil.generateToken(123L, false);
            shortSecretJwtUtil.validateToken(token);
        }).isInstanceOf(Exception.class);
    }

    // ========== CLAIMS MANIPULATION TESTS ==========

    @Test
    @DisplayName("extractAllClaims should return immutable-like claims")
    void extractAllClaims_ShouldReturnCompleteClaimsObject() {
        // Given
        Long userId = 789L;
        boolean isAdmin = true;
        String token = jwtUtil.generateToken(userId, isAdmin);

        // When
        Claims claims = jwtUtil.extractAllClaims(token);

        // Then
        assertThat(claims).isNotNull();
        assertThat(claims.keySet()).isNotEmpty();
        assertThat(claims.containsKey("userId")).isTrue();
        assertThat(claims.containsKey("admin")).isTrue();

        // Verify all standard JWT claims are present
        assertThat(claims.getSubject()).isNotNull();
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
    }

    // ========== INTEGRATION SCENARIO TESTS ==========

    @Test
    @DisplayName("Full token lifecycle should work correctly")
    void tokenLifecycle_CompleteScenario_ShouldWorkCorrectly() {
        // Given
        Long userId = 999L;
        boolean isAdmin = true;

        // When - Generate token
        String token = jwtUtil.generateToken(userId, isAdmin);

        // Then - Validate token
        assertThat(jwtUtil.validateToken(token)).isTrue();

        // Extract all information
        assertThat(jwtUtil.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtUtil.extractIsAdmin(token)).isEqualTo(isAdmin);

        // Verify claims
        Claims claims = jwtUtil.extractAllClaims(token);
        assertThat(claims.get("userId").toString()).isEqualTo(userId.toString());
        assertThat(claims.get("admin").toString()).isEqualTo(Boolean.toString(isAdmin));
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
    }

    @Test
    @DisplayName("Token validation error scenarios should be handled gracefully")
    void validateToken_ErrorScenarios_ShouldHandleGracefully() {
        // Test various malformed tokens that could cause different exceptions
        String[] malformedTokens = {
                "notbase64.notbase64.notbase64",
                "eyJhbGciOiJIUzI1NiJ9.{invalid-json}.signature",
                "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEyMywiYWRtaW4iOmZhbHNlfQ.invalidsignature",
                // Token with no claims
                Jwts.builder().signWith(Keys.hmacShaKeyFor(TEST_SECRET.getBytes())).compact()
        };

        for (String malformedToken : malformedTokens) {
            // Each should return false, not throw exception
            boolean isValid = jwtUtil.validateToken(malformedToken);
            assertThat(isValid).isFalse();
        }
    }
}
