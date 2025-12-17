package com.example.erp.dto;

public class ProductGalleryResponseDTO {

    private Long id;
    private String imageUrl;
    private Long productId;

    /* GETTERS / SETTERS */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
}
