package com.example.erp.service.impl;

import com.example.erp.dto.ExportItemDTO;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public InventoryItemServiceImpl(
            InventoryItemRepository itemRepository,
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository
    ) {
        this.itemRepository = itemRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
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
    public void importInventory(InventoryImportRequestDTO request) {

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (InventoryItemRequestDTO itemDto : request.getItems()) {

            // 1️⃣ LẤY / TẠO INVENTORY
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
                        
                        
                        productRepository.findById(itemDto.getProductId())
                                .ifPresent(p -> {
                                    inv.setProductCode(p.getCode());
                                    inv.setProductName(p.getName());
                                });

                        return inventoryRepository.save(inv);
                    });

            // 2️⃣ TẠO INVENTORY ITEM
            InventoryItem item = new InventoryItem();
            item.setInventoryId(inventory.getId());
            item.setProductId(itemDto.getProductId());
            item.setSupplierId(itemDto.getSupplierId());
            item.setBatchNumber(itemDto.getBatchNumber());

            item.setQuantity(itemDto.getQuantity());
            item.setRemainingQuantity(itemDto.getQuantity());
            item.setImportPrice(itemDto.getImportPrice());
            item.setUnitPrice(itemDto.getImportPrice());
            item.setTotalPrice(
                    itemDto.getImportPrice()
                            .multiply(BigDecimal.valueOf(itemDto.getQuantity()))
            );

            item.setManufactureDate(itemDto.getManufactureDate());
            item.setExpirationDate(itemDto.getExpirationDate());
            item.setReceivedDate(
                    request.getDate() != null
                            ? LocalDate.parse(request.getDate())
                            : LocalDate.now()
            );

            item.setStatus(InventoryItemStatus.AVAILABLE);
            item.setDeleted(false);

            itemRepository.save(item);

            
            // 3️⃣ UPDATE INVENTORY
            inventory.setQuantity(
                    inventory.getQuantity() + itemDto.getQuantity()
            );
            inventory.setLastImportDate(LocalDateTime.now());
            inventoryRepository.save(inventory);

            totalAmount = totalAmount.add(item.getTotalPrice());
        }

        request.setTotalAmount(totalAmount);
    }

 // =====================================================
 // XUẤT KHO FIFO (PRODUCT + WAREHOUSE)
 // =====================================================
    @Transactional
    public void exportInventory(InventoryExportRequestDTO request) {

        for (ExportItemDTO exportItem : request.getItems()) {

            // 1️⃣ INVENTORY (PRODUCT + WAREHOUSE)
            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouse(
                            exportItem.getProductId(),
                            request.getWarehouse()
                    )
                    .orElseThrow(() ->
                            new RuntimeException("Inventory không tồn tại")
                    );

            if (inventory.getQuantity() < exportItem.getQuantity()) {
                throw new RuntimeException(
                        "Không đủ tồn kho cho sản phẩm " + exportItem.getProductId()
                );
            }

            int need = exportItem.getQuantity();
            int totalExported = 0;

            // 2️⃣ FIFO CHUẨN THEO INVENTORY ID
            List<InventoryItem> fifoItems =
                    itemRepository
                            .findByInventoryIdAndRemainingQuantityGreaterThanAndDeletedFalseOrderByReceivedDateAsc(
                                    inventory.getId(),
                                    0
                            );

            for (InventoryItem item : fifoItems) {
                if (need <= 0) break;

                int available = item.getRemainingQuantity();

                if (available <= need) {
                    // BÁN HẾT LÔ
                    item.setRemainingQuantity(0);
                    item.setStatus(InventoryItemStatus.SOLD);

                    totalExported += available;
                    need -= available;

                } else {
                    // BÁN 1 PHẦN
                    item.setRemainingQuantity(available - need);
                    item.setStatus(InventoryItemStatus.AVAILABLE); // 🔥 BẮT BUỘC

                    totalExported += need;
                    need = 0;
                }

                item.setCustomerId(request.getCustomerId());
                itemRepository.save(item);
            }

            if (need > 0) {
                throw new RuntimeException(
                        "FIFO item không đủ cho sản phẩm " + exportItem.getProductId()
                );
            }

            // 3️⃣ 🔥 TRỪ TỒN TỔNG (FRONTEND ĐỌC)
            inventory.setQuantity(
                    inventory.getQuantity() - totalExported
            );
            inventory.setLastExportDate(LocalDateTime.now());

            inventoryRepository.save(inventory);
        }
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
