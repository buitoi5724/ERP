package com.example.erp.service;

import com.example.erp.dto.InvoiceDTO;
import com.example.erp.dto.InventoryDTO;
import com.example.erp.entity.Invoice;
import com.example.erp.entity.Payment;
import com.example.erp.repository.InvoiceRepository;
import com.example.erp.repository.PaymentRepository;
import com.example.erp.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {


@Autowired
private InvoiceRepository invoiceRepo;

@Autowired
private PaymentRepository paymentRepo;

// ================== Thanh toán theo invoiceId ==================
@Transactional
public void payInvoice(Long invoiceId, String paymentMethod, Long accountId) throws Exception {

    Invoice invoice = invoiceRepo.findById(invoiceId)
            .orElseThrow(() -> new Exception("Invoice không tồn tại"));

    // === Kiểm tra đã thanh toán chưa ===
    boolean isPaid = paymentRepo.existsByInvoice_Id(invoiceId);
    if (isPaid) {
        throw new Exception("Invoice đã được thanh toán");
    }

    // === Tạo Payment mới ===
    Payment payment = new Payment();
    payment.setInvoice(invoice);
    payment.setAmount(invoice.getTotalAmount());
    payment.setMethod(paymentMethod);
    payment.setPaymentDate(LocalDateTime.now());
    payment.setStatus("PAID");
    payment.setPaymentCode("PAY-" + System.currentTimeMillis());
    payment.setAccountId(accountId);

    paymentRepo.save(payment);

    // === Cập nhật invoice ===
    invoice.setPaymentMethod(paymentMethod);  // chỉ lưu thông tin
    invoice.setStatus("DONE");
    invoiceRepo.save(invoice);

    // === Cập nhật order ===
    if (invoice.getOrder() != null) {
        invoice.getOrder().setStatus(OrderStatus.DONE);
    }
}

// ================== Lấy invoice theo orderId ==================
public Invoice getByOrderId(Long orderId) {
    return invoiceRepo.findByOrder_Id(orderId).orElse(null);
}

// ================== Chuyển Invoice sang DTO ==================
public InvoiceDTO convertToDTO(Invoice invoice) {
    if(invoice == null) return null;

    InvoiceDTO dto = new InvoiceDTO();
    dto.setId(invoice.getId());

    if(invoice.getOrder() != null){
        dto.setOrderId(invoice.getOrder().getId());
        dto.setOrderCode(invoice.getOrder().getCode());
        dto.setOrderDate(invoice.getOrder().getOrderDate() != null ? invoice.getOrder().getOrderDate().toString() : null);
        dto.setAmount(invoice.getTotalAmount());
        dto.setCustomerId(invoice.getOrder().getCustomerId());
        dto.setPhone(invoice.getOrder().getPhone());
        dto.setSubtotal(invoice.getOrder().getSubtotal());
        dto.setTax(invoice.getOrder().getTax());
        dto.setShippingFee(invoice.getOrder().getShippingFee());
        dto.setDiscount(invoice.getOrder().getDiscount());
        dto.setPaymentMethod(invoice.getPaymentMethod());
        dto.setNote(invoice.getOrder().getNote());

        // === Map danh sách sản phẩm ===
        if(invoice.getOrder().getItems() != null){
            List<InventoryDTO> items = invoice.getOrder().getItems().stream().map(item -> {
                InventoryDTO i = new InventoryDTO();
                i.setProductId(item.getProductId());
                i.setProductName(item.getProductName());
                i.setPrice(item.getPrice());
                i.setQuantity(item.getQuantity());
                return i;
            }).collect(Collectors.toList());
            dto.setItems(items);
        }
    }

    return dto;
}

// ================== Các phương thức CRUD cơ bản ==================
public List<Invoice> getAll() {
    return invoiceRepo.findAll();
}

public Invoice getInvoiceById(Long id) {
    return invoiceRepo.findById(id).orElse(null);
}

public Invoice createInvoice(Invoice invoice) {
    return invoiceRepo.save(invoice);
}


}
