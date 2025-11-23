package com.example.erp.entity;

import com.example.erp.util.OrderStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;  // DOING / DONE

    // --- Thông tin khách hàng ---
    private Long customerId;   
    private Long accountId;    
    private String phone;

    // --- Thông tin thanh toán ---
    private Double subtotal;
    private Double tax;
    private Double shippingFee;
    private Double discount;
    private String paymentMethod;

    @Column(columnDefinition = "NVARCHAR(M255)")
    private String note;

    // --- Danh sách OrderItem ---
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    // --- Danh sách Invoice ---
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Invoice> invoices = new ArrayList<>();

    // ================== Getters & Setters ==================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getTax() { return tax; }
    public void setTax(Double tax) { this.tax = tax; }

    public Double getShippingFee() { return shippingFee; }
    public void setShippingFee(Double shippingFee) { this.shippingFee = shippingFee; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public List<Invoice> getInvoices() { return invoices; }
    public void addInvoice(Invoice invoice) {
        invoices.add(invoice);
        invoice.setOrder(this);
    }
}
