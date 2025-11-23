package com.example.erp.service;

import com.example.erp.entity.Invoice;
import com.example.erp.entity.Payment;
import com.example.erp.repository.InvoiceRepository;
import com.example.erp.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private InvoiceRepository invoiceRepo;

    public Payment payInvoice(Long invoiceId, String method, Long accountId) {
        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice không tồn tại với ID: " + invoiceId));

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setMethod(method);
        payment.setAmount(invoice.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentCode("PAY-" + System.currentTimeMillis());

        // Lưu Payment
        return paymentRepo.save(payment);
    }
}
