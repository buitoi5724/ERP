package com.example.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.erp.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ================= BASIC =================

    boolean existsByCode(String code);

    Optional<Product> findByCode(String code);

    // ================= FIX GALLERY =================

    @Query("""
        select distinct p
        from Product p
        left join fetch p.galleries
    """)
    List<Product> findAllWithGalleries();

    @Query("""
        select p
        from Product p
        left join fetch p.galleries
        where p.id = :id
    """)
    Optional<Product> findByIdWithGalleries(Long id);
}
