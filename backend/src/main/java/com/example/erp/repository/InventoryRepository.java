package com.example.erp.repository;

import com.example.erp.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Tìm tất cả inventory theo danh sách productId
    List<Inventory> findByProductIdIn(List<Long> productIds);
}