package com.example.erp.repository;

import com.example.erp.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findByRemainingQuantityGreaterThanAndDeletedFalse(int qty);

    List<InventoryItem> findByCustomerId(Long customerId);

    List<InventoryItem> findByInventoryIdAndRemainingQuantityGreaterThanOrderByReceivedDateAsc(
            Long inventoryId,
            Integer remainingQty
    );
}
