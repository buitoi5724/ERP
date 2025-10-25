package com.example.erp.service;

import com.example.erp.repository.ProductGalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductGalleryService {

    @Autowired
    private ProductGalleryRepository galleryRepo;

    public void deleteByProductId(Long productId) {
        galleryRepo.deleteByProductId(productId);
    }
}
