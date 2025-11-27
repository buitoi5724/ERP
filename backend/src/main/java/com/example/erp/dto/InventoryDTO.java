package com.example.erp.dto;

public class InventoryDTO {
    private String productName;
    private int quantity;
    private double price;

    private Long orderId;
    private Long invoiceId;

    // Constructor mặc định
    public InventoryDTO() {}
    private Long productId;
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public InventoryDTO(String productName, int quantity, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public InventoryDTO(String productName, int quantity, double price, Long orderId, Long invoiceId) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.orderId = orderId;
        this.invoiceId = invoiceId;
    }

    // === Getter & Setter ===
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

	public void setTotal(double d) {
		// TODO Auto-generated method stub
		
	}
}