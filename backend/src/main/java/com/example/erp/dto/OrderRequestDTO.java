package com.example.erp.dto;

import java.util.List;

public class OrderRequestDTO {

    private Long customerId;
    private String paymentMethod;
    private String warehouse; // ✅ THÊM
    private List<OrderItemDTO> items;

    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getWarehouse() {          // ✅ THÊM
        return warehouse;
    }
    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }
    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
    
    
    
}
