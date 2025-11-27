package com.example.erp.service;

import com.example.erp.entity.Inventory;
import java.util.List;

public interface InventoryService {
    Inventory createOrUpdateInventory(Long productId, int quantity);
    Inventory getInventoryByProductId(Long productId);
    List<Inventory> getAllInventory();
}
