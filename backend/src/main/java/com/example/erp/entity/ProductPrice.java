package com.example.erp.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity // Đây là một entity JPA (map với bảng trong DB)
@Table(name = "product_price") // Map tới bảng product_price
public class ProductPrice implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    // Khóa chính, auto-increment trong DB
    private Long id;

    @Column(nullable = false)
    private Double price; // Giá sản phẩm tại một thời điểm

    @Column(nullable = false)
    private LocalDateTime startDate; // Ngày bắt đầu áp dụng giá

    private LocalDateTime endDate;   // Ngày kết thúc (nếu null thì giá vẫn còn hiệu lực)

    @Column(nullable = false, updatable = false) 
    // Không cho cập nhật sau khi tạo
    private LocalDateTime createdAt; // Ngày giờ bản ghi được tạo

    @ManyToOne(fetch = FetchType.LAZY) // Nhiều giá thuộc về 1 sản phẩm
    @JoinColumn(name = "product_id", nullable = false) 
    // Khóa ngoại liên kết tới bảng product
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    // Tránh vòng lặp vô hạn khi convert sang JSON
    private Product product;

    // --- Lifecycle callback ---
    // Hàm này sẽ tự động chạy trước khi persist (insert vào DB)
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

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // --- Helper method ---
    // Xác định giá này có còn hiệu lực hay không
    public boolean isActive() {
        return (endDate == null || endDate.isAfter(LocalDateTime.now())) // chưa hết hạn
               && (startDate.isBefore(LocalDateTime.now()) || startDate.isEqual(LocalDateTime.now())); // đã bắt đầu
    }
}
