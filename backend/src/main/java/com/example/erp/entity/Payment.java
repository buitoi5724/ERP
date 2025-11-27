package com.example.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

// Mã payment
private String code;

// Số tiền thanh toán
private double amount;
private Long accountId;  // ai thanh toán
// Ngày thanh toán
private LocalDateTime paymentDate;

// Phương thức thanh toán: cash / bank
private String method;

// Liên kết với Invoice
@ManyToOne
@JoinColumn(name = "invoice_id")
private Invoice invoice;
@Column(name = "payment_code")
private String paymentCode;

public String getPaymentCode() { return paymentCode; }
public void setPaymentCode(String paymentCode) { this.paymentCode = paymentCode; }
// ================= Getters & Setters =================

public Long getId() { return id; }
/**
 * @return the accountId
 */
public Long getAccountId() {
	return accountId;
}
/**
 * @param accountId the accountId to set
 */
public void setAccountId(Long accountId) {
	this.accountId = accountId;
}
public void setId(Long id) { this.id = id; }

public String getCode() { return code; }
public void setCode(String code) { this.code = code; }

public double getAmount() { return amount; }
public void setAmount(double amount) { this.amount = amount; }

public LocalDateTime getPaymentDate() { return paymentDate; }
public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

public String getMethod() { return method; }
public void setMethod(String method) { this.method = method; }

public Invoice getInvoice() { return invoice; }
public void setInvoice(Invoice invoice) { this.invoice = invoice; }

}