package com.example.erp.dto;

import java.util.List;

import com.example.erp.util.PaymentMethod;

public class OrderRequestDTO {

    private Long customerId;
    private PaymentMethod paymentMethod; // ✅ enum
    private String warehouse;
    private List<OrderItemRequestDTO> items;
    
    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getWarehouse() {
        return warehouse;
    }
    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }
	/**
	 * @return the items
	 */
	public List<OrderItemRequestDTO> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(List<OrderItemRequestDTO> items) {
		this.items = items;
	}

 
}
