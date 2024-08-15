package com.djecommerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.djecommerce.model.Category;
import org.springframework.data.domain.Page;

import com.djecommerce.exception.ProductException;
import com.djecommerce.model.Product;
import com.djecommerce.repository.CategoryRepository;
import com.djecommerce.repository.ProductRepository;
import com.djecommerce.requestDTO.CreateProductRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class ProductServiceImplementation implements ProductService {

    private ProductRepository productRepository;
    private UserService userService;
    private CategoryRepository categoryRepository;

    public ProductServiceImplementation(ProductRepository productRepository, UserService userService, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product createProduct(CreateProductRequest createProductRequest) {

        Category topLevel = categoryRepository.findByName(createProductRequest.getTopLevelCategory());
        if (topLevel == null) {
            Category topLevelCategory = new Category();
            topLevelCategory.setName(createProductRequest.getTopLevelCategory());
            topLevelCategory.setLevel(1);

            topLevel = categoryRepository.save(topLevelCategory);
        }

        Category secondLevel = categoryRepository.findByNameAndParent(createProductRequest.getSecondLevelCategory(), topLevel.getName());
        if (secondLevel == null) {
            Category secondLevelCategory = new Category();
            secondLevelCategory.setName(createProductRequest.getSecondLevelCategory());
            secondLevelCategory.setParentCategory(topLevel);
            secondLevelCategory.setLevel(2);

            secondLevel = categoryRepository.save(secondLevelCategory);
        }

        Category thirdLevel = categoryRepository.findByNameAndParent(createProductRequest.getThirdLevelCategory(), secondLevel.getName());
        if (thirdLevel == null) {
            Category thirdLevelCategory = new Category();
            thirdLevelCategory.setName(createProductRequest.getThirdLevelCategory());
            thirdLevelCategory.setParentCategory(secondLevel);
            thirdLevelCategory.setLevel(3);

            thirdLevel = categoryRepository.save(thirdLevelCategory);
        }

        Product product = new Product();
        product.setTitle(createProductRequest.getTitle());
        product.setColor(createProductRequest.getColor());
        product.setDescription(createProductRequest.getDescription());
        product.setDiscountedPrice(createProductRequest.getDiscountedPrice());
        product.setDiscountPercent(createProductRequest.getDiscountedPersent());
        product.setImageUrl(createProductRequest.getImageUrl());
        product.setBrand(createProductRequest.getBrand());
        product.setPrice(createProductRequest.getPrice());
        product.setSizes(createProductRequest.getSizes());
        product.setQuantity(createProductRequest.getQuantity());
        product.setCategory(thirdLevel);
        product.setCreatedAt(LocalDateTime.now());

        Product saveProduct = productRepository.save(product);
        return saveProduct;
    }

    @Override
    public String deleteProduct(Long productId) throws ProductException {
        Product product=findProductById(productId);
		product.getSizes().clear();
		productRepository.delete(product);
        return "Product deleted successfully!!!!11";
    }

    @Override
    public Product updateProduct(Long productId, Product productUpdateRequest) throws ProductException {
		Product product=findProductById(productId);
		if(productUpdateRequest.getQuantity()!=0){
			product.setQuantity(productUpdateRequest.getQuantity());
		}
        return productRepository.save(product);
    }

    @Override
    public Product findProductById(Long id) throws ProductException {
		Optional<Product> existingProduct = productRepository.findById(id);
		if(existingProduct.isPresent()){
			return existingProduct.get();
		}
		throw new ProductException("Product not found with id " + id);
    }

    @Override
    public List<Product> findProductByCategory(String category) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<Product> getAllProduct(String category, List<String> colors, List<String> sizes, Integer minPrice,
                                       Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber,
                                       Integer pageSize) {
		Pageable pageable= PageRequest.of(pageNumber, pageSize);

		List<Product> products = productRepository.filterProducts(category,minPrice,maxPrice,minDiscount,sort);

        if(!colors.isEmpty()){
            products = products.stream()
                    .filter(p->colors.stream()
                            .anyMatch(c->c.equalsIgnoreCase(p.getColor())))
                    			.collect(Collectors.toList());
        }

        if(stock!= null){
            if(stock.equals("InStock")){
                products=products.stream().filter(p->p.getQuantity()>0).collect(Collectors.toList());
            } else if (stock.equals("OutOfStock")) {
                products=products.stream().filter(p->p.getQuantity()<1).collect(Collectors.toList());
            }
        }

        int startIndex= (int) pageable.getOffset();
        int endIndex = Math.min(startIndex+pageable.getPageSize(),products.size());

        List<Product> pageContent = products.subList(startIndex,endIndex);
        Page<Product> filteredProduct=new PageImpl<>(pageContent,pageable,products.size());

        return filteredProduct;
    }

}
