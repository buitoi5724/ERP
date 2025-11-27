// File: src/main/java/com/example/erp/dto/InvoiceDTO.java
package com.example.erp.dto;

import java.util.List;

public class InvoiceDTO {

    private Long id;

    // Thông tin Order liên kết
    private Long orderId;
    private String orderCode;
    private String orderDate; // String để format ngày dễ hơn
    private double amount;    // tổng cuối cùng

    private Long customerId;
    private String phone;

    private Double subtotal;
    private Double tax;
    private Double shippingFee;
    private Double discount;
    private String paymentMethod;
    private String note;
    private String paymentStatus;  // <-- thêm trường này
    // Danh sách sản phẩm trong hóa đơn
    private List<InventoryDTO> items;

    // === GETTER & SETTER ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    
    
    /**
	 * @return the paymentStatus
	 */
	public String getPaymentStatus() {
		return paymentStatus;
	}
	/**
	 * @param paymentStatus the paymentStatus to set
	 */
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

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

    public List<InventoryDTO> getItems() { return items; }
    public void setItems(List<InventoryDTO> items) { this.items = items; }
}