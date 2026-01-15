package com.example.erp.service;

import com.example.erp.dto.InventoryExportRequestDTO;
import com.example.erp.dto.InventoryImportRequestDTO;
import com.example.erp.dto.InventoryItemRequestDTO;
import com.example.erp.dto.InventoryItemResponseDTO;

import java.util.List;

public interface InventoryItemService {

    // Thêm item vào kho
    InventoryItemResponseDTO addItem(InventoryItemRequestDTO dto);

    // Xuất item (FIFO)
    InventoryItemResponseDTO exportItem(Long itemId, Long customerId);

    // Lấy tất cả items (không bao gồm deleted)
    List<InventoryItemResponseDTO> getAllItems();

    // Lấy items còn hàng (remainingQuantity > 0)
    List<InventoryItemResponseDTO> getAvailableItems();

    // Lấy item theo ID
    InventoryItemResponseDTO getItemById(Long itemId);

    // Lấy items theo khách hàng
    List<InventoryItemResponseDTO> getItemsByCustomer(Long customerId);

    // Xóa item (soft delete)
    void deleteItem(Long itemId);
    
    void importInventory(InventoryImportRequestDTO request);
    
    // ================== XUẤT KHO ==================
    void exportInventory(InventoryExportRequestDTO request); // 👈 THÊM DÒNG NÀY
    

}
