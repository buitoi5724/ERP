package com.example.erp.service.impl;

import com.example.erp.dto.ExportedInventoryItemDTO;
import com.example.erp.dto.ImportedInventoryItemDTO;
import com.example.erp.dto.InvoiceItemDTO;
import com.example.erp.dto.InvoiceRequestDTO;
import com.example.erp.dto.InvoiceResponseDTO;
import com.example.erp.entity.Invoice;
import com.example.erp.entity.InvoiceItem;
import com.example.erp.repository.InvoiceItemRepository;
import com.example.erp.repository.InvoiceRepository;
import com.example.erp.service.InvoiceService;
import com.example.erp.util.InvoiceStatus;
import com.example.erp.util.InvoiceType;
import com.example.erp.util.PriceType;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository itemRepository;

    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository,
            InvoiceItemRepository itemRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
    }

    // =====================================================
    // 🔥 CREATE EXPORT INVOICE (ĐƯỢC GỌI TỪ INVENTORY)
    // =====================================================
    @Override
    public InvoiceResponseDTO createExportInvoice(
            Long customerId,
            List<ExportedInventoryItemDTO> exportedItems
    ) {

        if (exportedItems == null || exportedItems.isEmpty()) {
            throw new IllegalArgumentException("Exported items is empty");
        }

        Invoice invoice = new Invoice();
        invoice.setCode("INV-" + System.currentTimeMillis());
        invoice.setCustomerId(customerId);
     // ✅ EXPORT → có CUSTOMER

        // ✅ EXPORT → KHÔNG CÓ supplier
        invoice.setPartnerId(null);

        invoice.setType(InvoiceType.EXPORT);
        invoice.setStatus(InvoiceStatus.DRAFT);

        invoice = invoiceRepository.save(invoice);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ExportedInventoryItemDTO e : exportedItems) {

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setInventoryItemId(e.getInventoryItemId());
            item.setProductId(e.getProductId());

            Integer quantity = e.getQuantity();           // Integer
            BigDecimal unitPrice = e.getSellPrice();      // BigDecimal

            BigDecimal totalPrice =
                    unitPrice.multiply(BigDecimal.valueOf(quantity));

            item.setQuantity(quantity);                   // ✅
            item.setUnitPrice(unitPrice);                 // (nếu dùng)
            item.setPrice(unitPrice);                     // ✅🔥 BẮT BUỘC
            item.setTotalPrice(totalPrice);               // ✅
            item.setPriceType(PriceType.EXPORT);

            totalAmount = totalAmount.add(totalPrice);

            itemRepository.save(item);
        }

        invoice.setTotalAmount(totalAmount);
        invoice = invoiceRepository.save(invoice);

        // ✅ QUAN TRỌNG: TRẢ DTO, KHÔNG TRẢ ENTITY
        return toResponseDTO(
                invoice,
                itemRepository.findByInvoiceId(invoice.getId())
        );
    
    }

    // =====================================================
    // QUERY
    // =====================================================
    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceById(Long id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        return toResponseDTO(
                invoice,
                itemRepository.findByInvoiceId(invoice.getId())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getAllInvoices() {

        return invoiceRepository.findAll().stream()
                .map(inv -> toResponseDTO(
                        inv,
                        itemRepository.findByInvoiceId(inv.getId())
                ))
                .collect(Collectors.toList());
    }

    // =====================================================
    // STATUS
    // =====================================================
    @Override
    public void markAsPaid(Long id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found"));

        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);
    }

    // =====================================================
    // ❌ KHÔNG CHO UPDATE / DELETE
    // =====================================================
    @Override
    public void deleteInvoice(Long id) {
        throw new UnsupportedOperationException(
                "Invoice generated from inventory cannot be deleted"
        );
    }

    @Override
    public InvoiceResponseDTO updateInvoice(Long id, InvoiceRequestDTO dto) {
        throw new UnsupportedOperationException(
                "Invoice generated from inventory cannot be updated"
        );
    }

    @Override
    public InvoiceResponseDTO createInvoice(InvoiceRequestDTO dto) {
        throw new UnsupportedOperationException(
                "Use inventory export to create invoice"
        );
    }

    // =====================================================
    // MAPPER
    // =====================================================
    private InvoiceResponseDTO toResponseDTO(
            Invoice invoice,
            List<InvoiceItem> items
    ) {

        InvoiceResponseDTO dto = new InvoiceResponseDTO();
        dto.setId(invoice.getId());
        dto.setCode(invoice.getCode());
        dto.setCustomerId(invoice.getCustomerId());
        dto.setType(invoice.getType());
        dto.setStatus(invoice.getStatus());
        dto.setPaymentMethod(invoice.getPaymentMethod());
        dto.setCreatedDate(invoice.getCreatedDate());
        dto.setTotalAmount(invoice.getTotalAmount());

        dto.setItems(
                items.stream().map(i -> {
                    InvoiceItemDTO itemDTO = new InvoiceItemDTO();
                    itemDTO.setProductId(i.getProductId());
                    itemDTO.setQuantity(i.getQuantity());
                    itemDTO.setPrice(i.getUnitPrice());

                    return itemDTO;
                }).collect(Collectors.toList())
        );

        return dto;
    }
    @Override
    public InvoiceResponseDTO createImportInvoice(
            Long supplierId,
            List<ImportedInventoryItemDTO> importedItems
    ) {

        if (importedItems == null || importedItems.isEmpty()) {
            throw new IllegalArgumentException("Imported items is empty");
        }

        Invoice invoice = new Invoice();
        invoice.setCode("IMP-" + System.currentTimeMillis());

        invoice.setSupplierId(supplierId); // ✅
        invoice.setCustomerId(null);       // ✅ DB cho phép null

        invoice.setType(InvoiceType.IMPORT);
        invoice.setStatus(InvoiceStatus.DRAFT);

        invoiceRepository.save(invoice);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (ImportedInventoryItemDTO i : importedItems) {

            InvoiceItem item = new InvoiceItem();
            item.setInvoice(invoice);
            item.setInventoryItemId(i.getInventoryItemId());
            item.setProductId(i.getProductId());

            Integer quantity = i.getQuantity();
            BigDecimal unitPrice = i.getImportPrice();

            BigDecimal totalPrice =
                    unitPrice.multiply(BigDecimal.valueOf(quantity));

            item.setQuantity(quantity);
            item.setUnitPrice(unitPrice);
            item.setPrice(unitPrice);              // 🔥 BẮT BUỘC
            item.setTotalPrice(totalPrice);
            item.setPriceType(PriceType.IMPORT);   // 🔥 KHÁC EXPORT

            totalAmount = totalAmount.add(totalPrice);

            itemRepository.save(item);
        }

        invoice.setTotalAmount(totalAmount);
        invoice = invoiceRepository.save(invoice);

        return toResponseDTO(
                invoice,
                itemRepository.findByInvoiceId(invoice.getId())
        );
    }
}
