package com.example.erp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.erp.dto.CategoryRequestDTO;
import com.example.erp.dto.CategoryResponseDTO;
import com.example.erp.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(
            @Valid @RequestBody CategoryRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(categoryService.getById(id));
    }
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAll() {
        List<CategoryResponseDTO> list = categoryService.getAll();
        return ResponseEntity.ok(list);
    }
}
