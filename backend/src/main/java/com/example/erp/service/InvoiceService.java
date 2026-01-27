package com.example.erp.service;

import com.example.erp.dto.ExportedInventoryItemDTO;
import com.example.erp.dto.ImportedInventoryItemDTO;
import com.example.erp.dto.InvoiceRequestDTO;
import com.example.erp.dto.InvoiceResponseDTO;
import java.util.List;

public interface InvoiceService {

    // ========= GIỮ NGUYÊN =========
    InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto);

    InvoiceResponseDTO getInvoiceById(Long id);

    List<InvoiceResponseDTO> getAllInvoices();

    InvoiceResponseDTO updateInvoice(Long id, InvoiceRequestDTO dto);

    void deleteInvoice(Long id);

    void markAsPaid(Long id);

    // ========= 🔥 THÊM MỚI =========
  
    InvoiceResponseDTO createExportInvoice(
            Long customerId,
            List<ExportedInventoryItemDTO> exportedItems
    );
    // 🔥 THÊM METHOD NÀY
    InvoiceResponseDTO createImportInvoice(
            Long supplierId,
            List<ImportedInventoryItemDTO> importedItems
    );
    
    
}
