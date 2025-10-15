package com.example.erp.repository;

import com.example.erp.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {

    // 🔹 Lấy tất cả lịch sử giá của một sản phẩm
    List<ProductPrice> findByProduct_Id(Long productId);

    // 🔹 Lấy lịch sử giá, sắp xếp mới nhất trước
    List<ProductPrice> findByProduct_IdOrderByStartDateDesc(Long productId);

    // 🔹 Lấy giá hiện tại (endDate = null)
    ProductPrice findFirstByProduct_IdAndEndDateIsNull(Long productId);
}
