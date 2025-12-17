package com.example.erp.entity;

import com.example.erp.util.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
public class Invoice extends BaseEntity {

    @Column(nullable = false, length = 50, unique = true)
    private String code; // mã hóa đơn

    @Column(nullable = false)
    private Long customerId; // ID khách hàng

    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(length = 20)
    private String status = "PENDING"; // PENDING, PAID, CANCELLED

    @Column(length = 20)
    private String paymentMethod; // CASH, CARD, TRANSFER

    // constructor
    public Invoice() {}

    // ===== GETTERS / SETTERS =====
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    // id đã có sẵn từ BaseEntity
}
