package com.example.erp.service;

import java.util.List;
import com.example.erp.dto.InventoryRequestDTO;
import com.example.erp.dto.InventoryResponseDTO;

/**
 * InventoryService: quản lý tồn kho tổng và các thao tác nhập/xuất/điều chỉnh.
 * <p>
 * - addStock: nhập kho, tự động tạo InventoryItem
 * - removeStock: xuất kho (không tác động InventoryItem)
 * - adjustStock: điều chỉnh tồn kho
 * - reserveStock / releaseReserved: đặt hàng trước hoặc hủy đặt
 * - importInitialStock: khởi tạo tồn kho ban đầu
 * - initializeInventoryForAllProducts: tạo record inventory cho tất cả product
 * - getAllProductsWithInventory: lấy tất cả sản phẩm kèm thông tin tồn kho
 */
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
    /**
     * Tạo record Inventory cho tất cả sản phẩm, warehouse chưa có inventory
     */
    void initializeInventoryForAllProducts(String warehouse);

    /**
     * Nhập tồn kho ban đầu cho product tại warehouse
     * @param productId Product ID
     * @param warehouse Warehouse
     * @param quantity Số lượng nhập
     */
    InventoryResponseDTO importInitialStock(Long productId, String warehouse, int quantity);

    // ==================== NEW ====================
    /**
     * Lấy tất cả sản phẩm kèm thông tin tồn kho tại warehouse.
     * Nếu sản phẩm chưa có inventory, quantity = 0
     */
    List<InventoryResponseDTO> getAllProductsWithInventory(String warehouse);
}
