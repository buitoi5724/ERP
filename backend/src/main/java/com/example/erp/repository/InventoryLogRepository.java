package com.example.erp.repository;

import com.example.erp.entity.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {

    // Lấy tất cả log của productId, sắp xếp mới nhất trước
    List<InventoryLog> findByProductIdOrderByActionTimeDesc(Long productId);
}
