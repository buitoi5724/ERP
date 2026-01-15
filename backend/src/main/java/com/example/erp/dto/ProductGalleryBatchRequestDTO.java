package com.example.erp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ProductGalleryBatchRequestDTO {

    @NotNull
    private Long productId;

    @NotEmpty
    private List<String> imageUrls;

    /* GETTERS / SETTERS */
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
}
