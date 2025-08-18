package com.example.erp.service;

import com.example.erp.entity.ProductCategory;
import com.example.erp.repository.ProductCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryService(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductCategory> getAll() {
        return productCategoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<ProductCategory> getById(Long id) {
        return productCategoryRepository.findById(id);
    }

    @Transactional
    public ProductCategory save(ProductCategory category) {
        return productCategoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        productCategoryRepository.deleteById(id);
    }
}
