package com.example.erp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.erp.entity.ProductGallery;
import com.example.erp.entity.Product;

public interface ProductGalleryRepository extends JpaRepository<ProductGallery, Long> {

    List<ProductGallery> findByProduct(Product product);

}
