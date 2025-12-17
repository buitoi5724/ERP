package com.example.erp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.erp.dto.ProductGalleryBatchRequestDTO;
import com.example.erp.dto.ProductGalleryResponseDTO;
import com.example.erp.service.ProductGalleryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/galleries")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductGalleryController {

    private final ProductGalleryService galleryService;

    public ProductGalleryController(ProductGalleryService galleryService) {
        this.galleryService = galleryService;
    }

    /* ================= CREATE MULTIPLE IMAGES ================= */
    @PostMapping("/batch")
    public ResponseEntity<List<ProductGalleryResponseDTO>> createBatch(
            @Valid @RequestBody ProductGalleryBatchRequestDTO dto) {

        List<ProductGalleryResponseDTO> result = galleryService.createBatch(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /* ================= GET ALL IMAGES BY PRODUCT ================= */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductGalleryResponseDTO>> getByProduct(
            @PathVariable Long productId) {

        return ResponseEntity.ok(galleryService.getByProductId(productId));
    }

    /* ================= SET MAIN IMAGE ================= */
    @PutMapping("/product/{productId}/main-image")
    public ResponseEntity<?> setMainImage(
            @PathVariable Long productId,
            @RequestBody Map<String, String> body) {

        String imageUrl = body.get("imageUrl");
        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Thiếu đường dẫn ảnh (imageUrl)");
        }

        galleryService.setMainImage(productId, imageUrl);
        return ResponseEntity.ok("Đặt ảnh đại diện thành công!");
    }

    /* ================= REMOVE MAIN IMAGE ================= */
    @DeleteMapping("/product/{productId}/main-image")
    public ResponseEntity<?> removeMainImage(@PathVariable Long productId) {
        galleryService.removeMainImage(productId);
        return ResponseEntity.ok("Xóa ảnh đại diện thành công!");
    }

    /* ================= DELETE SINGLE IMAGE ================= */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        galleryService.deleteImage(imageId);
        return ResponseEntity.ok("Xóa ảnh thành công!");
    }
}
