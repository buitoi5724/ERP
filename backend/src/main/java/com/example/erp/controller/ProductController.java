package com.example.erp.controller;

import com.example.erp.entity.Product;
import com.example.erp.entity.ProductCategory;
import com.example.erp.entity.ProductPrice; // Lịch sử giá
import com.example.erp.repository.ProductCategoryRepository;
import com.example.erp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal; // Làm việc với số thập phân (giá)
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime; // Ngày giờ
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products") // Tất cả API sản phẩm sẽ bắt đầu bằng /api/products
@CrossOrigin(origins = "*") // Cho phép gọi từ FE (tránh lỗi CORS)
public class ProductController {

    @Autowired
    private ProductService productService; // Gọi business/service
    @Autowired
    private ProductCategoryRepository productCategoryRepository; // Gọi DB cho Category

    private String uploadFolder = "D:/uploads/product/"; // Folder lưu ảnh


    /*
     * =================================================================================
     * DTO (Data Transfer Object) - Các lớp vận chuyển dữ liệu ra ngoài API
     * =================================================================================
     */

    // DTO: hiển thị lịch sử giá sản phẩm
    public static class PriceHistoryDTO {
        private Long id;
        private Double price;
        private boolean active;
        private LocalDateTime createdAt;

        // Map từ entity ProductPrice -> DTO
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

    // DTO: cho Product (bao gồm category + lịch sử giá)
    public static class ProductDTO {
        private Long id;
        private String name;
        private Double price;
        private String description;
        private String image;
        private CategoryDTO category;
        private List<PriceHistoryDTO> priceHistory;

        public ProductDTO(Product product) {
            this.id = product.getId();
            this.name = product.getName();
            this.price = product.getPrice();
            this.description = product.getDescription();
            this.image = product.getImage();

            // Map category
            if (product.getCategory() != null) {
                this.category = new CategoryDTO(product.getCategory());
            }

            // Map lịch sử giá
            if (product.getPriceHistory() != null) {
                this.priceHistory = product.getPriceHistory().stream()
                        .map(PriceHistoryDTO::new)
                        .collect(Collectors.toList());
            }
        }

        // Getters
        public Long getId() { return id; }
        public String getName() { return name; }
        public Double getPrice() { return price; }
        public String getDescription() { return description; }
        public String getImage() { return image; }
        public CategoryDTO getCategory() { return category; }
        public List<PriceHistoryDTO> getPriceHistory() { return priceHistory; }
    }

    // DTO: cho Category
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


    /*
     * =================================================================================
     * API ENDPOINTS
     * =================================================================================
     */

    // Lấy tất cả sản phẩm
    @GetMapping
    public List<ProductDTO> getAll() {
        return productService.getAll()
                .stream()
                .map(ProductDTO::new)
                .collect(Collectors.toList());
    }

    // Lấy sản phẩm theo id
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        return productService.getById(id)
                .map(ProductDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Lấy ảnh sản phẩm (trả về byte[])
    @GetMapping("/get-image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
        byte[] image = productService.getImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    // Tạo sản phẩm mới
    @PostMapping(consumes = {"multipart/form-data"})
    public Product create(
            @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        // Kiểm tra category
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            ProductCategory category = productCategoryRepository
                    .findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        // Xử lý ảnh nếu có
        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            Path filePath = Paths.get(uploadFolder, fileName);
            Files.createDirectories(filePath.getParent());
            image.transferTo(filePath.toFile());
            product.setImage(fileName);
        }

        return productService.save(product);
    }

    // Cập nhật sản phẩm
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product update(
            @PathVariable Long id,
            @RequestPart("product") Product updatedProduct,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        return productService.update(id, updatedProduct, image);
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
