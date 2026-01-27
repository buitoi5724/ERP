package com.example.erp.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.erp.util.OrderStatus;
import com.example.erp.util.PaymentMethod;
public class OrderResponseDTO {

    private Long id;
    private String code;
    private Long customerId;
    private OrderStatus status; // ✅ enum
    private PaymentMethod paymentMethod; // ✅ enum
    private LocalDateTime createdDate;
    
    
    
    private List<OrderItemDTO> items;

    public OrderStatus getStatus() {
        return status;
    }
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
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
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}
	/**
	 * @param paymentMethod the paymentMethod to set
	 */
	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	/**
	 * @return the createdDate
	 */
	public LocalDateTime getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * @return the items
	 */
	public List<OrderItemDTO> getItems() {
		return items;
	}
	/**
	 * @param items the items to set
	 */
	public void setItems(List<OrderItemDTO> items) {
		this.items = items;
	}
    
    
    
}