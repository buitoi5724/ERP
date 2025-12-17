package com.example.erp.service;

import java.util.List;
import com.example.erp.dto.InventoryRequestDTO;
import com.example.erp.dto.InventoryResponseDTO;

public interface InventoryService {

    // ==================== INVENTORY OPERATIONS ====================
    InventoryResponseDTO addStock(InventoryRequestDTO dto);
    InventoryResponseDTO removeStock(InventoryRequestDTO dto);
    InventoryResponseDTO adjustStock(InventoryRequestDTO dto);
    InventoryResponseDTO reserveStock(InventoryRequestDTO dto);
    InventoryResponseDTO releaseReserved(InventoryRequestDTO dto);

    // ==================== GET ====================
    InventoryResponseDTO getByProductIdAndWarehouse(Long productId, String warehouse);
    List<InventoryResponseDTO> getAllByProductId(Long productId);

    // ==================== INITIALIZATION ====================
    void initializeInventoryForAllProducts(String warehouse);
    InventoryResponseDTO importInitialStock(Long productId, String warehouse, int quantity);

    // ==================== NEW ====================
    /**
     * Lấy tất cả sản phẩm kèm thông tin tồn kho tại warehouse.
     * Nếu sản phẩm chưa có inventory, quantity = 0
     */
    List<InventoryResponseDTO> getAllProductsWithInventory(String warehouse);
}
