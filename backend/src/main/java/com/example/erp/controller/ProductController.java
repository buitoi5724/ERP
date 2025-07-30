package com.example.erp.controller;

import com.example.erp.entity.Product;
import com.example.erp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Cho phép tất cả nguồn gọi API
public class ProductController {

    @Autowired
    private ProductService productService;

    @Value("${upload.folder}")
    private String uploadFolder;

    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Product> getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public Product create(
            @RequestPart("product") Product product,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        if (image != null && !image.isEmpty()) {
            // Tạo tên file ngẫu nhiên để tránh trùng lặp
            String originalFileName = image.getOriginalFilename();
            String fileExtension = "";

            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID().toString() + fileExtension;
            Path uploadPath = Paths.get(uploadFolder, fileName);

            // Đảm bảo thư mục tồn tại
            Files.createDirectories(uploadPath.getParent());
            image.transferTo(uploadPath.toFile());

            // Đường dẫn frontend dùng
            product.setImage("/uploads/" + fileName);
        }

        return productService.save(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return productService.save(product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
