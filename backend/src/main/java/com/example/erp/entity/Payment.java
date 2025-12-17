package com.example.erp.entity;

import com.example.erp.util.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class Payment extends BaseEntity {

    @Column(nullable = false)
    private Long invoiceId;  // Liên kết Invoice

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount; // Số tiền thanh toán

    @Column(length = 50, nullable = false)
    private String method;   // CASH, CARD, BANK_TRANSFER...

    @Column(length = 20, nullable = false)
    private String status;   // PAID, PENDING, FAILED

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(length = 255)
    private String note;

    // ======== Getter / Setter ========

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
