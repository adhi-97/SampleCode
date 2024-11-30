package com.graphy.assessment.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.graphy.assessment.exception.CustomException;
import com.graphy.assessment.exception.RecordNotFoundException;
import com.graphy.assessment.model.Product;
import com.graphy.assessment.repository.ProductRepository;
import com.graphy.assessment.service.CacheService;
import com.graphy.assessment.service.ProductService;

import java.util.List;
 
@Service
public class ProductServiceImpl implements ProductService{
	
	private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
 
    @Autowired
    ProductRepository productRepository;
    
    @Autowired
    CacheService cacheService;
 
    @Cacheable(value = "products", key = "'allProducts'")
    public List<Product> getAllProducts() throws Exception{
    	logger.info("Value Taken From DB");
        return productRepository.findAll();
    }
 
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(Long id) throws Exception{
    	logger.info("Value Taken From DB");
        return productRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Product not found"));
    }
 
    @Cacheable(value = "products", key = "'filtered_'+#category+'_'+#priceMin+'_'+#priceMax")
    public List<Product> filterProducts(String category, Double priceMin, Double priceMax) throws Exception{
    	logger.info("Value Taken From DB");
        return productRepository.findByCategoryAndPriceBetween(category, priceMin, priceMax);
    }
 
    @CacheEvict(value = "products", allEntries = true)
    public Product addProduct(Product product) throws Exception{
        return productRepository.save(product);
    }
 
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Long id, Product product) throws Exception{
        Product existing = productRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Product not found"));
        existing.setName(product.getName());
        existing.setCategory(product.getCategory());
        existing.setPrice(product.getPrice());
        existing.setAvailable(product.getAvailable());
        return productRepository.save(existing);
    }
 
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) throws Exception{
        productRepository.deleteById(id);
    }
}