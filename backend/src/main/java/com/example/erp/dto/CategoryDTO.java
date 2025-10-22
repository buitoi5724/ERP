package com.example.erp.dto;

import com.example.erp.entity.ProductCategory;

public class CategoryDTO {
    private Long id;
    private String name;

    public CategoryDTO(ProductCategory category) {
        this.id = category.getId();
        this.name = category.getName();
    }
    public Long getId() { return id; }
    public String getName() { return name; }
}