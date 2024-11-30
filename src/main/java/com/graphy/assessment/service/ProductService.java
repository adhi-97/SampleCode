package com.graphy.assessment.service;

import java.util.List;

import com.graphy.assessment.model.Product;

public interface ProductService {

    public List<Product> getAllProducts() throws Exception;
    public Product getProductById(Long id) throws Exception;
    public List<Product> filterProducts(String category, Double priceMin, Double priceMax) throws Exception;
    public Product addProduct(Product product) throws Exception;
    public Product updateProduct(Long id, Product product) throws Exception;
    public void deleteProduct(Long id) throws Exception;
}
