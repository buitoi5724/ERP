package com.example.erp.service.impl;

import com.example.erp.dto.ExportedInventoryItemDTO;
import com.example.erp.dto.ImportedInventoryItemDTO;
import com.example.erp.dto.InventoryExportRequestDTO;
import com.example.erp.dto.InventoryImportRequestDTO;
import com.example.erp.dto.InventoryItemRequestDTO;
import com.example.erp.dto.InventoryItemResponseDTO;
import com.example.erp.entity.Inventory;
import com.example.erp.entity.InventoryItem;
import com.example.erp.entity.Product;
import com.example.erp.entity.Supplier;
import com.example.erp.repository.InventoryItemRepository;
import com.example.erp.repository.InventoryRepository;
import com.example.erp.repository.ProductRepository;
import com.example.erp.repository.SupplierRepository;
import com.example.erp.service.InventoryItemService;
import com.example.erp.util.InventoryStatus;
import com.example.erp.util.InventoryItemStatus;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.erp.service.InvoiceService;
@Service
@Transactional
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
	private InvoiceService invoiceService;

    public InventoryItemServiceImpl(
            InventoryItemRepository itemRepository,
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            InvoiceService invoiceService   // 👈 THÊM
    ) {
        this.itemRepository = itemRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.invoiceService = invoiceService; // 👈 THÊM
    }
    // =====================================================
    // ❌ KHÔNG DÙNG ADD ITEM LẺ
    // =====================================================
    @Override
    public InventoryItemResponseDTO addItem(InventoryItemRequestDTO dto) {
        throw new UnsupportedOperationException(
                "Không sử dụng addItem – chỉ nhập kho qua importInventory"
        );
    }

    // =====================================================
    // ❌ KHÔNG DÙNG XUẤT THEO ITEM
    // =====================================================
    @Override
    public InventoryItemResponseDTO exportItem(Long itemId, Long customerId) {
        throw new UnsupportedOperationException(
                "Không sử dụng exportItem – chỉ xuất kho qua exportInventory (FIFO)"
        );
    }

    // =====================================================
    // NHẬP KHO THEO PHIẾU (PRODUCT + WAREHOUSE)
    // =====================================================
    @Override
    @Transactional
    public void importInventory(InventoryImportRequestDTO request) {

        // 🔥 Map<SupplierId, List<ImportedInventoryItemDTO>>
        Map<Long, List<ImportedInventoryItemDTO>> itemsBySupplier = new HashMap<>();

        for (InventoryItemRequestDTO itemDto : request.getItems()) {

            // ===== 1️⃣ INVENTORY =====
            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouse(
                            itemDto.getProductId(),
                            request.getWarehouse()
                    )
                    .orElseGet(() -> {
                        Inventory inv = new Inventory();
                        inv.setProductId(itemDto.getProductId());
                        inv.setWarehouse(request.getWarehouse());
                        inv.setQuantity(0);
                        inv.setStatus(InventoryStatus.ACTIVE);
                        return inventoryRepository.save(inv);
                    });

            // ===== 2️⃣ INVENTORY ITEM =====
            InventoryItem item = new InventoryItem();
            item.setInventoryId(inventory.getId());
            item.setProductId(itemDto.getProductId());
            item.setSupplierId(itemDto.getSupplierId()); // ✅ supplier ở ITEM
            item.setBatchNumber(itemDto.getBatchNumber());

            item.setQuantity(itemDto.getQuantity());
            item.setRemainingQuantity(itemDto.getQuantity());
            item.setImportPrice(itemDto.getImportPrice());
            item.setUnitPrice(itemDto.getImportPrice());

            BigDecimal totalPrice =
                    itemDto.getImportPrice()
                            .multiply(BigDecimal.valueOf(itemDto.getQuantity()));

            item.setTotalPrice(totalPrice);

            item.setReceivedDate(
                    request.getDate() != null
                            ? LocalDate.parse(request.getDate())
                            : LocalDate.now()
            );

            item.setStatus(InventoryItemStatus.AVAILABLE);
            item.setDeleted(false);

            itemRepository.save(item);

            // ===== 3️⃣ UPDATE INVENTORY =====
            inventory.setQuantity(
                    inventory.getQuantity() + itemDto.getQuantity()
            );
            inventory.setLastImportDate(LocalDateTime.now());
            inventoryRepository.save(inventory);

            // ===== 4️⃣ PREPARE DTO FOR INVOICE =====
            ImportedInventoryItemDTO invoiceItem = new ImportedInventoryItemDTO();
            invoiceItem.setInventoryItemId(item.getId());
            invoiceItem.setProductId(item.getProductId());
            invoiceItem.setQuantity(itemDto.getQuantity());
            invoiceItem.setImportPrice(itemDto.getImportPrice());
            invoiceItem.setSupplierId(itemDto.getSupplierId()); // 🔥 KEY

            // 🔥 GROUP BY SUPPLIER
            itemsBySupplier
                    .computeIfAbsent(itemDto.getSupplierId(), k -> new ArrayList<>())
                    .add(invoiceItem);
        }

        // ===== 5️⃣ CREATE IMPORT INVOICE THEO SUPPLIER =====
        itemsBySupplier.forEach((supplierId, items) -> {
            invoiceService.createImportInvoice(supplierId, items);
        });
    }


 // =====================================================
 // XUẤT KHO FIFO (PRODUCT + WAREHOUSE)
 // =====================================================
    @Override
    @Transactional
    public List<ExportedInventoryItemDTO> exportInventory(InventoryExportRequestDTO request) {

        System.out.println("\n================= 🔥 EXPORT INVENTORY START 🔥 =================");
        System.out.println("Warehouse     = " + request.getWarehouse());
        System.out.println("CustomerId    = " + request.getCustomerId());
        System.out.println("Request Items = " + request.getItems());

        List<ExportedInventoryItemDTO> exportedItems = new ArrayList<>();

        System.out.println("📦 TOTAL REQUEST ITEMS = " +
                (request.getItems() == null ? "NULL" : request.getItems().size()));

        // ================== LOOP REQUEST ITEMS ==================
        for (ExportedInventoryItemDTO exportRequestItem : request.getItems()) {

            System.out.println("\n➡️ EXPORT PRODUCT");
            System.out.println("ProductId = " + exportRequestItem.getProductId());
            System.out.println("Quantity  = " + exportRequestItem.getQuantity());
            System.out.println("SellPrice = " + exportRequestItem.getSellPrice());

            // 1️⃣ LẤY INVENTORY
            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouse(
                            exportRequestItem.getProductId(),
                            request.getWarehouse()
                    )
                    .orElseThrow(() -> {
                        System.out.println("❌ INVENTORY NOT FOUND");
                        return new RuntimeException(
                                "Inventory không tồn tại cho productId = "
                                        + exportRequestItem.getProductId()
                        );
                    });

            System.out.println("✅ Inventory found: id=" + inventory.getId()
                    + ", qty=" + inventory.getQuantity());

            if (inventory.getQuantity() < exportRequestItem.getQuantity()) {
                System.out.println("❌ NOT ENOUGH STOCK");
                throw new RuntimeException(
                        "Không đủ tồn kho cho productId = " + exportRequestItem.getProductId()
                );
            }

            int need = exportRequestItem.getQuantity();
            int totalExported = 0;

            // 2️⃣ FIFO ITEMS
            List<InventoryItem> fifoItems =
                    itemRepository
                            .findByInventoryIdAndRemainingQuantityGreaterThanAndDeletedFalseOrderByReceivedDateAsc(
                                    inventory.getId(),
                                    0
                            );

            System.out.println("📦 FIFO ITEMS FOUND = " + fifoItems.size());

            for (InventoryItem item : fifoItems) {

                if (need <= 0) break;

                System.out.println("   ▶ InventoryItemId = " + item.getId()
                        + ", remaining=" + item.getRemainingQuantity());

                int available = item.getRemainingQuantity();
                int exportedQty;

                if (available <= need) {
                    exportedQty = available;
                    item.setRemainingQuantity(0);
                    item.setStatus(InventoryItemStatus.SOLD);
                } else {
                    exportedQty = need;
                    item.setRemainingQuantity(available - need);
                    item.setStatus(InventoryItemStatus.AVAILABLE);
                }

                item.setCustomerId(request.getCustomerId());
                itemRepository.save(item);

                System.out.println("   ✅ ExportedQty = " + exportedQty);

                ExportedInventoryItemDTO exportedDTO = new ExportedInventoryItemDTO();
                exportedDTO.setInventoryItemId(item.getId());
                exportedDTO.setProductId(item.getProductId());
                exportedDTO.setQuantity(exportedQty);
                exportedDTO.setSellPrice(
                        exportRequestItem.getSellPrice() != null
                                ? exportRequestItem.getSellPrice()
                                : item.getUnitPrice()
                );

                exportedItems.add(exportedDTO);

                totalExported += exportedQty;
                need -= exportedQty;
            }

            if (need > 0) {
                System.out.println("❌ FIFO NOT ENOUGH AFTER LOOP");
                throw new RuntimeException(
                        "FIFO không đủ số lượng cho productId = "
                                + exportRequestItem.getProductId()
                );
            }

            inventory.setQuantity(inventory.getQuantity() - totalExported);
            inventory.setLastExportDate(LocalDateTime.now());
            inventoryRepository.save(inventory);

            System.out.println("✅ Inventory updated: newQty=" + inventory.getQuantity());
        }

        // ================== CREATE INVOICE ==================
        System.out.println("\n================= 🧾 BEFORE CREATE INVOICE =================");
        System.out.println("ExportedItems size = " + exportedItems.size());

        if (!exportedItems.isEmpty()) {
            System.out.println("🔥 CALL invoiceService.createExportInvoice()");
            invoiceService.createExportInvoice(
                    request.getCustomerId(),
                    exportedItems
            );
            System.out.println("✅ AFTER createExportInvoice()");
        } else {
            System.out.println("⚠️ SKIP CREATE INVOICE – exportedItems EMPTY");
        }

        System.out.println("================= ✅ EXPORT INVENTORY END =================\n");
        return exportedItems;
    }


    // =====================================================
    // QUERY
    // =====================================================
    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponseDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .filter(item -> !Boolean.TRUE.equals(item.getDeleted()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponseDTO> getAvailableItems() {
        return itemRepository
                .findByRemainingQuantityGreaterThanAndDeletedFalse(0)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryItemResponseDTO getItemById(Long itemId) {
        InventoryItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        if (Boolean.TRUE.equals(item.getDeleted())) {
            throw new EntityNotFoundException("Item is deleted");
        }

        return toDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponseDTO> getItemsByCustomer(Long customerId) {
        return itemRepository.findByCustomerIdAndDeletedFalse(customerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        InventoryItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        item.setDeleted(true);
        itemRepository.save(item);
    }

    // =====================================================
    // ENTITY → DTO
    // =====================================================
    private InventoryItemResponseDTO toDTO(InventoryItem item) {

        InventoryItemResponseDTO dto = new InventoryItemResponseDTO();

        dto.setItemId(item.getId());

        inventoryRepository.findById(item.getInventoryId())
                .ifPresent(inv -> dto.setWarehouseName(inv.getWarehouse()));

        dto.setProductId(item.getProductId());
        dto.setProductName(getProductName(item.getProductId()));

        dto.setSupplierId(item.getSupplierId());
        dto.setSupplierName(getSupplierName(item.getSupplierId()));

        dto.setCustomerId(item.getCustomerId());
        dto.setBatchNumber(item.getBatchNumber());

        dto.setQuantity(item.getQuantity());
        dto.setRemainingQuantity(item.getRemainingQuantity());

        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());

        dto.setReceivedDate(item.getReceivedDate());
        dto.setExpirationDate(item.getExpirationDate());
        dto.setStatus(item.getStatus());

        return dto;
    }
    
    
    
    private String getProductName(Long productId) {
        return productId == null ? null :
                productRepository.findById(productId)
                        .map(Product::getName)
                        .orElse(null);
    }

    private String getSupplierName(Long supplierId) {
        return supplierId == null ? null :
                supplierRepository.findById(supplierId)
                        .map(Supplier::getName)
                        .orElse(null);
    }
    
}
