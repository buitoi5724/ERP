package com.example.erp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.erp.dto.ProductRequestDTO;
import com.example.erp.dto.ProductResponseDTO;
import com.example.erp.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /* ================= CREATE ================= */

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ProductResponseDTO> create(
            @RequestPart("product") @Valid ProductRequestDTO dto,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {

        ProductResponseDTO response = productService.create(dto, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    /* ================= UPDATE ================= */


@PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
public ResponseEntity<ProductResponseDTO> update(
        @PathVariable Long id,
        @RequestPart("product") @Valid ProductRequestDTO dto,
        @RequestPart(value = "images", required = false) MultipartFile[] images) {

    ProductResponseDTO response = productService.update(id, dto, images);
    return ResponseEntity.ok(response);
}

    /* ================= GET BY ID ================= */

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(
            @PathVariable Long id) {

        ProductResponseDTO response = productService.getById(id);
        return ResponseEntity.ok(response);
    }
    /* ================= GET ALL ================= */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    @PutMapping("/{id}/main-image")
    public ResponseEntity<ProductResponseDTO> updateMainImage(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        String imageUrl = payload.get("imageUrl");
        ProductResponseDTO updated = productService.updateMainImage(id, imageUrl);
        return ResponseEntity.ok(updated);
    }
    /* ================= DELETE ================= */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
