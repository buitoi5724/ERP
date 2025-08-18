package com.example.erp.controller;

import com.example.erp.entity.Product;
import com.example.erp.entity.ProductCategory;
import com.example.erp.entity.ProductPrice; // <-- Thêm import này
import com.example.erp.repository.ProductCategoryRepository;
import com.example.erp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal; // <-- Thêm import này
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime; // <-- Thêm import này
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    private String uploadFolder = "D:/uploads/product/";


    /*
     * =================================================================================
     * DTO (Data Transfer Object) - CÁC LỚP "VẬN CHUYỂN DỮ LIỆU"
     * =================================================================================
     */

    // <-- BẮT ĐẦU PHẦN CẬP NHẬT CHO BƯỚC 1.4

    // 1. DTO MỚI ĐỂ HIỂN THỊ LỊCH SỬ GIÁ
    public static class PriceHistoryDTO {
        private Long id;
        private Double price;
        private boolean active;
        private LocalDateTime createdAt;

        // Constructor để chuyển đổi từ Entity sang DTO
        public PriceHistoryDTO(ProductPrice priceEntity) {
            this.id = priceEntity.getId();
            this.price = priceEntity.getPrice();
            this.active = priceEntity.isActive();
            this.createdAt = priceEntity.getCreatedAt();
        }

        // Getters
        public Long getId() { return id; }
        public Double getPrice() { return price; }
        public boolean isActive() { return active; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    // 2. CẬP NHẬT LẠI PRODUCTDTO ĐỂ CHỨA DANH SÁCH LỊCH SỬ GIÁ
    public static class ProductDTO {
        private Long id;
        private String name;
        private Double price;
        private String description;
        private String image;
        private CategoryDTO category;
        private List<PriceHistoryDTO> priceHistory; // <-- THÊM TRƯỜNG MỚI NÀY

        public ProductDTO(Product product) {
            this.id = product.getId();
            this.name = product.getName();
            this.price = product.getPrice();
            this.description = product.getDescription();
            this.image = product.getImage();
            if (product.getCategory() != null) {
                this.category = new CategoryDTO(product.getCategory());
            }

            // --- THÊM LOGIC MỚI ĐỂ CHUYỂN ĐỔI LỊCH SỬ GIÁ ---
            if (product.getPriceHistory() != null) {
                this.priceHistory = product.getPriceHistory().stream()
                        .map(PriceHistoryDTO::new) // Với mỗi ProductPrice, tạo một PriceHistoryDTO
                        .collect(Collectors.toList());
            }
        }

        // Getters cũ giữ nguyên
        public Long getId() { return id; }
        public String getName() { return name; }
        public Double getPrice() { return price; }
        public String getDescription() { return description; }
        public String getImage() { return image; }
        public CategoryDTO getCategory() { return category; }
        
        // <-- THÊM GETTER MỚI NÀY
        public List<PriceHistoryDTO> getPriceHistory() { return priceHistory; }
    }

    // <-- KẾT THÚC PHẦN CẬP NHẬT

    // CategoryDTO giữ nguyên, không thay đổi
    public static class CategoryDTO {
        private Long id;
        private String name;

        public CategoryDTO(ProductCategory category) {
            this.id = category.getId();
            this.name = category.getName();
        }
        public Long getId() { return id; }
        public String getName() { return name; }
    }


    // ---------------------------------------------------------------------------------
    // CÁC NHIỆM VỤ CỤ THỂ (API ENDPOINTS) - KHÔNG CẦN THAY ĐỔI GÌ Ở ĐÂY
    // ---------------------------------------------------------------------------------

    @GetMapping
    public List<ProductDTO> getAll() {
        return productService.getAll()
                .stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        return productService.getById(id)
                .map(ProductDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/get-image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
        byte[] image = productService.getImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public Product create(
            @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        // ... code giữ nguyên
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            ProductCategory category = productCategoryRepository
                    .findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            Path filePath = Paths.get(uploadFolder, fileName);
            Files.createDirectories(filePath.getParent());
            image.transferTo(filePath.toFile());
            product.setImage(fileName);
        }

        return productService.save(product);
    }
 // Trong file: ProductController.java

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product update(
            @PathVariable Long id,
            @RequestPart("product") Product updatedProduct,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        // ĐÃ SỬA: Gọi phương thức mới của service (chỉ có 3 tham số)
        return productService.update(id, updatedProduct, image);
    }
    

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}