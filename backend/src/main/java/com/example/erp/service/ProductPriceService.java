package com.example.erp.service;

import com.example.erp.entity.Product;
import com.example.erp.entity.ProductPrice;
import com.example.erp.repository.ProductPriceRepository;
import com.example.erp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductPriceService {

    @Autowired
    private ProductPriceRepository productPriceRepository;

    @Autowired
    private ProductRepository productRepository;

    // Lấy lịch sử giá theo productId
    public List<ProductPrice> getByProductId(Long productId) {
        return productPriceRepository.findByProduct_IdOrderByStartDateDesc(productId);
    }

    // Thêm giá mới cho sản phẩm
    public ProductPrice addNewPrice(Long productId, Double price) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductPrice productPrice = new ProductPrice();
        productPrice.setProduct(product);
        productPrice.setPrice(price);
        productPrice.setStartDate(LocalDateTime.now());

        return productPriceRepository.save(productPrice);
    }
}
