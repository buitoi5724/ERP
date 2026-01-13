package com.example.erp.repository;

import com.example.erp.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    // Tồn kho chung
    List<InventoryItem>
    findByRemainingQuantityGreaterThanAndDeletedFalse(int qty);

    // Theo customer
    List<InventoryItem>
    findByCustomerIdAndDeletedFalse(Long customerId);

    // FIFO theo phiếu nhập
    List<InventoryItem>
    findByInventoryIdAndRemainingQuantityGreaterThanAndDeletedFalseOrderByReceivedDateAsc(
            Long inventoryId,
            Integer remainingQty
    );

    // FIFO theo sản phẩm (QUAN TRỌNG)
    List<InventoryItem>
    findByProductIdAndRemainingQuantityGreaterThanAndDeletedFalseOrderByReceivedDateAsc(
            Long productId,
            Integer remainingQty
    );

    // Tổng tồn kho theo sản phẩm
    @Query("""
        SELECT SUM(i.remainingQuantity)
        FROM InventoryItem i
        WHERE i.productId = :productId
          AND i.deleted = false
    """)
    Integer getTotalStockByProduct(@Param("productId") Long productId);
}