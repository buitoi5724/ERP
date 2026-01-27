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
import com.example.erp.util.InventoryStatus;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository logRepository;
    private final ProductRepository productRepository;
    private final InventoryItemService inventoryItemService;

    public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            InventoryLogRepository logRepository,
            ProductRepository productRepository,
            InventoryItemService inventoryItemService) {
        this.inventoryRepository = inventoryRepository;
        this.logRepository = logRepository;
        this.productRepository = productRepository;
        this.inventoryItemService = inventoryItemService;
    }

    /* =====================================================
     * HELPER: TÌM HOẶC TẠO INVENTORY
     * ===================================================== */
    private Inventory findOrCreate(Long productId, String warehouse) {

        return inventoryRepository
            .findByProductIdAndWarehouse(productId, warehouse)
            .orElseGet(() -> {

                Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

                Inventory inv = new Inventory();
                inv.setProductId(productId);
                inv.setProductCode(product.getCode());
                inv.setProductName(product.getName());

                // ✅ GIÁ BÁN lấy từ Product
                inv.setSalePrice(product.getPrice());

                // ❌ KHÔNG set costPrice = price
                inv.setCostPrice(null);

                inv.setWarehouse(warehouse);
                inv.setQuantity(0);
                inv.setReservedQuantity(0);
                inv.setStatus(InventoryStatus.ACTIVE);

                return inventoryRepository.save(inv);
            });
    }

    /* =====================================================
     * LOG
     * ===================================================== */
    private void createLog(Inventory inv, int changeQty, String type) {
        InventoryLog log = new InventoryLog(
            inv.getProductId(),
            changeQty,
            type,
            inv.getWarehouse()
        );
        logRepository.save(log);
    }

    /* =====================================================
     * MAP ENTITY → DTO
     * ===================================================== */
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
        dto.setStatus(inv.getStatus());
        dto.setNote(inv.getNote());
        dto.setCreatedDate(inv.getCreatedDate());
        dto.setUpdatedDate(inv.getUpdatedDate());
        dto.setLastImportDate(inv.getLastImportDate());
        dto.setLastExportDate(inv.getLastExportDate());
        return dto;
    }

    /* =====================================================
     * NHẬP KHO
     * ===================================================== */
    @Override
    public InventoryResponseDTO addStock(InventoryRequestDTO dto) {

        Inventory inv = findOrCreate(dto.getProductId(), dto.getWarehouse());

        // ✅ giá vốn lấy từ phiếu nhập
        if (dto.getCostPrice() != null) {
            inv.setCostPrice(dto.getCostPrice());
        }

        inv.setQuantity(inv.getQuantity() + dto.getQuantity());
        inv.setLastImportDate(LocalDateTime.now());

        createLog(inv, dto.getQuantity(), "IN");

        // ✅ TẠO INVENTORY_ITEM (CHI TIẾT LÔ)
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

    /* =====================================================
     * XUẤT KHO
     * ===================================================== */
    @Override
    public InventoryResponseDTO removeStock(InventoryRequestDTO dto) {

        Inventory inv = findOrCreate(dto.getProductId(), dto.getWarehouse());

        int available = inv.getQuantity() - inv.getReservedQuantity();
        int exportQty = Math.min(dto.getQuantity(), available);

        inv.setQuantity(inv.getQuantity() - exportQty);
        inv.setLastExportDate(LocalDateTime.now());

        createLog(inv, -exportQty, "OUT");

        return toDTO(inv);
    }

    /* =====================================================
     * ĐIỀU CHỈNH TỒN
     * ===================================================== */
    @Override
    public InventoryResponseDTO adjustStock(InventoryRequestDTO dto) {

        Inventory inv = findOrCreate(dto.getProductId(), dto.getWarehouse());

        int diff = dto.getQuantity() - inv.getQuantity();
        inv.setQuantity(dto.getQuantity());

        createLog(inv, diff, "ADJUST");

        return toDTO(inv);
    }

    /* =====================================================
     * GIỮ HÀNG
     * ===================================================== */
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

    /* =====================================================
     * QUERY
     * ===================================================== */
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

    /* =====================================================
     * KHỞI TẠO TỒN BAN ĐẦU
     * ===================================================== */
    @Override
    public InventoryResponseDTO importInitialStock(Long productId, String warehouse, int quantity) {

        Inventory inv = findOrCreate(productId, warehouse);

        inv.setQuantity(inv.getQuantity() + quantity);
        inv.setLastImportDate(LocalDateTime.now());

        createLog(inv, quantity, "INIT");

        InventoryItemRequestDTO itemDTO = new InventoryItemRequestDTO();
        itemDTO.setInventoryId(inv.getId());
        itemDTO.setQuantity(quantity);

        inventoryItemService.addItem(itemDTO);

        return toDTO(inv);
    }

    /* =====================================================
     * INVENTORY PAGE
     * ===================================================== */
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
                dto.setProductCode(product.getCode());
                dto.setProductName(product.getName());
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

	@Override
	public void initializeInventoryForAllProducts(String warehouse) {
		// TODO Auto-generated method stub
		
	}
}
