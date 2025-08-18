package com.example.erp.service;

import com.example.erp.entity.Product;
import com.example.erp.entity.ProductPrice;
import com.example.erp.repository.ProductPriceRepository;
import com.example.erp.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductPriceService {

    private final ProductPriceRepository productPriceRepository;
    private final ProductRepository productRepository;

    public ProductPriceService(ProductPriceRepository productPriceRepository, ProductRepository productRepository) {
        this.productPriceRepository = productPriceRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductPrice> getByProductId(Long productId) {
        return productPriceRepository.findByProductIdOrderByStartDateDesc(productId);
    }

    @Transactional
    public ProductPrice addNewPrice(Long productId, Double newPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // đóng giá cũ nếu còn hiệu lực
        List<ProductPrice> history = productPriceRepository.findByProductIdOrderByStartDateDesc(productId);
        history.stream().filter(p -> p.getEndDate() == null).forEach(p -> p.setEndDate(LocalDateTime.now()));

        // thêm giá mới
        ProductPrice newPriceEntity = new ProductPrice();
        newPriceEntity.setProduct(product);
        newPriceEntity.setPrice(newPrice);
        newPriceEntity.setStartDate(LocalDateTime.now());
        newPriceEntity.setEndDate(null);

        product.setPrice(newPrice); // update giá hiện tại

        productRepository.save(product);
        return productPriceRepository.save(newPriceEntity);
    }

    @Transactional
    public void delete(Long id) {
        productPriceRepository.deleteById(id);
    }

	public ProductPrice save(ProductPrice price) {
		// TODO Auto-generated method stub
		return null;
	}
}
