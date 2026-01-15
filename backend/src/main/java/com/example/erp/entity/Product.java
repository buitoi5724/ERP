package com.example.erp.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.erp.util.BaseEntity;
import com.example.erp.util.ProductStatus;

/**
 * Product Entity
 */
@Entity
@Table(
    name = "product",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "code")
    }
)
public class Product extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /* ================= BASIC INFO ================= */

    @Column(nullable = false, length = 50, unique = true)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    private String image;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    /* ================= ATTRIBUTES ================= */
    private String sizes;
    private String colors;

    @Column(length = 50)
    private String unit;

    /* ================= STATUS ================= */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status = ProductStatus.ACTIVE;

    /* ================= CATEGORY ================= */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /* ================= GALLERIES ================= */
    @OneToMany(
        mappedBy = "product",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<ProductGallery> galleries = new ArrayList<>();

    /* ================= CONSTRUCTOR ================= */
    public Product() {
        // JPA & Mapper
    }

    /* ================= BUSINESS METHODS ================= */
    public void activate() { this.status = ProductStatus.ACTIVE; }
    public void deactivate() { this.status = ProductStatus.INACTIVE; }
    public void discontinue() { this.status = ProductStatus.DISCONTINUED; }

    /* ================= GETTERS / SETTERS ================= */


    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Price must be >= 0");
        }
        this.price = price;
    }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSizes() { return sizes; }
    public void setSizes(String sizes) { this.sizes = sizes; }

    public String getColors() { return colors; }
    public void setColors(String colors) { this.colors = colors; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) {
        if (status == null) throw new IllegalArgumentException("Status must not be null");
        this.status = status;
    }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<ProductGallery> getGalleries() { return galleries; }
    public void addGallery(ProductGallery gallery) {
        galleries.add(gallery);
        gallery.setProduct(this);
    }
}
