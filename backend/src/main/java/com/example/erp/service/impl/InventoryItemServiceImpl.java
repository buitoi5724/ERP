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

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    // THÊM 1 ITEM ĐƠN LẺ (KHÔNG PHẢI PHIẾU NHẬP)
    // =====================================================
    @Override
    public InventoryItemResponseDTO addItem(InventoryItemRequestDTO dto) {

        Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        BigDecimal unitPrice = dto.getImportPrice();
        BigDecimal totalPrice = unitPrice.multiply(
                BigDecimal.valueOf(dto.getQuantity())
        );

        InventoryItem item = new InventoryItem();
        item.setInventoryId(inventory.getId());
        
        item.setProductId(dto.getProductId());
        item.setSupplierId(dto.getSupplierId());
        item.setBatchNumber(dto.getBatchNumber());
        item.setSerialNumber(dto.getSerialNumber());
        item.setExpirationDate(dto.getExpirationDate());

        item.setImportPrice(dto.getImportPrice());
        item.setUnitPrice(dto.getImportPrice()); // nếu vẫn cần unitPrice legacy

        item.setTotalPrice(
            dto.getImportPrice()
                .multiply(BigDecimal.valueOf(dto.getQuantity()))
        );
        item.setReceivedDate(LocalDate.now());

        // ===== GIÁ =====
        item.setUnitPrice(unitPrice);
        item.setTotalPrice(totalPrice);

        item.setStatus("AVAILABLE");
        item.setDeleted(false);

        InventoryItem saved = itemRepository.save(item);

        inventory.setQuantity(inventory.getQuantity() + dto.getQuantity());
        inventoryRepository.save(inventory);

        return toDTO(saved);
    }

    // =====================================================
    // XUẤT KHO FIFO (THEO ITEM)
    // =====================================================
    @Override
    public InventoryItemResponseDTO exportItem(Long itemId, Long customerId) {

        InventoryItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("InventoryItem not found"));

        if (Boolean.TRUE.equals(item.getDeleted()) || item.getRemainingQuantity() <= 0) {
            throw new IllegalStateException("Item đã hết hàng hoặc bị xóa");
        }

        item.setRemainingQuantity(item.getRemainingQuantity() - 1);
        item.setCustomerId(customerId);

        if (item.getRemainingQuantity() == 0) {
            item.setStatus("SOLD");
        }

        InventoryItem saved = itemRepository.save(item);

        Inventory inventory = inventoryRepository.findById(item.getInventoryId())
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));

        inventory.setQuantity(inventory.getQuantity() - 1);
        inventoryRepository.save(inventory);

        return toDTO(saved);
    }

    // =====================================================
    // NHẬP KHO THEO PHIẾU NHẬP (CHUẨN ERP)
    // =====================================================
    @Override
    public void importInventory(InventoryImportRequestDTO request) {
        Inventory inventory;

        // ==== Tạo mới inventory nếu không có ID ====
        if (request.getInventoryId() == null) {
            inventory = new Inventory();
            inventory.setReceiptCode(request.getReceiptCode());
            inventory.setWarehouse(request.getWarehouse());
            inventory.setQuantity(0);
            inventory.setCreatedDate(LocalDate.now());
            inventory.setUpdatedDate(LocalDate.now());
            inventory.setStatus("ACTIVE");
            inventoryRepository.save(inventory); // lưu trước để sinh ID
        } else {
            inventory = inventoryRepository.findById(request.getInventoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;

        // ==== Lưu từng item ====
        for (InventoryItemRequestDTO itemDto : request.getItems()) {
            InventoryItem item = new InventoryItem();
            item.setInventoryId(inventory.getId());
            item.setProductId(itemDto.getProductId());
            item.setSupplierId(itemDto.getSupplierId());
            item.setBatchNumber(itemDto.getBatchNumber());
            item.setQuantity(itemDto.getQuantity());
            item.setRemainingQuantity(itemDto.getQuantity());
            item.setUnitPrice(itemDto.getImportPrice());
            item.setTotalPrice(itemDto.getImportPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
            item.setImportPrice(itemDto.getImportPrice());
            item.setStatus("AVAILABLE");
            item.setDeleted(false);

         // ==== Ngày nhập (receivedDate) ====
            if (itemDto.getReceivedDate() != null) {
                item.setReceivedDate(itemDto.getReceivedDate()); // trực tiếp từ item
            } else if (request.getDate() != null) {
                // request.getDate() là String, parse sang LocalDate
                item.setReceivedDate(LocalDate.parse(request.getDate()));
            } else {
                item.setReceivedDate(LocalDate.now()); // fallback mặc định
            }

            // ==== Ngày sản xuất (manufactureDate) ====
            item.setManufactureDate(itemDto.getManufactureDate()); // nếu null cũng ok, entity sẽ lưu null

            // ==== Hạn sử dụng (expirationDate) ====
            item.setExpirationDate(itemDto.getExpirationDate()); // nếu null cũng ok

            itemRepository.save(item);

            totalQuantity += itemDto.getQuantity();
            totalAmount = totalAmount.add(item.getTotalPrice());
        }

        // ==== Cập nhật tổng số lượng kho và tổng tiền ====
        inventory.setQuantity(inventory.getQuantity() + totalQuantity);
        inventory.setUpdatedDate(LocalDate.now());
        inventoryRepository.save(inventory);

        // ==== Cập nhật tổng tiền cho request (DTO) ====
        request.setTotalAmount(totalAmount);
    }

    @Override
    @Transactional
    public void exportInventory(InventoryExportRequestDTO request) {

        for (ExportItemDTO exportItem : request.getItems()) {

            int need = exportItem.getQuantity();

            List<InventoryItem> items =
                itemRepository.findFIFOByWarehouseAndProduct(
                    request.getWarehouse(),
                    exportItem.getProductId()
                );

            if (items.isEmpty()) {
                throw new IllegalStateException(
                    "Kho " + request.getWarehouse()
                    + " không còn sản phẩm " + exportItem.getProductId()
                );
            }

            for (InventoryItem item : items) {
                if (need <= 0) break;

                int available = item.getRemainingQuantity();

                if (available <= need) {
                    item.setRemainingQuantity(0);
                    item.setStatus("SOLD");
                    need -= available;
                } else {
                    item.setRemainingQuantity(available - need);
                    need = 0;
                }

                item.setCustomerId(request.getCustomerId());
                itemRepository.save(item);
            }

            if (need > 0) {
                throw new IllegalStateException(
                    "Không đủ tồn cho sản phẩm " + exportItem.getProductId()
                );
            }
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
        dto.setInventoryId(item.getInventoryId());
        dto.setWarehouseName(getWarehouseName(item.getInventoryId()));

        dto.setProductId(item.getProductId());
        dto.setProductName(getProductName(item.getProductId()));

        dto.setSupplierId(item.getSupplierId());
        dto.setSupplierName(getSupplierName(item.getSupplierId()));

        dto.setCustomerId(item.getCustomerId());
        dto.setBatchNumber(item.getBatchNumber());
        dto.setSerialNumber(item.getSerialNumber());

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

    private String getWarehouseName(Long inventoryId) {
        return inventoryId == null ? null :
                inventoryRepository.findById(inventoryId)
                        .map(Inventory::getWarehouse)
                        .orElse(null);
    }
}
