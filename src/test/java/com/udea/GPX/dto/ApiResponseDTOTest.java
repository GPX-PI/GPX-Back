package com.udea.gpx.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseDTOTest {
    @Test
    void testConstructorsAndGettersSetters() {
        ApiResponseDTO<String> dto = new ApiResponseDTO<>();
        dto.setSuccess(true);
        dto.setMessage("ok");
        dto.setData("data");
        dto.setTimestamp(123L);
        assertTrue(dto.isSuccess());
        assertEquals("ok", dto.getMessage());
        assertEquals("data", dto.getData());
        assertEquals(123L, dto.getTimestamp());
    }

    @Test
    void testAllArgsConstructor() {
        ApiResponseDTO<String> dto = new ApiResponseDTO<>(true, "msg", "data");
        assertTrue(dto.isSuccess());
        assertEquals("msg", dto.getMessage());
        assertEquals("data", dto.getData());
    }

    @Test
    void testStaticSuccess() {
        ApiResponseDTO<String> dto = ApiResponseDTO.success("ok", "data");
        assertTrue(dto.isSuccess());
        assertEquals("ok", dto.getMessage());
        assertEquals("data", dto.getData());
    }

    @Test
    void testStaticError() {
        ApiResponseDTO<String> dto = ApiResponseDTO.error("fail");
        assertFalse(dto.isSuccess());
        assertEquals("fail", dto.getMessage());
        assertNull(dto.getData());
    }
}
