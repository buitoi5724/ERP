package com.example.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.erp.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
