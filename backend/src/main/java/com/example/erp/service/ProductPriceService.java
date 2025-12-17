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

	/**
	 * 📖 Lấy toàn bộ lịch sử giá của sản phẩm
	 */
	public List<ProductPrice> getByProductId(Long productId) {
		return productPriceRepository.findByProductIdOrderByStartDateDesc(productId);
	}

	/**
	 * ➕ Thêm giá mới cho sản phẩm, đồng thời đóng giá cũ
	 */
	public ProductPrice addNewPrice(Long productId, Double price) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));

		// 🔹 Đóng giá cũ nếu còn hiệu lực
		ProductPrice currentPrice = productPriceRepository
				.findFirstByProductIdAndEndDateIsNull(productId);
		if (currentPrice != null) {
			currentPrice.setEndDate(LocalDateTime.now());
			productPriceRepository.save(currentPrice);
		}

		// 🔹 Tạo giá mới
		ProductPrice productPrice = new ProductPrice();
		productPrice.setProductId(product.getId());
		productPrice.setPrice(price);
		productPrice.setStartDate(LocalDateTime.now());

		return productPriceRepository.save(productPrice);
	}
}
