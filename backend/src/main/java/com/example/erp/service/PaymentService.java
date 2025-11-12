package com.example.erp.service;
import com.example.erp.repository.PaymentRepository;

import com.example.erp.entity.Payment;
import com.example.erp.repository.InvoiceRepository;
import com.example.erp.util.CodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PaymentService {
    @Autowired private InvoiceRepository invoiceRepo;
    @Autowired private com.example.erp.repository.PaymentRepository paymentRepo;

    public Payment pay(Long invoiceId, double amount, String method) {
        var invoice = invoiceRepo.findById(invoiceId).orElseThrow();
        Payment p = new Payment();
        p.setInvoice(invoice);
        p.setPaymentCode(CodeGenerator.generateCode("PAY"));
        p.setAmount(amount);
        p.setMethod(method);
        p.setPaymentDate(LocalDateTime.now());
        return paymentRepo.save(p);
    }
}
