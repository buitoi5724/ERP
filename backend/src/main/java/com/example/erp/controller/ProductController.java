package com.example.erp.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import com.example.erp.entity.Product;
import com.example.erp.entity.ProductCategory;
import com.example.erp.entity.ProductGallery;
import com.example.erp.entity.ProductPrice;
import com.example.erp.repository.ProductCategoryRepository;
import com.example.erp.service.ProductService;
import com.example.erp.service.ProductPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductPriceService productPriceService;

    private String uploadFolder = "D:/uploads/product/";

    // =================================================================================
    // DTO CLASSES
    // =================================================================================

    public static class PriceHistoryDTO {
        private int stt;
        private Long id;
        private Double price;
        private boolean active;
        private LocalDateTime createdAt;

        public PriceHistoryDTO(ProductPrice priceEntity, int stt) {
            this.stt = stt;
            this.id = priceEntity.getId();
            this.price = priceEntity.getPrice();
            this.active = priceEntity.isActive();
            this.createdAt = priceEntity.getCreatedAt();
        }

        public int getStt() { return stt; }
        public Long getId() { return id; }
        public Double getPrice() { return price; }
        public boolean isActive() { return active; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    public static class ProductDTO {
        private Long id;
        private String name;
        private Double price;
        private String description;
        private String image;
        private List<String> imageUrls;
        private CategoryDTO category;
        private List<PriceHistoryDTO> priceHistory;

        public ProductDTO(Product product) {
            this.id = product.getId();
            this.name = product.getName();
            this.price = product.getPrice();
            this.description = product.getDescription();
            this.image = product.getImage();

            if (product.getGalleries() != null) {
                this.imageUrls = product.getGalleries().stream()
                        .map(ProductGallery::getImageUrl)
                        .collect(Collectors.toList());
            }

            if (product.getCategory() != null) {
                this.category = new CategoryDTO(product.getCategory());
            }

            if (product.getPriceHistory() != null) {
                this.priceHistory = IntStream.range(0, product.getPriceHistory().size())
                        .mapToObj(i -> new PriceHistoryDTO(product.getPriceHistory().get(i), i + 1))
                        .collect(Collectors.toList());
            }
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public Double getPrice() { return price; }
        public String getDescription() { return description; }
        public String getImage() { return image; }
        public List<String> getImageUrls() { return imageUrls; }
        public CategoryDTO getCategory() { return category; }
        public List<PriceHistoryDTO> getPriceHistory() { return priceHistory; }
    }

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

    // =================================================================================
    // API ENDPOINTS
    // =================================================================================

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
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    // =================================================================================
    // CREATE PRODUCT (Hỗ trợ nhiều ảnh, xử lý tên file an toàn)
    // =================================================================================
    @PostMapping(consumes = {"multipart/form-data"})
    public Product create(
            @RequestPart("product") Product product,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {

        if (product.getCategory() != null && product.getCategory().getId() != null) {
            ProductCategory category = productCategoryRepository
                    .findById(product.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        if (images != null && !images.isEmpty()) {
            Files.createDirectories(Paths.get(uploadFolder));

            List<ProductGallery> galleries = new ArrayList<>();
            for (MultipartFile file : images) {
                // ✅ Làm sạch tên file để tránh lỗi URL (xóa ký tự đặc biệt, khoảng trắng)
                String originalName = file.getOriginalFilename()
                        .replaceAll("[^a-zA-Z0-9._-]", "_");

                String fileName = UUID.randomUUID().toString() + "-" + originalName;
                Path filePath = Paths.get(uploadFolder, fileName);
                file.transferTo(filePath.toFile());

                ProductGallery gallery = new ProductGallery();
                gallery.setImageUrl(fileName);
                gallery.setProduct(product);
                galleries.add(gallery);
            }

            if (!galleries.isEmpty()) {
                product.setImage(galleries.get(0).getImageUrl());
            }
            product.setGalleries(galleries);
        }

        return productService.save(product);
    }

    // =================================================================================
    // UPDATE PRODUCT
    // =================================================================================
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product update(
            @PathVariable Long id,
            @RequestPart("product") Product updatedProduct,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        return productService.update(id, updatedProduct, images);
    }

    // =================================================================================
    // DELETE PRODUCT
    // =================================================================================
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    // =================================================================================
    // PRICE HISTORY
    // =================================================================================
    @GetMapping("/{id}/price-history")
    public List<PriceHistoryDTO> getPriceHistory(@PathVariable Long id) {
        List<ProductPrice> prices = productPriceService.getByProductId(id);
        return IntStream.range(0, prices.size())
                .mapToObj(i -> new PriceHistoryDTO(prices.get(i), i + 1))
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/price-history")
    public PriceHistoryDTO addPrice(@PathVariable Long id, @RequestParam Double price) {
        ProductPrice newPrice = productPriceService.addNewPrice(id, price);
        return new PriceHistoryDTO(newPrice, 1);
    }

    // =================================================================================
    // TRẢ VỀ FILE ẢNH THỰC TẾ
    // =================================================================================
    @GetMapping("/image/{filename}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable String filename) throws IOException {
        Path path = Paths.get(uploadFolder, filename);
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        byte[] imageBytes = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(imageBytes);
    }

    // =================================================================================
    // ĐẶT ẢNH ĐẠI DIỆN CHÍNH
    // =================================================================================
    @PutMapping("/{productId}/set-main-image/{galleryId}")
    public ResponseEntity<String> setMainImage(
            @PathVariable Long productId,
            @PathVariable Long galleryId) {
        productService.setMainImage(productId, galleryId);
        return ResponseEntity.ok("Cập nhật ảnh đại diện thành công!");
    }
}
