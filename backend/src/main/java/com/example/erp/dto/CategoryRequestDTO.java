package com.example.erp.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoryRequestDTO {

    @NotBlank
    private String name;

    private String description;

    // ⚡ Thêm productType để frontend gửi loại sản phẩm khi tạo mới
    @NotBlank
    private String productType;

    /* GETTERS / SETTERS */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
