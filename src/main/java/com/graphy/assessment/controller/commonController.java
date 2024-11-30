package com.graphy.assessment.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.graphy.assessment.model.Product;
import com.graphy.assessment.service.ProductService;

@RestController
@RequestMapping("/productCatalog")
public class commonController {
	
	private static final Logger logger = LoggerFactory.getLogger(commonController.class);
	
	@Autowired
	private ProductService productService;

	@GetMapping("/getAllProducts")
    public List<Product> getAllProducts() {
        try {
			return productService.getAllProducts();
		} catch (Exception e) {
			logger.error("API Call Failed");
		}
        
        return null;
    }
 
    @GetMapping("/getProduct/{id}")
    public Product getProductById(@PathVariable Long id) {
        try {
			return productService.getProductById(id);
		} catch (Exception e) {
			logger.error("API Call Failed");
		}
        return null;
    }
 
    @GetMapping("/filterProducts")
    public List<Product> filterProducts(@RequestParam String category,@RequestParam Double priceMin,@RequestParam Double priceMax) {
        try {
			return productService.filterProducts(category, priceMin, priceMax);
		} catch (Exception e) {
			logger.error("API Call Failed");
		}
        return null;
    }
 
    @PostMapping("/addProduct")
    public Product addProduct(@RequestBody Product product) {
        try {
			return productService.addProduct(product);
		} catch (Exception e) {
			logger.error("API Call Failed");
		}
        return null;
    }
 
    @PutMapping("/updateProduct/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        try {
			return productService.updateProduct(id, product);
		} catch (Exception e) {
			logger.error("API Call Failed");
		}
        return null;
    }
 
    @DeleteMapping("/deleteProduct/{id}")
    public void deleteProduct(@PathVariable Long id) {
        try {
			productService.deleteProduct(id);
		} catch (Exception e) {
			logger.error("API Call Failed");
		}
    }
}
