package com.example.erp.dto;

import java.time.LocalDateTime;

public class PaymentDTO {

private Long id;
private String code;
private double amount;
private LocalDateTime paymentDate;
private String method;

// Getters & Setters
public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public String getCode() { return code; }
public void setCode(String code) { this.code = code; }

public double getAmount() { return amount; }
public void setAmount(double amount) { this.amount = amount; }

public LocalDateTime getPaymentDate() { return paymentDate; }
public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

public String getMethod() { return method; }
public void setMethod(String method) { this.method = method; }

}