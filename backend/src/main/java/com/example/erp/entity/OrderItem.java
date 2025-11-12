// File: src/main/java/com/example/erp/entity/OrderItem.java
package com.example.erp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items") 
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thông tin sản phẩm (lưu trữ tại thời điểm mua)
    @Column(nullable = false)
    private Long productId; // SỬA TỪ String THÀNH Long

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String productName; 
    
    @Column(nullable = false)
    private int quantity; 
    
    @Column(nullable = false)
    private double price; // (Cái này là double nguyên thủy cũng được,
                         // vì nó sẽ không bao giờ null khi tạo)

    // Liên kết ngược về Order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // === GETTERS AND SETTERS ===

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // --- ĐÃ SỬA KIỂU DỮ LIỆU ---
    public Long getProductId() { 
        return productId; 
    }
    public void setProductId(Long productId) { 
        this.productId = productId; 
    }
    // ---------------------------

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}