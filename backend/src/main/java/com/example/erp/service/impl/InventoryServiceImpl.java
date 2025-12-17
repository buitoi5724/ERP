package com.example.erp.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.erp.dto.InventoryRequestDTO;
import com.example.erp.dto.InventoryResponseDTO;
import com.example.erp.entity.Inventory;
import com.example.erp.entity.InventoryLog;
import com.example.erp.entity.Product;
import com.example.erp.repository.InventoryLogRepository;
import com.example.erp.repository.InventoryRepository;
import com.example.erp.repository.ProductRepository;
import com.example.erp.service.InventoryService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository logRepository;
    private final ProductRepository productRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository,
                                InventoryLogRepository logRepository,
                                ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.logRepository = logRepository;
        this.productRepository = productRepository;
    }
    // =========================================================
    // HELPER: FIND OR CREATE INVENTORY (SNAPSHOT PRODUCT)
    // =========================================================
    private Inventory findOrCreate(Long productId, String warehouse) {
        return inventoryRepository.findByProductIdAndWarehouse(productId, warehouse)
            .orElseGet(() -> {

                Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));

                Inventory inv = new Inventory();
                inv.setProductId(productId);
                inv.setProductCode(product.getCode());
                inv.setProductName(product.getName());

                inv.setSalePrice(product.getPrice());   // giá bán
                inv.setCostPrice(product.getPrice());   // hoặc null nếu chưa nhập

                inv.setWarehouse(warehouse);
                inv.setQuantity(0);
                inv.setReservedQuantity(0);

                inv.setMinStock(0);
                inv.setMaxStock(1000); // default
                inv.setStatus("ACTIVE");
                inv.setNote(null);

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

    // =========================================================
    // MAPPING ENTITY -> DTO (FULL COLUMNS)
    // =========================================================
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

    // =========================================================
    // INVENTORY OPERATIONS
    // =========================================================
    @Override
    public InventoryResponseDTO addStock(InventoryRequestDTO dto) {
        Inventory inv = findOrCreate(dto.getProductId(), dto.getWarehouse());

        // SET COST PRICE WHEN IMPORT
        if (dto.getCostPrice() != null) {
            inv.setCostPrice(dto.getCostPrice());
        }

        inv.setQuantity(inv.getQuantity() + dto.getQuantity());
        inv.setLastImportDate(LocalDateTime.now());

        createLog(inv, dto.getQuantity(), "IN");
        return toDTO(inv);
    }

    @Override
    public InventoryResponseDTO removeStock(InventoryRequestDTO dto) {
        Inventory inv = findOrCreate(dto.getProductId(), dto.getWarehouse());

        int qty = Math.min(
            dto.getQuantity(),
            inv.getQuantity() - inv.getReservedQuantity()
        );

        inv.setQuantity(inv.getQuantity() - qty);
        inv.setLastExportDate(LocalDateTime.now());

        createLog(inv, -qty, "OUT");
        return toDTO(inv);
    }

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

    // =========================================================
    // QUERY
    // =========================================================
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

    // =========================================================
    // INITIAL INVENTORY
    // =========================================================
    @Override
    public InventoryResponseDTO importInitialStock(Long productId, String warehouse, int quantity) {
        Inventory inv = findOrCreate(productId, warehouse);

        inv.setQuantity(inv.getQuantity() + quantity);
        inv.setLastImportDate(LocalDateTime.now());

        createLog(inv, quantity, "INT");
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

                    // SNAPSHOT
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

    // =========================================================
    // ALL PRODUCTS + INVENTORY
    // =========================================================
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

                // 🔥 LUÔN LẤY TỪ PRODUCT
                dto.setProductName(product.getName());
                dto.setProductCode(product.getCode());
                dto.setSalePrice(product.getPrice()); // ✅ ĐÚNG

                if (inv != null) {
                    dto.setQuantity(inv.getQuantity());
                    dto.setReservedQuantity(inv.getReservedQuantity());
                    dto.setAvailableQuantity(inv.getAvailableQuantity());
                   
                    dto.setInventoryValue(inv.getInventoryValue());
                    dto.setCostPrice(inv.getCostPrice()); // ✅ GIÁ NHẬP
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
