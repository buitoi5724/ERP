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

    /**
     * Thanh toán invoice
     * @param invoiceId ID của hóa đơn cần thanh toán
     * @param method Phương thức thanh toán (ví dụ: CASH, CARD)
     * @param accountId ID người thực hiện thanh toán
     * @return Payment đã lưu
     */
    @PostMapping("/pay")
    public ResponseEntity<Payment> payInvoice(
            @RequestParam Long invoiceId,
            @RequestParam String method,
            @RequestParam Long accountId) {

        Payment payment = paymentService.payInvoice(invoiceId, method, accountId);
        return ResponseEntity.ok(payment);
    }
    /**
     * Hoàn trả invoice (refund)
     * @param invoiceId ID của hóa đơn cần refund
     * @return ResponseEntity với thông báo thành công
     */
    @PostMapping("/refund")
    public ResponseEntity<String> refundInvoice(@RequestParam Long invoiceId) {
        paymentService.refundInvoice(invoiceId);
        return ResponseEntity.ok("Hoàn trả thành công cho invoice " + invoiceId);
    }
}
