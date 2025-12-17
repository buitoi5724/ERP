package com.example.erp.repository;

import com.example.erp.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {

    @Query("SELECT pp FROM ProductPrice pp WHERE pp.productId = :productId ORDER BY pp.startDate DESC")
    List<ProductPrice> findByProductIdOrderByStartDateDesc(Long productId);

	ProductPrice findFirstByProductIdAndEndDateIsNull(Long productId);
}