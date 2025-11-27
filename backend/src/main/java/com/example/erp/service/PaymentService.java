package com.example.erp.service;

import com.example.erp.entity.Invoice;
import com.example.erp.entity.Payment;
import com.example.erp.repository.InvoiceRepository;
import com.example.erp.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private InventoryTransactionService inventoryTransactionService;

    /**
     * Thanh toán invoice
     * @param invoiceId ID hóa đơn
     * @param method phương thức thanh toán
     * @param accountId ID người thanh toán
     */
    @Autowired
    private InventoryTransactionService inventoryService;  // <- phải có
    @Transactional
    public Payment payInvoice(Long invoiceId, String method, Long accountId) {

        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice không tồn tại"));

        if (invoice.isPaid())
            throw new RuntimeException("Invoice đã thanh toán");

        // Trừ kho 1 lần
        inventoryService.decreaseStock(invoice.getOrder().getItems());

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(invoice.getTotalAmount());
        payment.setMethod(method);
        payment.setAccountId(accountId);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentCode("PAY-" + System.currentTimeMillis());
        Payment savedPayment = paymentRepo.save(payment);

        // Đánh dấu invoice đã thanh toán
        invoice.setPaid(true);
        invoiceRepo.save(invoice);

        return savedPayment;
    }

    @Transactional
    public void refundInvoice(Long invoiceId) {

        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice không tồn tại"));

        if (!invoice.isPaid())
            throw new RuntimeException("Invoice chưa thanh toán");

        // Cộng kho khi hoàn trả
        inventoryTransactionService.increaseStock(invoice.getOrder().getItems());

        // Đánh dấu invoice chưa thanh toán
        invoice.setPaid(false);
        invoiceRepo.save(invoice);
    }
}
