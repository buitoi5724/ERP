package com.example.erp.controller;

import com.example.erp.entity.Payment;
import com.example.erp.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<Payment> payInvoice(@RequestParam Long invoiceId,
                                              @RequestParam String method,
                                              @RequestParam Long accountId) {
        Payment payment = paymentService.payInvoice(invoiceId, method, accountId);
        return ResponseEntity.ok(payment);
    }
}