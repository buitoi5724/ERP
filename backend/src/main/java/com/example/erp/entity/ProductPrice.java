package com.example.erp.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.example.erp.util.BaseEntity;

@Entity
@Table(name = "product_price")
public class ProductPrice extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double price; // Giá sản phẩm tại một thời điểm

    @Column(nullable = false)
    private LocalDateTime startDate; // Ngày bắt đầu áp dụng giá

    private LocalDateTime endDate;   // Ngày kết thúc (nếu null thì giá vẫn còn hiệu lực)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Ngày giờ bản ghi được tạo

    // --- Chỉ lưu productId thay vì liên kết Product entity ---
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // --- Lifecycle callback ---
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // --- Helper method ---
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return (endDate == null || endDate.isAfter(now))
                && (startDate.isBefore(now) || startDate.isEqual(now));
    }
}
