package com.example.erp.service;

import com.example.erp.dto.ExportedInventoryItemDTO;
import com.example.erp.dto.InventoryExportRequestDTO;
import com.example.erp.dto.InventoryImportRequestDTO;
import com.example.erp.dto.InventoryItemRequestDTO;
import com.example.erp.dto.InventoryItemResponseDTO;

import java.util.List;

public interface InventoryItemService {

    InventoryItemResponseDTO addItem(InventoryItemRequestDTO dto);

    InventoryItemResponseDTO exportItem(Long itemId, Long customerId);

    List<InventoryItemResponseDTO> getAllItems();

    List<InventoryItemResponseDTO> getAvailableItems();

    InventoryItemResponseDTO getItemById(Long itemId);

    List<InventoryItemResponseDTO> getItemsByCustomer(Long customerId);

    void deleteItem(Long itemId);

    void importInventory(InventoryImportRequestDTO request);

    // ✅ ĐỔI RETURN TYPE
    List<ExportedInventoryItemDTO> exportInventory(
            InventoryExportRequestDTO request
    );
}
