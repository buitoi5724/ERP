package com.example.erp.repository;

import com.example.erp.entity.InventoryTransaction;  // Thêm dòng này
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    // Không cần thêm gì nếu chỉ dùng method mặc định
}