package com.example.erp.controller;

import com.example.erp.dto.InvoiceDTO;
import com.example.erp.entity.Invoice;
import com.example.erp.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "http://localhost:3000")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    // === Lấy tất cả hóa đơn ===
    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAll();
        List<InvoiceDTO> dtos = invoices.stream()
                .map(invoiceService::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // === Lấy hóa đơn theo ID ===
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        if (invoice == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(invoiceService.convertToDTO(invoice));
    }

    // === Tạo hóa đơn mới (nếu cần) ===
    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody Invoice invoice) {
        Invoice newInvoice = invoiceService.createInvoice(invoice);
        return ResponseEntity.ok(invoiceService.convertToDTO(newInvoice));
    }

    // === Lấy hóa đơn theo OrderId (liên kết) ===
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<InvoiceDTO> getInvoiceByOrder(@PathVariable Long orderId) {
        Invoice invoice = invoiceService.getByOrderId(orderId);
        if (invoice == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(invoiceService.convertToDTO(invoice));
    }

    // === Thanh toán invoice (rút gọn) ===
    @PostMapping("/pay")
    public ResponseEntity<String> payInvoice(@RequestBody PayInvoiceRequest request) {
    	  System.out.println("Frontend gửi invoiceId: " + request.getInvoiceId());
        try {
            invoiceService.payInvoice(
                    request.getInvoiceId(), 
                    request.getPaymentMethod(), 
                    request.getAccountId()
            );
            return ResponseEntity.ok("Thanh toán hóa đơn thành công!");
        } catch (Exception e) {
        	   e.printStackTrace(); // <-- thêm dòng này để xem lỗi thực tế
            return ResponseEntity.badRequest().body("Lỗi thanh toán: " + e.getMessage());
        }
    }
}

// DTO rút gọn cho frontend chỉ gửi ID + phương thức thanh toán
class PayInvoiceRequest {
    private Long invoiceId;
    private String paymentMethod;
    private Long accountId;

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
}
