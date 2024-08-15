package com.djecommerce.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.djecommerce.exception.ProductException;
import com.djecommerce.model.Product;
import com.djecommerce.requestDTO.CreateProductRequest;

public interface ProductService {
	
	public Product createProduct(CreateProductRequest createProductRequest);
	
	public String deleteProduct(Long productId) throws ProductException;
	
	public Product updateProduct(Long productId, Product productUpdateRequest) throws ProductException;
	
	public Product findProductById(Long id) throws ProductException;
	
	public List<Product> findProductByCategory(String category);
	
	public Page<Product> getAllProduct(String category, List<String> colors, List<String> sizes, Integer minPrice, Integer maxPriceInteger,
			Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize);
 	
}
