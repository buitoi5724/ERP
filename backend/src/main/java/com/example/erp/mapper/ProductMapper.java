package com.example.erp.mapper;

import java.io.File;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.erp.dto.ProductRequestDTO;
import com.example.erp.dto.ProductResponseDTO;
import com.example.erp.entity.Category;
import com.example.erp.entity.Product;
import com.example.erp.entity.ProductGallery;

@Component
public class ProductMapper {

    /**
     * DTO → Entity (CREATE)
     */
    public Product toEntity(ProductRequestDTO dto, Category category) {
        Product product = new Product();
        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setImage(dto.getImage());
        product.setDescription(dto.getDescription());
        product.setSizes(dto.getSizes());
        product.setColors(dto.getColors());
        product.setUnit(dto.getUnit());
        product.setStatus(dto.getStatus());
        product.setCategory(category);
        return product;
    }

    /**
     * DTO → Entity (UPDATE)
     */
    public void updateEntity(Product product, ProductRequestDTO dto, Category category) {

        if (dto.getName() != null) {
            product.setName(dto.getName());
        }

        // ✅ FIX LỖI BigDecimal (KHÔNG BAO GIỜ dùng isEmpty)
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }

        if (dto.getImage() != null) {
            product.setImage(dto.getImage());
        }

        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }

        if (dto.getSizes() != null) {
            product.setSizes(dto.getSizes());
        }

        if (dto.getColors() != null) {
            product.setColors(dto.getColors());
        }

        if (dto.getUnit() != null) {
            product.setUnit(dto.getUnit());
        }

        if (dto.getStatus() != null) {
            product.setStatus(dto.getStatus());
        }

        if (category != null) {
            product.setCategory(category);
        }
    }


    /**
     * Entity → Response DTO
     */
    public ProductResponseDTO toResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();

        dto.setId(product.getId());
        dto.setCode(product.getCode());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setSizes(product.getSizes());
        dto.setColors(product.getColors());
        dto.setUnit(product.getUnit());

        // ✅ STATUS
        dto.setStatus(product.getStatus());

        // ✅ CATEGORY
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        // ✅ DATE
        dto.setCreatedDate(product.getCreatedDate());
        dto.setUpdatedDate(product.getUpdatedDate());

        // ✅ IMAGE GALLERY (LỖI CỦA ANH)
        if (product.getGalleries() != null && !product.getGalleries().isEmpty()) {
            dto.setImageUrls(
                product.getGalleries().stream()
                    .map(g -> "/uploads/" + g.getFileName())
                    .toList()
            );

            // ✅ ảnh đại diện = ảnh đầu tiên
            dto.setImage(dto.getImageUrls().get(0));
        }

        return dto;
    }

    /**
     * Thêm ảnh sản phẩm từ MultipartFile[]
     */
    public void addImages(Product product, MultipartFile[] images) {
        if (images == null || images.length == 0) return;

        for (MultipartFile file : images) {
            ProductGallery gallery = new ProductGallery();
            gallery.setFileName(file.getOriginalFilename());
            try {
            } catch (Exception e) {
                throw new RuntimeException("Failed to read image data", e);
            }

            product.addGallery(gallery); // dùng helper method để tránh NullPointerException
        }
    }
    public void addImages(Product product, MultipartFile[] images, String uploadFolder) {
        if (images == null || images.length == 0) return;

        File folder = new File(uploadFolder);
        if (!folder.exists()) folder.mkdirs();

        for (MultipartFile file : images) {
            try {
                // Tạo tên file duy nhất
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File dest = new File(folder, fileName);

                // Lưu file xuống ổ đĩa
                file.transferTo(dest);

                // Set ảnh đại diện nếu chưa có
                if (product.getImage() == null || product.getImage().isEmpty()) {
                    product.setImage(fileName);
                }

                // Thêm vào gallery nếu bạn dùng ProductGallery
                ProductGallery gallery = new ProductGallery();
                gallery.setFileName(fileName);
                gallery.setFilePath(uploadFolder + fileName); // lưu đường dẫn tương đối hoặc tuyệt đối
                product.addGallery(gallery);

            } catch (Exception e) {
                throw new RuntimeException("Failed to save image file", e);
            }
        }
    }
}
