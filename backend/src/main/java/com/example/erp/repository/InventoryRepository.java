package com.example.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.erp.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Lấy tồn kho của 1 sản phẩm trong 1 kho cụ thể
     * 👉 Dùng cho: nhập / xuất / reserve / release
     */
    Optional<Inventory> findByProductIdAndWarehouse(Long productId, String warehouse);

    /**
     * Lấy toàn bộ tồn kho của 1 sản phẩm (nhiều kho)
     * 👉 Dùng cho: báo cáo, tổng tồn
     */
    List<Inventory> findAllByProductId(Long productId);

    /**
     * Lấy toàn bộ tồn kho trong 1 kho
     * 👉 Dùng cho: InventoryPage
     */
    List<Inventory> findAllByWarehouse(String warehouse);

}
