package com.example.erp.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.erp.dto.ProductGalleryBatchRequestDTO;
import com.example.erp.dto.ProductGalleryResponseDTO;

public interface ProductGalleryService {

    List<ProductGalleryResponseDTO> createBatch(ProductGalleryBatchRequestDTO dto);

    List<ProductGalleryResponseDTO> getByProductId(Long productId);

    void setMainImage(Long productId, String imageUrl);

    void removeMainImage(Long productId);

    void deleteImage(Long imageId);
    void uploadMainImage(Long productId, MultipartFile file);
}
