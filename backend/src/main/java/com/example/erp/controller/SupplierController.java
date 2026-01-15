package com.example.erp.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.example.erp.dto.SupplierListDTO;
import com.example.erp.dto.SupplierRequestDTO;
import com.example.erp.dto.SupplierResponseDTO;
import com.example.erp.service.SupplierService;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }
    
    @PostMapping
    public SupplierResponseDTO create(@Valid @RequestBody SupplierRequestDTO dto) {
        return supplierService.create(dto);
    }

    @PutMapping("/{id}")
    public SupplierResponseDTO update(@PathVariable Long id,
                                      @Valid @RequestBody SupplierRequestDTO dto) {
        return supplierService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        supplierService.delete(id);
    }

    @GetMapping("/{id}")
    public SupplierResponseDTO getById(@PathVariable Long id) {
        return supplierService.getById(id);
    }

    @GetMapping
    public List<SupplierResponseDTO> getAll() {
        return supplierService.getAll();
    }
}
