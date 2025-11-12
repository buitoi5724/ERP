// File: src/main/java/com/example/erp/entity/Order.java
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

    // --- Thông tin cơ bản về đơn hàng ---
    @Column(unique = true, nullable = false)
    private String code;
    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // --- Thông tin khách hàng ---
    private String customerName;
    private String phone;
    private String email;
    @Column(columnDefinition = "NVARCHAR(MAX)") // Cho địa chỉ dài
    private String address;

    // --- Thông tin thanh toán (ĐÃ SỬA) ---
    // Đã đổi sang 'Double' (lớp bao bọc) để cho phép CSDL chấp nhận giá trị NULL
    private Double subtotal; // Tiền hàng
    private Double tax; // Thuế
    private Double shippingFee; // Phí ship
    private Double discount; // Giảm giá
    
    // Đã đổi 'totalAmount' sang Double cho an toàn và nhất quán
    private Double totalAmount; 

    private String paymentMethod; // Phương thức thanh toán

    // --- Thông tin khác ---
    @Column(columnDefinition = "NVARCHAR(M255)") // Cho ghi chú dài
    private String note;
    private String createdBy; // Tên nhân viên tạo đơn
    

    // --- Danh sách sản phẩm ---
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    // === GETTERS AND SETTERS ===
    // (Đã sửa lại toàn bộ cho chính xác)

    public Long getId() {
        return id; // Đã sửa lỗi
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    // --- Đã sửa Getters/Setters cho khớp với 'Double' ---
    
    public Double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getTax() {
        return tax;
    }
    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getShippingFee() {
        return shippingFee;
    }
    public void setShippingFee(Double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public Double getDiscount() {
        return discount;
    }
    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    // --- Các trường còn lại ---

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<OrderItem> getItems() {
        return items;
    }
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    // Helper: add item
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}