package com.example.erp.entity;

import com.example.erp.util.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false)
    private Long customerId;

    // ✅ KHO BẮT BUỘC – CÓ DEFAULT ĐỂ MIGRATE DB
    @Column(nullable = false, length = 50, columnDefinition = "varchar(50) default 'DEFAULT'")
    private String warehouse;

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(length = 20)
    private String status = "PENDING";
    // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

    @Column(length = 20)
    private String paymentMethod;
    // CASH, CARD, TRANSFER

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    public Order() {}

    // ===== GETTERS / SETTERS =====

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getWarehouse() {
        return warehouse;
    }
    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderItem> getItems() {
        return items;
    }
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
