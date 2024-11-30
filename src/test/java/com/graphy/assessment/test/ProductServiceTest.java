package com.graphy.assessment.test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.graphy.assessment.exception.CustomException;
import com.graphy.assessment.exception.RecordNotFoundException;
import com.graphy.assessment.model.Product;
import com.graphy.assessment.repository.ProductRepository;
import com.graphy.assessment.service.CacheService;
import com.graphy.assessment.service.impl.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProductById_CacheHit() throws Exception {
        Product mockProduct = new Product();
        when(cacheService.getFromCache("product_1")).thenReturn(mockProduct);

        Product product = productService.getProductById(1L);
        assertEquals("Test Product", product.getName());
        verify(productRepository, never()).findById(anyLong());
    }

    @Test
    void testGetProductById_CacheMiss() throws Exception {
        Product mockProduct = new Product();
        when(cacheService.getFromCache("product_1")).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        Product product = productService.getProductById(1L);
        assertEquals("Test Product", product.getName());
        verify(cacheService).addToCache("product_1", mockProduct);
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
    	lenient().when(cacheService.getFromCache("product_1")).thenReturn(null);
        when(cacheService.getFromCache("product_1")).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> productService.getProductById(1L));
    }
}