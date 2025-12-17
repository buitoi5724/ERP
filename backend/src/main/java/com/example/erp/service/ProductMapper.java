package com.example.erp.service;

import org.springframework.stereotype.Component;
import com.example.erp.dto.ProductRequestDTO;
import com.example.erp.dto.ProductResponseDTO;
import com.example.erp.entity.Category;
import com.example.erp.entity.Product;

@Component("serviceProductMapper")  // ⚡ Đổi tên bean
public class ProductMapper {

    // DTO → Entity
    public Product toEntity(ProductRequestDTO dto, Category category) {
        Product p = new Product();
        p.setCode(dto.getCode());
        p.setName(dto.getName());
        p.setPrice(dto.getPrice());
        p.setDescription(dto.getDescription());
        p.setImage(dto.getImage());
        p.setSizes(dto.getSizes());
        p.setColors(dto.getColors());
        p.setUnit(dto.getUnit());
        p.setStatus(dto.getStatus());
        p.setCategory(category);
        return p;
    }

    // Update entity từ DTO
    public void updateEntity(Product p, ProductRequestDTO dto, Category category) {
        p.setCode(dto.getCode());
        p.setName(dto.getName());
        p.setPrice(dto.getPrice());
        p.setDescription(dto.getDescription());
        p.setImage(dto.getImage());
        p.setSizes(dto.getSizes());
        p.setColors(dto.getColors());
        p.setUnit(dto.getUnit());
        p.setStatus(dto.getStatus());
        p.setCategory(category);
    }

    // Entity → ResponseDTO
    public ProductResponseDTO toResponseDTO(Product p) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(p.getId());
        dto.setCode(p.getCode());
        dto.setName(p.getName());
        dto.setPrice(p.getPrice());
        dto.setDescription(p.getDescription());
        dto.setImage(p.getImage());
        dto.setSizes(p.getSizes());
        dto.setColors(p.getColors());
        dto.setUnit(p.getUnit());
        dto.setStatus(p.getStatus());
        dto.setCategoryId(p.getCategory().getId());
        return dto;
    }
}
