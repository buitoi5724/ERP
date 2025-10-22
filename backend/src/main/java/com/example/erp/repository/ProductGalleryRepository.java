package com.example.erp.repository;

import com.example.erp.entity.ProductGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProductGalleryRepository extends JpaRepository<ProductGallery, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductGallery pg WHERE pg.product.id = :productId")
    void deleteByProductId(Long productId);
}
