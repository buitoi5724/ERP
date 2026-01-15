package com.example.erp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductIdAndWarehouse(Long productId, String warehouse);
    
    Optional<Inventory> findByProductId(Long productId);

	Optional<Inventory> findByWarehouse(String warehouse);
}
