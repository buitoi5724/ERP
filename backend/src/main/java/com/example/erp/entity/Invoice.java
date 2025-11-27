package com.example.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.example.erp.util.InvoiceType;
import com.example.erp.util.InvoiceCategory;
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Enumerated(EnumType.STRING)
    private InvoiceType type;

    @Enumerated(EnumType.STRING)
    private InvoiceCategory category;

    private double totalAmount;

    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
 
    // Trạng thái thanh toán: DOING / DONE
    private String status;

    // Phương thức thanh toán: cash / bank
    private String paymentMethod;

    // Danh sách item trong hóa đơn
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<Payment> payments;

    // ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public InvoiceType getType() { return type; }
    public void setType(InvoiceType type) { this.type = type; }

    public InvoiceCategory getCategory() { return category; }
    public void setCategory(InvoiceCategory category) { this.category = category; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public List<Payment> getPayments() {
        return payments;
    }
    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }
}