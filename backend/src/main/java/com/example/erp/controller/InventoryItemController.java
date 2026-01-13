package com.example.erp.controller;

import com.example.erp.dto.InventoryItemRequestDTO;
import com.example.erp.dto.InventoryItemResponseDTO;
import com.example.erp.service.InventoryItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-items")

public class InventoryItemController {

    private final InventoryItemService service;

    public InventoryItemController(InventoryItemService service) {
        this.service = service;
    }

    @PostMapping
    public InventoryItemResponseDTO addItem(@RequestBody InventoryItemRequestDTO dto) {
        return service.addItem(dto);
    }

    @PostMapping("/{id}/export/{customerId}")
    public InventoryItemResponseDTO exportItem(@PathVariable Long id, @PathVariable Long customerId) {
        return service.exportItem(id, customerId);
    }

    @GetMapping
    public List<InventoryItemResponseDTO> getAll() {
        return service.getAllItems();
    }

    @GetMapping("/available")
    public List<InventoryItemResponseDTO> getAvailable() {
        return service.getAvailableItems();
    }

    @GetMapping("/{id}")
    public InventoryItemResponseDTO getById(@PathVariable Long id) {
        return service.getItemById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<InventoryItemResponseDTO> getByCustomer(@PathVariable Long customerId) {
        return service.getItemsByCustomer(customerId);
    }
    
}
