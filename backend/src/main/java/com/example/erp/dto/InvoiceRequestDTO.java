package com.example.erp.dto;

import java.math.BigDecimal;
import java.util.List;

public class InvoiceRequestDTO {

    private Long customerId;
    private String paymentMethod;
    private List<InvoiceItemDTO> items; // danh sách sản phẩm & số lượng
	/**
	 * @return the customerId
	 */
	public Long getCustomerId() {
		return customerId;
	}
	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	/**
	 * @return the paymentMethod
	 */
	public String getPaymentMethod() {
		return paymentMethod;
	}
	/**
	 * @param paymentMethod the paymentMethod to set
	 */
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	/**
	 * @return the items
	 */
	public List<InvoiceItemDTO> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(List<InvoiceItemDTO> items) {
		this.items = items;
	}

    // getters / setters
    
}
