package com.example.erp.dto;

import java.util.List;

import com.example.erp.util.InvoiceType;
import com.example.erp.util.PaymentMethod;

public class InvoiceRequestDTO {

    /**
     * IMPORT  -> supplierId
     * EXPORT  -> customerId
     */
    private Long partnerId;

    private InvoiceType type; // IMPORT / EXPORT / RETURN

    private PaymentMethod paymentMethod;

    private List<InvoiceItemDTO> items;

    // ===== GETTERS / SETTERS =====

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public InvoiceType getType() {
        return type;
    }

    public void setType(InvoiceType type) {
        this.type = type;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<InvoiceItemDTO> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItemDTO> items) {
        this.items = items;
    }
}
