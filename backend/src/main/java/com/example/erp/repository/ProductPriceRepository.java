package com.example.erp.repository;

import com.example.erp.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    List<ProductPrice> findByProductIdOrderByStartDateDesc(Long productId);
}
