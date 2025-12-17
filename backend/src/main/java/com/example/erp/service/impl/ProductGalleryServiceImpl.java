package com.example.erp.service.impl;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.erp.dto.ProductGalleryBatchRequestDTO;
import com.example.erp.dto.ProductGalleryResponseDTO;
import com.example.erp.entity.Product;
import com.example.erp.entity.ProductGallery;
import com.example.erp.repository.ProductGalleryRepository;
import com.example.erp.repository.ProductRepository;
import com.example.erp.service.ProductGalleryService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ProductGalleryServiceImpl implements ProductGalleryService {

    private final ProductGalleryRepository galleryRepository;
    private final ProductRepository productRepository;

    private final String uploadFolder = "D:/uploads/product/";
    private final String publicUrlPrefix = "/uploads/product/";

    public ProductGalleryServiceImpl(ProductGalleryRepository galleryRepository,
                                     ProductRepository productRepository) {
        this.galleryRepository = galleryRepository;
        this.productRepository = productRepository;
    }

    /* ================= CREATE BATCH ================= */
    @Override
    public List<ProductGalleryResponseDTO> createBatch(ProductGalleryBatchRequestDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + dto.getProductId()));

        return dto.getImageUrls().stream()
                .map(url -> {
                    String fileName = url.substring(url.lastIndexOf("/") + 1);
                    ProductGallery gallery = new ProductGallery();
                    gallery.setFileName(fileName);
                    gallery.setFilePath(uploadFolder);
                    gallery.setProduct(product);

                    return toResponse(galleryRepository.save(gallery));
                })
                .collect(Collectors.toList());
    }

    /* ================= GET BY PRODUCT ================= */
    @Override
    @Transactional(readOnly = true)
    public List<ProductGalleryResponseDTO> getByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        return galleryRepository.findByProduct(product).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /* ================= UPLOAD / SET / REMOVE MAIN IMAGE ================= */
    @Override
    public void uploadMainImage(Long productId, MultipartFile file) {
        Product product = getProductOrThrow(productId);

        try {
            File folder = new File(uploadFolder);
            if (!folder.exists()) folder.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(folder, fileName);
            file.transferTo(dest);

            product.setImage(fileName);
            productRepository.save(product);

        } catch (Exception e) {
            throw new RuntimeException("Failed to save image file", e);
        }
    }

    @Override
    public void setMainImage(Long productId, String fileName) {
        Product product = getProductOrThrow(productId);
        product.setImage(fileName);
        productRepository.save(product);
    }

    @Override
    public void removeMainImage(Long productId) {
        Product product = getProductOrThrow(productId);
        product.setImage(null);
        productRepository.save(product);
    }

    /* ================= DELETE ================= */
    @Override
    public void deleteImage(Long imageId) {
        ProductGallery gallery = galleryRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Gallery image not found with id: " + imageId));

        galleryRepository.delete(gallery);
    }

    /* ================= HELPERS ================= */
    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));
    }

    private ProductGalleryResponseDTO toResponse(ProductGallery gallery) {
        ProductGalleryResponseDTO dto = new ProductGalleryResponseDTO();
        dto.setId(gallery.getId());
        dto.setProductId(gallery.getProduct().getId());
        dto.setImageUrl(publicUrlPrefix + gallery.getFileName());
        return dto;
    }
}
