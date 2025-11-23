package com.example.erp.dto;

import java.util.List;

public class OrderDTO {
    
    private Long id;                // ID Order

    // Thông tin cơ bản
    private String orderCode;
    private String orderDate;
    private String status;
    private String createdBy;
    private String note;

    // Thông tin khách hàng
    private String customerName;
    private String phone;

    // Thông tin thanh toán
    private Double subtotal;
    private Double tax;
    private Double shippingFee;
    private Double discount;
    private Double totalAmount;
    private String paymentMethod;
    
    // Danh sách sản phẩm
    private List<OrderItemDTO> items;

    // Thông tin hóa đơn liên quan (Invoice)
    private InvoiceDTO invoice; // liên kết 1-1 với Order

    // === Getters & Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

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

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }

    public InvoiceDTO getInvoice() { return invoice; }
    public void setInvoice(InvoiceDTO invoice) { this.invoice = invoice; }
}
