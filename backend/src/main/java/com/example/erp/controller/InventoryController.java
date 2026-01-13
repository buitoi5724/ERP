package com.example.erp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.erp.dto.InventoryImportRequestDTO;
import com.example.erp.dto.InventoryRequestDTO;
import com.example.erp.dto.InventoryResponseDTO;
import com.example.erp.service.InventoryItemService;
import com.example.erp.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin
public class InventoryController {

    private final InventoryService inventoryService;
    private final InventoryItemService inventoryItemService;

    public InventoryController(
            InventoryService inventoryService,
            InventoryItemService inventoryItemService
    ) {
        this.inventoryService = inventoryService;
        this.inventoryItemService = inventoryItemService;
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

    @GetMapping("/{productId}/{warehouse}")
    public InventoryResponseDTO getByProductAndWarehouse(
            @PathVariable Long productId,
            @PathVariable String warehouse) {
        return inventoryService.getByProductIdAndWarehouse(productId, warehouse);
    }

    @GetMapping("/all/{productId}")
    public List<InventoryResponseDTO> getAllByProduct(@PathVariable Long productId) {
        return inventoryService.getAllByProductId(productId);
    }

    @GetMapping("/all-with-products")
    public List<InventoryResponseDTO> getAllProductsWithInventory(
            @RequestParam(required = false) String warehouse) {
        return inventoryService.getAllProductsWithInventory(warehouse);
    }

    // ===================== NHẬP KHO =====================
    @PostMapping("/import")
    public ResponseEntity<?> importInventory(
            @RequestBody InventoryImportRequestDTO request
    ) {
        System.out.println("REQUEST = " + request);
        System.out.println("inventoryId = " + request.getInventoryId());
        inventoryItemService.importInventory(request);
        return ResponseEntity.ok("Import inventory success");
    }
}
