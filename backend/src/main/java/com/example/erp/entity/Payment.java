package com.example.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentCode;   // mã thanh toán
    private double amount;        // số tiền
    private String method;        // cash / bank
    private LocalDateTime paymentDate;
    private Long accountId;
    // Trạng thái thanh toán: PENDING / DONE
    private String status;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    
    
    /**
	 * @return the accountId
	 */
	public Long getAccountId() {
		return accountId;
	}
	// ==================== Getters & Setters ====================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentCode() { return paymentCode; }
    public void setPaymentCode(String paymentCode) { this.paymentCode = paymentCode; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
	public void setAccountId(Long accountId) {
		// TODO Auto-generated method stub
		
	}
}
