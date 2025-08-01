package com.example.erp.service;

import com.example.erp.entity.Product;
import com.example.erp.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
    
    public byte[] getImage(Long id) throws IOException {
    	Product product = getById(id).get();
    	Path imagePath = Paths.get(product.getImage());

        if (!Files.exists(imagePath)) {
            return null;
        }
    	byte[] imageBytes = Files.readAllBytes(imagePath);
    	return imageBytes;
    }
}
