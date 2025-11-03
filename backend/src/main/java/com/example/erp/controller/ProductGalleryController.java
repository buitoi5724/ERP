package com.example.erp.controller;

import com.example.erp.entity.Product;
import com.example.erp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductGalleryController {

    @Autowired
    private ProductRepository productRepo;

    /**
     * API đặt ảnh đại diện chính cho sản phẩm
     */
    @PutMapping("/{id}/main-image")
    public ResponseEntity<?> setMainImage(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String imageUrl = body.get("imageUrl");

        if (imageUrl == null || imageUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Thiếu đường dẫn ảnh (imageUrl)");
        }

        Optional<Product> optProduct = productRepo.findById(id);
        if (optProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy sản phẩm");
        }

        Product product = optProduct.get();
        product.setImage(imageUrl);
        productRepo.save(product);

        System.out.println("✅ Đã đặt ảnh đại diện cho sản phẩm ID " + id + ": " + imageUrl);
        return ResponseEntity.ok("Đặt ảnh đại diện thành công!");
    }
}
