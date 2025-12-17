package com.example.erp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.erp.dto.InventoryRequestDTO;
import com.example.erp.dto.InventoryResponseDTO;
import com.example.erp.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin // Có thể cấu hình tập trung CORS
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ================= INVENTORY OPERATIONS =================

    @PostMapping("/add")
    public InventoryResponseDTO addStock(@RequestBody InventoryRequestDTO dto) {
        return inventoryService.addStock(dto);
    }

    @PostMapping("/remove")
    public InventoryResponseDTO removeStock(@RequestBody InventoryRequestDTO dto) {
        return inventoryService.removeStock(dto);
    }

    @PostMapping("/adjust")
    public InventoryResponseDTO adjustStock(@RequestBody InventoryRequestDTO dto) {
        return inventoryService.adjustStock(dto);
    }

    @PostMapping("/reserve")
    public InventoryResponseDTO reserveStock(@RequestBody InventoryRequestDTO dto) {
        return inventoryService.reserveStock(dto);
    }

    @PostMapping("/release")
    public InventoryResponseDTO releaseReserved(@RequestBody InventoryRequestDTO dto) {
        return inventoryService.releaseReserved(dto);
    }

    // ================= INVENTORY QUERY =================

    // Lấy tồn kho của 1 sản phẩm theo warehouse
    @GetMapping("/{productId}/{warehouse}")
    public InventoryResponseDTO getByProductAndWarehouse(
            @PathVariable Long productId,
            @PathVariable String warehouse) {
        return inventoryService.getByProductIdAndWarehouse(productId, warehouse);
    }

    // Lấy tất cả inventory theo product
    @GetMapping("/all/{productId}")
    public List<InventoryResponseDTO> getAllByProduct(@PathVariable Long productId) {
        return inventoryService.getAllByProductId(productId);
    }

    // Lấy tất cả sản phẩm kèm inventory (quantity = 0 nếu chưa có)
    @GetMapping("/all-with-products")
    public List<InventoryResponseDTO> getAllProductsWithInventory(
            @RequestParam(required = false) String warehouse) {
        return inventoryService.getAllProductsWithInventory(warehouse);
    }
}
