package com.example.erp.dto;

public class OrderItemDTO {
    
    private Long productId;
    private String productName;
    private int quantity;
    private double price;

    // Liên kết với Order/Invoice
    private Long orderId;    // Thuộc Order nào
    private Long invoiceId;  // Thuộc Invoice nào (nếu đã tạo hóa đơn)

    public OrderItemDTO() {
    }

    public OrderItemDTO(Long productId, String productName, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItemDTO(Long productId, String productName, int quantity, double price, Long orderId, Long invoiceId) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.orderId = orderId;
        this.invoiceId = invoiceId;
    }
    private Long id; // id của OrderItem

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    // === Getter & Setter ===
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

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
}
