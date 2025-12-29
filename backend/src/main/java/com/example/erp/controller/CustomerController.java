package com.example.erp.controller;

import com.example.erp.dto.CustomerRequestDTO;
import com.example.erp.dto.CustomerResponseDTO;
import com.example.erp.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public CustomerResponseDTO create(@RequestBody CustomerRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CustomerResponseDTO update(@PathVariable Long id, @RequestBody CustomerRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public CustomerResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<CustomerResponseDTO> getAll() {
        return service.getAll();
    }
}