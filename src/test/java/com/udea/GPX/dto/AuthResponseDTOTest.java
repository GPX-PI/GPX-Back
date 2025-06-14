package com.udea.gpx.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthResponseDTOTest {
    @Test
    void testGettersAndSetters() {
        AuthResponseDTO dto = new AuthResponseDTO();
        dto.setAccessToken("token");
        dto.setRefreshToken("refresh");
        dto.setToken("tok");
        dto.setUserId(1L);
        dto.setAdmin(true);
        dto.setAuthProvider("google");
        dto.setProfileComplete(true);
        dto.setFirstName("John");
        dto.setPicture("pic.jpg");
        dto.setMessage("msg");
        assertEquals("token", dto.getAccessToken());
        assertEquals("refresh", dto.getRefreshToken());
        assertEquals("tok", dto.getToken());
        assertEquals(1L, dto.getUserId());
        assertTrue(dto.isAdmin());
        assertEquals("google", dto.getAuthProvider());
        assertTrue(dto.isProfileComplete());
        assertEquals("John", dto.getFirstName());
        assertEquals("pic.jpg", dto.getPicture());
        assertEquals("msg", dto.getMessage());
    }
}
