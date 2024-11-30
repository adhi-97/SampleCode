package com.graphy.assessment.test;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.graphy.assessment.service.CacheService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private CacheService cacheService;

    @Test
    void testGetProductById_ValidId() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void testGetProductById_InvalidId() throws Exception {
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCacheInvalidationAfterUpdate() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isInternalServerError());

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Product\",\"price\":200.0}"))
                .andExpect(status().isInternalServerError());

        verify(cacheService, times(1)).invalidate("product_1");

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }
}