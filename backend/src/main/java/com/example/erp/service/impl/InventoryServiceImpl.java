package com.example.erp.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.dto.InventoryItemRequestDTO;
import com.example.erp.dto.InventoryRequestDTO;
import com.example.erp.dto.InventoryResponseDTO;
import com.example.erp.entity.Inventory;
import com.example.erp.entity.InventoryLog;
import com.example.erp.entity.Product;
import com.example.erp.repository.InventoryLogRepository;
import com.example.erp.repository.InventoryRepository;
import com.example.erp.repository.ProductRepository;
import com.example.erp.service.InventoryItemService;
import com.example.erp.service.InventoryService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository logRepository;
    private final ProductRepository productRepository;
    private final InventoryItemService inventoryItemService;

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                InventoryLogRepository logRepository,
                                ProductRepository productRepository,
                                InventoryItemService inventoryItemService) {
        this.inventoryRepository = inventoryRepository;
        this.logRepository = logRepository;
        this.productRepository = productRepository;
        this.inventoryItemService = inventoryItemService;
    }

    // ================== HELPER ==================
    private Inventory findOrCreate(Long productId, String warehouse) {
        return inventoryRepository.findByProductIdAndWarehouse(productId, warehouse)
            .orElseGet(() -> {
                Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

                Inventory inv = new Inventory();
                inv.setProductId(productId);
                inv.setProductCode(product.getCode());
                inv.setProductName(product.getName());

                inv.setSalePrice(product.getPrice());
                inv.setCostPrice(product.getPrice());

                inv.setWarehouse(warehouse);
                inv.setQuantity(0);
                inv.setReservedQuantity(0);

                inv.setMinStock(0);
                inv.setMaxStock(1000);
                inv.setStatus("ACTIVE");

                return inventoryRepository.save(inv);
            });
    }

    private void createLog(Inventory inv, int changeQty, String type) {
        InventoryLog log = new InventoryLog(
            inv.getProductId(),
            changeQty,
            type,
            inv.getWarehouse()
        );
        logRepository.save(log);
    }

    private InventoryResponseDTO toDTO(Inventory inv) {
        InventoryResponseDTO dto = new InventoryResponseDTO();
        dto.setProductId(inv.getProductId());
        dto.setProductCode(inv.getProductCode());
        dto.setProductName(inv.getProductName());
        dto.setQuantity(inv.getQuantity());
        dto.setReservedQuantity(inv.getReservedQuantity());
        dto.setAvailableQuantity(inv.getAvailableQuantity());
        dto.setCostPrice(inv.getCostPrice());
        dto.setSalePrice(inv.getSalePrice());
        dto.setInventoryValue(inv.getInventoryValue());
        dto.setWarehouse(inv.getWarehouse());
        dto.setMinStock(inv.getMinStock());
        dto.setMaxStock(inv.getMaxStock());
        dto.setStatus(inv.getStatus());
        dto.setNote(inv.getNote());
        dto.setCreatedDate(inv.getCreatedDate());
        dto.setUpdatedDate(inv.getUpdatedDate());
        dto.setLastImportDate(inv.getLastImportDate());
        dto.setLastExportDate(inv.getLastExportDate());
        return dto;
    }

    // ================== NHẬP KHO (AUTO TẠO INVENTORY_ITEM) ==================
    @Override
    public InventoryResponseDTO addStock(InventoryRequestDTO dto) {
        Inventory inv = findOrCreate(dto.getProductId(), dto.getWarehouse());

        if (dto.getCostPrice() != null) {
            inv.setCostPrice(dto.getCostPrice());
        }

        inv.setQuantity(inv.getQuantity() + dto.getQuantity());
        inv.setLastImportDate(LocalDateTime.now());

        createLog(inv, dto.getQuantity(), "IN");

        // ================== TẠO INVENTORY_ITEM MỖI LẦN NHẬP KHO ==================
        InventoryItemRequestDTO itemDTO = new InventoryItemRequestDTO();
        itemDTO.setInventoryId(inv.getId());
        itemDTO.setSupplierId(dto.getSupplierId());
        itemDTO.setQuantity(dto.getQuantity());
        itemDTO.setBatchNumber(dto.getBatchNumber());
        itemDTO.setSerialNumber(dto.getSerialNumber());
        itemDTO.setExpirationDate(dto.getExpirationDate());

        inventoryItemService.addItem(itemDTO);

        return toDTO(inv);
    }

    // ================== XUẤT KHO ==================
    @Override
    public InventoryResponseDTO removeStock(InventoryRequestDTO dto) {
        Inventory inv = findOrCreate(dto.getProductId(), dto.getWarehouse());

        int qty = Math.min(dto.getQuantity(), inv.getQuantity() - inv.getReservedQuantity());
        inv.setQuantity(inv.getQuantity() - qty);
        inv.setLastExportDate(LocalDateTime.now());

        createLog(inv, -qty, "OUT");

        return toDTO(inv);
    }

    // ================== CÁC CHỨC NĂNG KHÁC GIỮ NGUYÊN ==================
    @Override
    public InventoryResponseDTO adjustStock(InventoryRequestDTO dto) {
        Inventory inv = findOrCreate(dto.getProductId(), dto.getWarehouse());
        int diff = dto.getQuantity() - inv.getQuantity();
        inv.setQuantity(dto.getQuantity());
        createLog(inv, diff, "ADJUST");
        return toDTO(inv);
    }

    @Override
    public InventoryResponseDTO reserveStock(InventoryRequestDTO dto) {
        Inventory inv = findOrCreate(dto.getProductId(), dto.getWarehouse());
        int available = inv.getQuantity() - inv.getReservedQuantity();
        int reserveQty = Math.min(dto.getQuantity(), available);
        inv.setReservedQuantity(inv.getReservedQuantity() + reserveQty);
        createLog(inv, reserveQty, "RESERVE");
        return toDTO(inv);
    }

    @Override
    public InventoryResponseDTO releaseReserved(InventoryRequestDTO dto) {
        Inventory inv = findOrCreate(dto.getProductId(), dto.getWarehouse());
        int releaseQty = Math.min(dto.getQuantity(), inv.getReservedQuantity());
        inv.setReservedQuantity(inv.getReservedQuantity() - releaseQty);
        createLog(inv, -releaseQty, "RELEASE");
        return toDTO(inv);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDTO getByProductIdAndWarehouse(Long productId, String warehouse) {
        Inventory inv = inventoryRepository
            .findByProductIdAndWarehouse(productId, warehouse)
            .orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
        return toDTO(inv);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getAllByProductId(Long productId) {
        return inventoryRepository.findAll().stream()
            .filter(inv -> inv.getProductId().equals(productId))
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public InventoryResponseDTO importInitialStock(Long productId, String warehouse, int quantity) {
        Inventory inv = findOrCreate(productId, warehouse);
        inv.setQuantity(inv.getQuantity() + quantity);
        inv.setLastImportDate(LocalDateTime.now());
        createLog(inv, quantity, "INT");

        // Tạo InventoryItem cho initial stock
        InventoryItemRequestDTO itemDTO = new InventoryItemRequestDTO();
        itemDTO.setInventoryId(inv.getId());
        itemDTO.setQuantity(quantity);
        inventoryItemService.addItem(itemDTO);

        return toDTO(inv);
    }

    @Override
    public void initializeInventoryForAllProducts(String warehouse) {
        List<Product> products = productRepository.findAll();
        for (Product p : products) {
            inventoryRepository.findByProductIdAndWarehouse(p.getId(), warehouse)
                .orElseGet(() -> {
                    Inventory inv = new Inventory();
                    inv.setProductId(p.getId());
                    inv.setProductCode(p.getCode());
                    inv.setProductName(p.getName());
                    inv.setSalePrice(p.getPrice());
                    inv.setWarehouse(warehouse);
                    inv.setQuantity(0);
                    inv.setReservedQuantity(0);
                    inv.setMinStock(0);
                    inv.setStatus("ACTIVE");
                    return inventoryRepository.save(inv);
                });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> getAllProductsWithInventory(String warehouse) {
        return productRepository.findAll().stream()
            .map(product -> {
                Inventory inv = inventoryRepository
                    .findByProductIdAndWarehouse(product.getId(), warehouse)
                    .orElse(null);

                InventoryResponseDTO dto = new InventoryResponseDTO();
                dto.setProductId(product.getId());
                dto.setProductName(product.getName());
                dto.setProductCode(product.getCode());
                dto.setSalePrice(product.getPrice());

                if (inv != null) {
                    dto.setQuantity(inv.getQuantity());
                    dto.setReservedQuantity(inv.getReservedQuantity());
                    dto.setAvailableQuantity(inv.getAvailableQuantity());
                    dto.setInventoryValue(inv.getInventoryValue());
                    dto.setCostPrice(inv.getCostPrice());
                    dto.setWarehouse(inv.getWarehouse());
                    dto.setLastImportDate(inv.getLastImportDate());
                    dto.setLastExportDate(inv.getLastExportDate());
                } else {
                    dto.setQuantity(0);
                    dto.setReservedQuantity(0);
                    dto.setAvailableQuantity(0);
                    dto.setWarehouse(warehouse);
                }

                return dto;
            })
            .collect(Collectors.toList());
    }
    
}
